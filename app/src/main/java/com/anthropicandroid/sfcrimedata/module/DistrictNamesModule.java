package com.anthropicandroid.sfcrimedata.module;

/*
 * Created by Andrew Brin on 7/10/2016.
 */

import com.anthropicandroid.sfcrimedata.services.DataStore;

import java.util.List;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import rx.Observable;
import rx.functions.Func1;

@Module
public class DistrictNamesModule {

    @Provides
    @NaviActivityScope
    @Named("DistrictNames")
    Observable<List<String>> getDistrictNames(
            final DataStore dataStore,
            @Named("NetworkOverwrite") Observable<Boolean> networkOverwrite) {

        return Observable
                .merge(
                        dataStore.getStoredDistricts(),
                        networkOverwrite.flatMap(new Func1<Boolean, Observable<List<String>>>() {
                            @Override
                            public Observable<List<String>> call(Boolean aBoolean) {
                                return aBoolean
                                        ? dataStore.getStoredDistricts()
                                        : Observable.<List<String>>empty();
                            }
                        }))
                .filter(new Func1<List<String>, Boolean>() {
                    @Override
                    public Boolean call(List<String> strings) {
                        if(strings==null) return false;
                        return strings.size() > 0;
                    }
                });
    }

}
