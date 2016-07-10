package com.anthropicandroid.sfcrimedata.module;

/*
 * Created by Andrew Brin on 7/10/2016.
 */

import android.os.Bundle;

import com.anthropicandroid.sfcrimedata.activity.MapLifeCycleHolder;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import rx.Observable;

@Module
public class MapLifeCycleHolderModule {

    @Provides
    @NaviActivityScope
    MapLifeCycleHolder getMapLifeCycleHolder(
            @Named("onCreate")Observable<Bundle> onCreate,
            @Named("onResume")Observable<Void> onResume,
            @Named("onPause")Observable<Void> onPause,
            @Named("onDestroy")Observable<Void> onDestroy,
            @Named("onSavedInstanceState")Observable<Bundle> onSavedInstanceState
    ){
        return new MapLifeCycleHolder(onCreate, onResume, onPause, onDestroy, onSavedInstanceState);
    }
}
