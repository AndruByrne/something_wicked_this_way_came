package com.anthropicandroid.sfcrimedata.module;

/*
 * Created by user on 7/10/2016.
 */

import com.trello.navi.NaviComponent;
import com.trello.rxlifecycle.ActivityLifecycleProvider;
import com.trello.rxlifecycle.navi.NaviLifecycle;

import dagger.Module;
import dagger.Provides;

@Module
public class NaviModule {

    public static final String TAG = NaviModule.class.getSimpleName();
    private NaviComponent naviComponent;

    public NaviModule(NaviComponent naviComponent) {this.naviComponent = naviComponent;}

    @Provides
    @NaviActivityScope
    NaviComponent getNaviComponent(){ return naviComponent; }

    @Provides
    @NaviActivityScope
    ActivityLifecycleProvider getActivityLifecycleProvider(NaviComponent naviComponent){
        return NaviLifecycle.createActivityLifecycleProvider(naviComponent);
    }

}
