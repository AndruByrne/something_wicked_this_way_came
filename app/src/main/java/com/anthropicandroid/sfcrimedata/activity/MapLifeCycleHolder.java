package com.anthropicandroid.sfcrimedata.activity;

import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.MapView;

import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;

/*
 * Created by Andrew Brin on 7/10/2016.
 */
public class MapLifeCycleHolder {
    public static final String TAG = MapLifeCycleHolder.class.getSimpleName();
    private final Observable<Bundle> onCreate;
    private final Observable<Void> onResume;
    private final Observable<Void> onPause;
    private final Observable<Void> onDestroy;
    private final Observable<Bundle> onSavedInstanceState;

    public MapLifeCycleHolder(
            Observable<Bundle> onCreate,
            Observable<Void> onResume,
            Observable<Void> onPause,
            Observable<Void> onDestroy,
            Observable<Bundle> onSavedInstanceState) {

        this.onCreate = onCreate;
        this.onResume = onResume;
        this.onPause = onPause;
        this.onDestroy = onDestroy;
        this.onSavedInstanceState = onSavedInstanceState;
    }

    public void addMap(final MapView mapView) {
        Log.d(TAG, "adding map");
        mapView.onCreate(new Bundle());
        mapView.onResume();
        Subscriber<? super Bundle> createSubscriber = new Subscriber<Bundle>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                getLifecycleError(e);
            }

            @Override
            public void onNext(Bundle bundle) {
                Log.d(TAG, " got obnNExt to forward to mapViewe ");
                mapView.onCreate(bundle);
            }
        };
        Subscriber<? super Bundle> savedInstanceStateSubscriber = new Subscriber<Bundle>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                getLifecycleError(e);
            }

            @Override
            public void onNext(Bundle bundle) {
                mapView.onSaveInstanceState(bundle);
            }
        };
        Subscriber<? super Void> resumeSubscriber = new Subscriber<Void>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                getLifecycleError(e);
            }

            @Override
            public void onNext(Void aVoid) {
                Log.d(TAG, " got resume to forward to mapViewe ");
                mapView.onResume();
            }
        };
        Subscriber<? super Void> pauseSubscriber = new Subscriber<Void>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                getLifecycleError(e);
            }

            @Override
            public void onNext(Void aVoid) {
                Log.d(TAG, " got pause to forward to mapViewe ");

                mapView.onPause();
            }
        };
        Subscriber<? super Void> destroySubscriber = new Subscriber<Void>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                getLifecycleError(e);
            }

            @Override
            public void onNext(Void aVoid) {
                mapView.onDestroy();
            }
        };

        onCreate
                .observeOn(Schedulers.newThread())
                .subscribe(createSubscriber);
        onResume
                .observeOn(Schedulers.newThread())
                .subscribe(resumeSubscriber);
        onPause
                .observeOn(Schedulers.newThread())
                .subscribe(pauseSubscriber);
        onDestroy
                .observeOn(Schedulers.newThread())
                .subscribe(destroySubscriber);
        onSavedInstanceState
                .observeOn(Schedulers.newThread())
                .subscribe(savedInstanceStateSubscriber);
    }

    private static void getLifecycleError(Throwable e) {
        Log.e(
                TAG,
                " Error in map lifecycle holder: " + e.getMessage());
        e.printStackTrace();
    }
}
