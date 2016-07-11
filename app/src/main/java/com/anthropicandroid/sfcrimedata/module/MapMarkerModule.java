package com.anthropicandroid.sfcrimedata.module;

/*
 * Created by Andrew Brin on 7/10/2016.
 */

import org.json.JSONObject;

import dagger.Module;
import dagger.Provides;
import rx.Observable;

@Module
public class MapMarkerModule {

    @Provides
    @NaviActivityScope
    Observable<JSONObject> getMarkers(){
        JSONObject dummyJsonData = new JSONObject();
//        return Observable.concat(
//                dataStoreObs,
//                networkObs, //  no update to data store will make this network obs empty
//                searchActionObs);
        return Observable.just(dummyJsonData);
    }
}
