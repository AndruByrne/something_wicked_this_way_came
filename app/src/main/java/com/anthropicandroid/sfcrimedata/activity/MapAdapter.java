package com.anthropicandroid.sfcrimedata.activity;

import android.databinding.BindingAdapter;
import android.util.Log;

import com.anthropicandroid.sfcrimedata.module.ActivityComponent;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func2;

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
        // give the map updates from lifecycle
        activityComponent.getMapLifeCycleHolder().addMap(mapView);
        // get the map
        Observable<GoogleMap> theMap = getTheMap(mapView);
        // give the map updates from model
        Observable
                .combineLatest(
                        theMap,
                        activityComponent.getMarkers(),
                        new Func2<GoogleMap, List<MarkerOptions>, MapAndMarkers>() {
                            @Override
                            public MapAndMarkers call(
                                    GoogleMap googleMap,
                                    List<MarkerOptions> markerOptionses) {
                                return new MapAndMarkers(googleMap, markerOptionses);
                            }
                        })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Action1<MapAndMarkers>() {
                            @Override
                            public void call(MapAndMarkers mapAndMarkers) {
                                for (MarkerOptions marker : mapAndMarkers.markerOptionses)
                                    mapAndMarkers.googleMap.addMarker(marker);
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

    private static class MapAndMarkers {
        final GoogleMap googleMap;
        final List<MarkerOptions> markerOptionses;

        public MapAndMarkers(
                GoogleMap googleMap,
                List<MarkerOptions> markerOptionses) {

            this.googleMap = googleMap;
            this.markerOptionses = markerOptionses;
        }
    }
}
