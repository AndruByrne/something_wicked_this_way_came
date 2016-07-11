package com.anthropicandroid.sfcrimedata.activity;

import android.databinding.BindingAdapter;
import android.util.Log;

import com.anthropicandroid.sfcrimedata.module.ActivityComponent;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.maps.android.geojson.GeoJsonLayer;

import org.json.JSONObject;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
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
    private static GeoJsonLayer currentLayer;

    @BindingAdapter("daysToReflect")
    public static void getDaysToReflect(
            ActivityComponent activityComponent,
            MapView mapView,
            String district) {
        if(district==null) Log.d(TAG, "district is null");
        else Log.d(TAG, "district is: "+district);
        // give the map updates from lifecycle
        activityComponent.getMapLifeCycleHolder().addMap(mapView);
        // get the map
        Observable<GoogleMap> theMap = getTheMap(mapView);
        // give the map updates from model
        Observable
                .combineLatest(
                        theMap,
                        activityComponent.getMarkers(),
                        new Func2<GoogleMap, JSONObject, GeoJsonLayer>() {
                            @Override
                            public GeoJsonLayer call(GoogleMap googleMap, JSONObject markers) {
                                return new GeoJsonLayer(googleMap, markers);
                            }
                        })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Action1<GeoJsonLayer>() {
                            @Override
                            public void call(GeoJsonLayer layer) {
                                if (currentLayer != null)
                                    currentLayer.removeLayerFromMap();
                                layer.addLayerToMap();
                                currentLayer = layer;
                            }
                        },
                        new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {
                                Log.e(TAG, "error in map adapter data feed: " + throwable
                                        .getMessage());
                                throwable.printStackTrace();
                            }
                        },
                        new Action0() {
                            @Override
                            public void call() { }
                        });
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
