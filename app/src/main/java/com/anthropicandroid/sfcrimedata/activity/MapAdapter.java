package com.anthropicandroid.sfcrimedata.activity;

import android.databinding.BindingAdapter;
import android.util.Log;

import com.anthropicandroid.sfcrimedata.module.ActivityComponent;
import com.anthropicandroid.sfcrimedata.services.MarkerService;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.maps.android.geojson.GeoJsonLayer;
import com.trello.rxlifecycle.ActivityLifecycleProvider;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func2;

/*
 * Created by Andrew Brin on 7/10/2016.
 *
 *
 * Because of the following record-keeping issue, I cannot display markers for the districts that
  * would accuratly reflect the areas covered; my fall-back is to use markers which are colored
  * differently based on district reporting levels
  *
  *
 * As of July 19, 2015, the PD District boundaries have been updated through a redistricting
 * process. These new boundaries are not reflected in the dataset yet so you cannot compare data
 * from July 19, 2015 onward to official reports from PD with the Police District column. We are
 * working on an update to the dataset to reflect the updated boundaries starting with data
 * entered July 19 onward.
 *
 *
 */
public class MapAdapter {

    public static final String TAG = MapAdapter.class.getSimpleName();

    @BindingAdapter("districtToReflect")
    public static void getDistrictToReflect(
            final ActivityComponent activityComponent,
            final MapView mapView,
            String district) {
        if (district == null) Log.d(TAG, "district is null");
        else Log.d(TAG, "district is: " + district);
        // get lifecycle provider
        ActivityLifecycleProvider activityLifecycleProvider = activityComponent
                .getActivityLifecycleProvider();
        // give the map updates from lifecycle
        activityComponent.getMapLifeCycleHolder().addMap(mapView);
        // get the map
        Observable<GoogleMap> theMap = getTheMap(mapView);
        // sanitize district string
        if (!validDistrict(district, activityComponent.getDistrictNames()))
            district = null;
        // give the map updates from model
        MarkerService markerService = activityComponent.getMarkerService();
        Observable
                .combineLatest(
                        theMap,
                        district == null
                                ? markerService.getAllMarkers()
                                : markerService.getMarkersForDistrict(district),
                        new Func2<GoogleMap, List<JSONObject>, List<GeoJsonLayer>>() {
                            @Override
                            public List<GeoJsonLayer> call(
                                    final GoogleMap googleMap,
                                    final List<JSONObject> jsonObjects) {
                                return new ArrayList<GeoJsonLayer>() {{
                                    for (JSONObject jsonObject : jsonObjects)
                                        add(new GeoJsonLayer(googleMap, jsonObject));
                                }};
                            }
                        })
                .compose(activityLifecycleProvider.<List<GeoJsonLayer>>bindToLifecycle())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Action1<List<GeoJsonLayer>>() {
                            @Override
                            public void call(List<GeoJsonLayer> geoJsonLayers) {
                                List<GeoJsonLayer> layerRegister = activityComponent
                                        .getLayerRegister();
                                for (GeoJsonLayer oldLayer : layerRegister)
                                    oldLayer.removeLayerFromMap();
                                layerRegister.clear();
                                for (GeoJsonLayer layer : geoJsonLayers) {
                                    activityComponent.getLayerRegister().add(layer);
                                    layer.addLayerToMap();
                                }
                            }
                        },
                        new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {
                                Log.e(TAG, "error in map adapter data feed: " + throwable
                                        .getMessage());
                                throwable.printStackTrace();

                            }
                        }
                );
    }

    private static boolean validDistrict(
            String district,
            Observable<List<String>> districtNames) {
        if (district == null) return true;
        List<String> districts = districtNames.toBlocking().first();
        return districts.contains(district);
    }

    private static Observable<GoogleMap> getTheMap(final MapView mapView) {
        return Observable.create(new Observable.OnSubscribe<GoogleMap>() {
            @Override
            public void call(final Subscriber<? super GoogleMap> subscriber) {
                mapView.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(GoogleMap googleMap) {
                        subscriber.onNext(googleMap);
                    }
                });
            }
        }).subscribeOn(AndroidSchedulers.mainThread());
    }
}
