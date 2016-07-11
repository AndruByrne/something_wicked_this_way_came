package com.anthropicandroid.sfcrimedata.module;

/*
 * Created by Andrew Brin on 7/10/2016.
 */


import android.databinding.DataBindingComponent;

import com.anthropicandroid.sfcrimedata.activity.CrimeSpotsActivity;
import com.anthropicandroid.sfcrimedata.activity.MapLifeCycleHolder;
import com.anthropicandroid.sfcrimedata.services.MarkerService;
import com.google.maps.android.geojson.GeoJsonLayer;
import com.trello.rxlifecycle.ActivityLifecycleProvider;

import java.util.List;

import javax.inject.Named;

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
                DistrictNamesModule.class,
                DataStoreModule.class
        })
public interface ActivityComponent extends DataBindingComponent {
    void inject(CrimeSpotsActivity crimeSpotsActivity);

    MapLifeCycleHolder getMapLifeCycleHolder();

    ActivityLifecycleProvider getActivityLifecycleProvider();

    MarkerService getMarkerService();

    @Named("DistrictNames") Observable<List<String>> getDistrictNames();

    List<GeoJsonLayer> getLayerRegister();
}
