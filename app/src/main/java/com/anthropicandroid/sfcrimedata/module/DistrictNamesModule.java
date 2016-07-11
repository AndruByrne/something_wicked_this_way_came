package com.anthropicandroid.sfcrimedata.module;

/*
 * Created by Andrew Brin on 7/10/2016.
 */

import java.util.ArrayList;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import rx.Observable;

@Module
public class DistrictNamesModule {

    @Provides
    @NaviActivityScope
    @Named("DistrictNames") Observable<ArrayList<String>> getDistrictNames(){
        final ArrayList<String> tempDistricts = new ArrayList<String>() {{
            add("NORTHERN");
            add("SOUTHERN");
            add("VALDERON");
            add("MORIA");
        }};
        return Observable.just(tempDistricts);
    }
}
