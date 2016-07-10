package com.anthropicandroid.sfcrimedata.module;

/*
 * Created by user on 7/10/2016.
 */

import android.os.Bundle;
import android.util.Log;

import com.trello.navi.Event;
import com.trello.navi.NaviComponent;
import com.trello.navi.rx.RxNavi;
import com.trello.rxlifecycle.ActivityLifecycleProvider;
import com.trello.rxlifecycle.navi.NaviLifecycle;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import rx.Observable;
import rx.functions.Action1;
import rx.observables.ConnectableObservable;

@Module
public class NaviModule {

    public static final String TAG = NaviModule.class.getSimpleName();
    private NaviComponent naviComponent;

    public NaviModule(NaviComponent naviComponent) {this.naviComponent = naviComponent;}

    @Provides
    @NaviActivityScope
    ActivityLifecycleProvider getActivityLifecycleProvider() {
        return NaviLifecycle.createActivityLifecycleProvider(naviComponent);
    }

    @Provides
    @NaviActivityScope
    @Named("onCreate")
    Observable<Bundle> getOnCreate() {
        ConnectableObservable<Bundle> replay = RxNavi
                .observe(naviComponent, Event.CREATE)
                .replay();
        replay.connect();
        return replay;
    }

    @Provides
    @NaviActivityScope
    @Named("onStart")
    Observable<Void> getOnStart() {
        ConnectableObservable<Void> replay = RxNavi
                .observe(naviComponent, Event.START)
                .doOnNext(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        Log.d(TAG, "got onResume from navi OK");
                    }
                })
                .replay();
        replay.connect();
        return replay;
    }

    @Provides
    @NaviActivityScope
    @Named("onResume")
    Observable<Void> getOnResume() {
        ConnectableObservable<Void> replay = RxNavi
                .observe(naviComponent, Event.RESUME)
                .replay();
        replay.connect();
        return replay;
    }

    @Provides
    @NaviActivityScope
    @Named("onPause")
    Observable<Void> getOnPause() {
        return RxNavi
                .observe(naviComponent, Event.PAUSE);
    }

    @Provides
    @NaviActivityScope
    @Named("onStop")
    Observable<Void> getOnStop() {
        return RxNavi
                .observe(naviComponent, Event.STOP);
    }

    @Provides
    @NaviActivityScope
    @Named("onDestroy")
    Observable<Void> getOnDestroy() {
        return RxNavi
                .observe(naviComponent, Event.DESTROY);
    }

    @Provides
    @NaviActivityScope
    @Named("onSavedInstanceState")
    Observable<Bundle> getOnSavedInstanceState() {
        ConnectableObservable<Bundle> replay = RxNavi
                .observe(naviComponent, Event.SAVE_INSTANCE_STATE)
                .replay();
        replay.connect();
        return replay;
    }

}
