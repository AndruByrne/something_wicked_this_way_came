package com.anthropicandroid.sfcrimedata.activity;

import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.MapView;
import com.trello.navi.Event;
import com.trello.navi.NaviComponent;
import com.trello.navi.rx.RxNavi;

import rx.Subscriber;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/*
 * Created by Andrew Brin on 7/10/2016.
 */
public class MapLifeCycleHolder {
    public static final String TAG = MapLifeCycleHolder.class.getSimpleName();
    private NaviComponent naviComponent;

    public MapLifeCycleHolder(NaviComponent naviComponent) { this.naviComponent = naviComponent; }

    public void addMap(final MapView mapView) {
        Log.d(TAG, "adding map");
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
            public void onNext(Bundle bundle) {
                Log.d(TAG, " got obnNExt to forward to mapViewe ");
                mapView.onCreate(bundle);
            }
        };
        Subscriber<? super Bundle> savedInstanceStateSubscriber = new Subscriber<Bundle>() {
            @Override
            public void onCompleted() { }

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
            public void onCompleted() { }

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
            public void onCompleted() { }

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
                .doOnNext(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        Log.d(TAG, "got onResume from navi OK");
                    }
                })
                .observeOn(Schedulers.newThread())
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
