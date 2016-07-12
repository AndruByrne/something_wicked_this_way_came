package com.anthropicandroid.sfcrimedata.module;

/*
 * Created by Andrew Brin on 7/10/2016.
 */

import android.support.annotation.NonNull;
import android.util.Log;
import android.util.Pair;

import com.anthropicandroid.sfcrimedata.services.DataStore;
import com.anthropicandroid.sfcrimedata.services.MarkerService;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;
import rx.functions.Action2;
import rx.functions.Func0;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.observables.ConnectableObservable;
import rx.observables.GroupedObservable;
import rx.schedulers.Schedulers;

@Module
public class MapMarkerModule {

    private static final List<String> markerColors = new ArrayList<>();
    public static final String TAG = MapMarkerModule.class.getSimpleName();

    static {
        markerColors.add("#eb3600");
        markerColors.add("#e54800");
        markerColors.add("#d86d00");
        markerColors.add("#d27f00");
        markerColors.add("#c5a300");
        markerColors.add("#b9c800");
        markerColors.add("#a6ff00");
    }

    @Provides
    @NaviActivityScope
    MarkerService getMarkerService(
            DataStore dataStore,
            @Named("NetworkOverwrite") Observable<Boolean> networkOverwrite) {
        return new MarkerService(dataStore, networkOverwrite);
    }

    @Provides
    @NaviActivityScope
    @Named("NetworkOverwrite")
    Observable<Boolean> networkOverwrite(
            NetworkModule.SFPDInterface sfpdInterface,
            final DataStore dataStore) {
        final Gson gson = new Gson();
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String nowHere = dateFormat.format(calendar.getTime()) + "T00:00:00";
        calendar.add(Calendar.MONTH, -1);
        String monthAgo = dateFormat.format(calendar.getTime()) + "T00:00:00";
        String queryString = "date between '" + nowHere + "' and '" + monthAgo + "'";
        Log.d(TAG, "query String: " + queryString);
        ConnectableObservable<Boolean> replay = sfpdInterface
                .getIncidents()
                .doOnNext(new Action1<List<JsonObject>>() {
                    @Override
                    public void call(List<JsonObject> jsonObjects) {
                        Log.d(TAG, "got response from network");
                    }
                })
                .flatMap(new Func1<List<JsonObject>, Observable<Boolean>>() {
                    @Override
                    public Observable<Boolean> call(List<JsonObject> jsonObjects) {
                        return dbUpdate(jsonObjects, dataStore);
                    }
                })
                .subscribeOn(Schedulers.io())
                .replay();
        replay.connect();
        return replay;
    }

    private Observable<Boolean> dbUpdate(
            final List<JsonObject> jsonList,
            final DataStore dataStore) {
        return Observable
                .range(0, jsonList.size()) //  iterate over list
                .map(new Func1<Integer, JsonObject>() {
                    @Override
                    public JsonObject call(Integer integer) {
                        return jsonList.get(integer);
                    }
                })
                .filter(getNullFilter())
                .groupBy(new Func1<JsonObject, String>() {
                    @Override
                    public String call(JsonObject jsonObject) {
                        return jsonObject.get("pddistrict").getAsString();
                    }
                })
                .filter(getNullFilter())
                .flatMap(new Func1<GroupedObservable<String, JsonObject>, Observable<Pair<String,
                        JsonObject>>>() {
                    @Override
                    public Observable<Pair<String, JsonObject>> call(
                            final GroupedObservable<String, JsonObject>
                                    incidentObservableByDistrict) {
                        return incidentObservableByDistrict
                                .map(new Func1<JsonObject, Pair<String, JsonObject>>() {
                                    @Override
                                    public Pair<String, JsonObject> call(JsonObject jsonObject) {
                                        return new Pair<>(
                                                incidentObservableByDistrict.getKey(),
                                                jsonObject);
                                    }
                                });
                    }
                })
                .filter(getNullFilter())
                .collect(
                        new Func0<Map<String, ArrayList<JsonObject>>>() {
                            @Override
                            public Map<String, ArrayList<JsonObject>> call() {
                                return new HashMap<>();
                            }
                        },
                        new Action2<Map<String, ArrayList<JsonObject>>, Pair<String, JsonObject>>
                                () {
                            @Override
                            public void call(
                                    Map<String, ArrayList<JsonObject>> map,
                                    Pair<String, JsonObject> stringJsonObjectPair) {
                                ArrayList<JsonObject> incidentList = map.get(stringJsonObjectPair
                                        .first);
                                if (incidentList == null) {
                                    incidentList = new ArrayList<>();
                                    map.put(stringJsonObjectPair.first, incidentList);
                                }
                                incidentList.add(stringJsonObjectPair.second);
                            }
                        }
                )
                .flatMap(new Func1<Map<String, ArrayList<JsonObject>>, Observable<Boolean>>() {
                    @Override
                    public Observable<Boolean> call(
                            final Map<String, ArrayList<JsonObject>> districtToIncidentsMapping) {
                        return Observable.zip(
                                getOrder(districtToIncidentsMapping),
                                Observable.from(markerColors),
                                markerCreateZipFunction(districtToIncidentsMapping, dataStore)
                        );
                    }
                })
                .reduce(false, new Func2<Boolean, Boolean, Boolean>() {
                    @Override
                    public Boolean call(Boolean accumulator, Boolean aBoolean) {
                        if(!accumulator) accumulator = aBoolean;
                        return accumulator;
                    }
                });
    }

    @NonNull
    private Func2<String, String, Boolean> markerCreateZipFunction(
            final Map<String, ArrayList<JsonObject>> districtToIncidentsMapping,
            final DataStore dataStore) {
        final Gson gson = new Gson();
        return new Func2<String, String, Boolean>() {
            @Override
            public Boolean call(final String district, final String color) {
                return Observable
                        .from(districtToIncidentsMapping.get(district))
                        .map(new Func1<JsonObject, JSONObject>() {
                            @Override
                            public JSONObject call(JsonObject jsonObject) {
                                String json = gson.toJson(convertToMarker(jsonObject, color));
                                try {
                                    return new JSONObject(json);
                                } catch (JSONException e) {
                                    Log.e(TAG, "Error creating JSON: " + e.getMessage());
                                    e.printStackTrace();
                                }
                                return null;
                            }
                        })
                        .filter(getNullFilter())
                        .toList()
                        .map(new Func1<List<JSONObject>, Boolean>() {
                            @Override
                            public Boolean call(List<JSONObject> markers) {
                                return dataStore
                                        .setMarkersForDistrict(
                                                markers,
                                                district).toBlocking()
                                        .first();
                            }
                        }).toBlocking().first();
            }
        };
    }

    private Observable<String> getOrder(
            final Map<String, ArrayList<JsonObject>>
                    stringArrayListMap) {
        final TreeMap<Integer, String> treeMap = new TreeMap<>();
        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                for (Map.Entry<String, ArrayList<JsonObject>> entry : stringArrayListMap.entrySet
                        ()) {
                    int proposedKey = entry.getValue().size();
                    while (treeMap.containsKey(proposedKey))
                        proposedKey--; // if even, one will be lower
                    treeMap.put(proposedKey, entry.getKey());
                }
                for (Integer count : treeMap.descendingKeySet())
                    subscriber.onNext(treeMap.get(count));
            }
        });
    }

    private JsonObject convertToMarker(JsonObject jsonObject, String color) {
        JsonObject marker = new JsonObject();
        marker.addProperty("type", "Feature");
        marker.add("geometry", getGeometry(jsonObject));
        marker.add("properties", getProperties(jsonObject, color));
        return marker;
    }

    private JsonObject getProperties(JsonObject jsonObject, String color) {
        JsonObject properties = new JsonObject();
        properties.addProperty("marker-color", color);
        return properties;
    }

    private JsonObject getGeometry(JsonObject jsonObject) {
        JsonObject geometry = new JsonObject();
        JsonArray coordinates = new JsonArray();
        coordinates.add(jsonObject.get("x"));
        coordinates.add(jsonObject.get("y"));
        geometry.addProperty("type", "Point");
        geometry.add(
                "coordinates",
                coordinates);
        return geometry;
    }

    @NonNull
    private Func1<Object, Boolean> getNullFilter() {
        return new Func1<Object, Boolean>() {
            @Override
            public Boolean call(Object jsonObject) {
                return jsonObject != null;
            }
        };
    }
}
