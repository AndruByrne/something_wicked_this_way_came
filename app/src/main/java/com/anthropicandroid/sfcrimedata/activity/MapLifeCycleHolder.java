package com.anthropicandroid.sfcrimedata.activity;

import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.MapView;
import com.trello.navi.Event;
import com.trello.navi.NaviComponent;
import com.trello.navi.rx.RxNavi;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/*
 * Created by Andrew Brin on 7/10/2016.
 */
public class MapLifeCycleHolder {
    public static final String TAG = MapLifeCycleHolder.class.getSimpleName();
    private NaviComponent naviComponent;

    public MapLifeCycleHolder(NaviComponent naviComponent) { this.naviComponent = naviComponent; }

    public void addMap(final MapView mapView) {
        // map appears after onCreate and onResume, so must give it those commands manually
        mapView.onCreate(new Bundle());
        mapView.onResume();
        Subscriber<? super Bundle> createSubscriber = new Subscriber<Bundle>() {
            @Override
            public void onCompleted() { }

            @Override
            public void onError(Throwable e) {
                getLifecycleError(e);
            }

            @Override
            public void onNext(Bundle bundle) { mapView.onCreate(bundle); }
        };
        Subscriber<? super Bundle> savedInstanceStateSubscriber = new Subscriber<Bundle>() {
            @Override
            public void onCompleted() { }

            @Override
            public void onError(Throwable e) {
                getLifecycleError(e);
            }

            @Override
            public void onNext(Bundle bundle) { mapView.onSaveInstanceState(bundle); }
        };
        Subscriber<? super Void> resumeSubscriber = new Subscriber<Void>() {
            @Override
            public void onCompleted() { }

            @Override
            public void onError(Throwable e) {
                getLifecycleError(e);
            }

            @Override
            public void onNext(Void aVoid) { mapView.onResume(); }
        };
        Subscriber<? super Void> pauseSubscriber = new Subscriber<Void>() {
            @Override
            public void onCompleted() { }

            @Override
            public void onError(Throwable e) {
                getLifecycleError(e);
            }

            @Override
            public void onNext(Void aVoid) { mapView.onPause(); }
        };
        Subscriber<? super Void> destroySubscriber = new Subscriber<Void>() {
            @Override
            public void onCompleted() { }

            @Override
            public void onError(Throwable e) {
                getLifecycleError(e);
            }

            @Override
            public void onNext(Void aVoid) {
                mapView.onDestroy();
            }
        };

        RxNavi
                .observe(naviComponent, Event.CREATE)
                .observeOn(Schedulers.newThread())
                .subscribe(createSubscriber);
        RxNavi
                .observe(naviComponent, Event.RESUME)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(resumeSubscriber);
        RxNavi
                .observe(naviComponent, Event.PAUSE)
                .observeOn(Schedulers.newThread())
                .subscribe(pauseSubscriber);
        RxNavi
                .observe(naviComponent, Event.DESTROY)
                .observeOn(Schedulers.newThread())
                .subscribe(destroySubscriber);
        RxNavi
                .observe(naviComponent, Event.SAVE_INSTANCE_STATE)
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
