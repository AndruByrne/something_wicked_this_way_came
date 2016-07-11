package com.anthropicandroid.sfcrimedata.module;

/*
 * Created by Andrew Brin on 7/10/2016.
 */


import android.databinding.DataBindingComponent;

import com.anthropicandroid.sfcrimedata.activity.CrimeSpotsActivity;
import com.anthropicandroid.sfcrimedata.activity.MapLifeCycleHolder;
import com.trello.rxlifecycle.ActivityLifecycleProvider;

import org.json.JSONObject;

import dagger.Component;
import rx.Observable;


@NaviActivityScope
@Component(
        dependencies = {
                ApplicationComponent.class
        },
        modules = {
                NaviModule.class,
                MapLifeCycleHolderModule.class,
                MapMarkerModule.class,
                DistrictNamesModule.class
        })
public interface ActivityComponent extends DataBindingComponent {
    void inject(CrimeSpotsActivity crimeSpotsActivity);

    MapLifeCycleHolder getMapLifeCycleHolder();

    ActivityLifecycleProvider getActivityLifecycleProvider();

    Observable<JSONObject> getMarkers();
}
