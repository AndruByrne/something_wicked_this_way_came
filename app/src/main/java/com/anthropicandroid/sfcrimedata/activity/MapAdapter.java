package com.anthropicandroid.sfcrimedata.activity;

import android.databinding.BindingAdapter;
import android.util.Log;

import com.anthropicandroid.sfcrimedata.module.ActivityComponent;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;

import rx.Observable;
import rx.Subscriber;
import rx.observables.ConnectableObservable;

/*
 * Created by Andrew Brin on 7/10/2016.
 */
public class MapAdapter {

    public static final String TAG = MapAdapter.class.getSimpleName();

    @BindingAdapter("daysToReflect")
    public static void getDaysToReflect(
            ActivityComponent activityComponent,
            MapView mapView,
            Integer days) {
        // get the map
        ConnectableObservable<GoogleMap> theMap = getTheMap(mapView);
        // give the map updates from lifecycle
        activityComponent.getMapLifeCycleHolder().addMap(mapView);
        // give the map updates from model
        // begin getting the map
        theMap.connect();
        Log.d(TAG, "get to view model");
    }

    private static ConnectableObservable<GoogleMap> getTheMap(final MapView mapView) {
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
        }).publish();
    }
}
