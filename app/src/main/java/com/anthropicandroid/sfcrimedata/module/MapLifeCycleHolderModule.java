package com.anthropicandroid.sfcrimedata.module;

/*
 * Created by Andrew Brin on 7/10/2016.
 */

import com.anthropicandroid.sfcrimedata.activity.MapLifeCycleHolder;
import com.trello.navi.NaviComponent;

import dagger.Module;
import dagger.Provides;

@Module
public class MapLifeCycleHolderModule {

    @Provides
    @NaviActivityScope
    MapLifeCycleHolder getMapLifeCycleHolder( NaviComponent naviComponent) {
        return new MapLifeCycleHolder(naviComponent);
    }
}
