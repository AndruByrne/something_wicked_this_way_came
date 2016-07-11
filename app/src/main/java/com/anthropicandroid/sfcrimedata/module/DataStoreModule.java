package com.anthropicandroid.sfcrimedata.module;

import android.content.Context;

import com.anthropicandroid.sfcrimedata.services.DataStore;
import com.google.maps.android.geojson.GeoJsonLayer;

import java.util.ArrayList;
import java.util.List;

import dagger.Module;
import dagger.Provides;

/*
 * Created by Andrew Brin on 7/10/2016.
 */
@Module
public class DataStoreModule {

    @Provides
    @NaviActivityScope
    DataStore getDataStore(Context context){
        return new DataStore(context);
    }

    @Provides
    @NaviActivityScope
    List<GeoJsonLayer> getLayerRegister(){
        return new ArrayList<>();
    }
}
