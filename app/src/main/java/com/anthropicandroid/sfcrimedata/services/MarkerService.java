package com.anthropicandroid.sfcrimedata.services;

/*
 * Created by Andrew Brin on 7/10/2016.
 */

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.functions.Action2;
import rx.functions.Func0;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class MarkerService {

    public static final String TAG = MarkerService.class.getSimpleName();
    private DataStore dataStore;
    private Observable<Boolean> networkOverwrite;

    public MarkerService(DataStore dataStore, Observable<Boolean> networkOverwrite) {
        this.dataStore = dataStore;
        this.networkOverwrite = networkOverwrite;
    }

    public Observable<List<JSONObject>> getMarkersForDistrict(final String district) {
        return Observable
                .merge(
                        dataStore.getMarkersForDistrict(district),
                        networkOverwrite
                                .flatMap(new Func1<Boolean, Observable<List<JSONObject>>>() {
                                    @Override
                                    public Observable<List<JSONObject>> call(Boolean aBoolean) {
                                        return aBoolean
                                                ? dataStore.getMarkersForDistrict(district)
                                                : Observable.<List<JSONObject>>empty();
                                    }
                                }))
                .filter(new Func1<List<JSONObject>, Boolean>() {
                    @Override
                    public Boolean call(List<JSONObject> jsonObjects) {
                        return jsonObjects.size() > 0;
                    }
                })
                .subscribeOn(Schedulers.io());
    }

    public Observable<List<JSONObject>> getAllMarkers() {
        return Observable
                .merge(
                        getAllMarkersObservable(),
                        networkOverwrite
                                .flatMap(new Func1<Boolean, Observable<List<JSONObject>>>() {
                                    @Override
                                    public Observable<List<JSONObject>> call(Boolean aBoolean) {
                                        return aBoolean
                                                ? getAllMarkersObservable()
                                                : Observable.<List<JSONObject>>empty();
                                    }
                                }))
                .filter(new Func1<List<JSONObject>, Boolean>() {
                    @Override
                    public Boolean call(List<JSONObject> jsonObjects) {
                        return jsonObjects.size() > 0;
                    }
                });
    }

    private Observable<List<JSONObject>> getAllMarkersObservable() {
        return dataStore
                .getStoredDistricts()
                .first()
                .filter(new Func1<List<String>, Boolean>() {
                    @Override
                    public Boolean call(List<String> strings) {
                        return strings != null;
                    }
                })
                .flatMap(new Func1<List<String>, Observable<List<JSONObject>>>() {
                    @Override
                    public Observable<List<JSONObject>> call(List<String> districts) {
                        return Observable.from(districts).collect(
                                new Func0<List<JSONObject>>() {
                                    @Override
                                    public List<JSONObject> call() {
                                        return new ArrayList<>();
                                    }
                                }, new Action2<List<JSONObject>, String>() {
                                    @Override
                                    public void call(
                                            List<JSONObject> jsonObjects,
                                            String district) {
                                        List<JSONObject> markers = getMarkersForDistrict(district)
                                                .take(1)
                                                .toBlocking()
                                                .first();
                                        if (markers.size() > 0)
                                            jsonObjects.addAll(markers);
                                    }
                                });
                    }
                })
                .subscribeOn(Schedulers.io());
    }
}
