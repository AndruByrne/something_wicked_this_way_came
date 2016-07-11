package com.anthropicandroid.sfcrimedata.module;

/*
 * Created by Andrew Brin on 7/10/2016.
 */

import com.anthropicandroid.sfcrimedata.services.DataStore;
import com.anthropicandroid.sfcrimedata.services.MarkerService;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import rx.Observable;

@Module
public class MapMarkerModule {

    @Provides
    @NaviActivityScope
    MarkerService getMarkerService(DataStore dataStore, @Named("NetworkOverwrite") Observable<Boolean> networkOverwrite){
        return new MarkerService(dataStore, networkOverwrite);
    }

    @Provides
    @NaviActivityScope
    @Named("NetworkOverwrite") Observable<Boolean> networkOverwrite(){
        return Observable.just(true);
    }
}
