package com.anthropicandroid.sfcrimedata.module;

/*
 * Created by Andrew Brin on 7/10/2016.
 */

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

import dagger.Module;
import dagger.Provides;
import rx.Observable;

@Module
public class MapMarkerModule {

    @Provides
    @NaviActivityScope
    Observable<ArrayList<MarkerOptions>> getMarkerOptions(){
        ArrayList<MarkerOptions> optionses = new ArrayList<MarkerOptions>() {{
            add(new MarkerOptions().position(new LatLng(37.764532, -122.4437732)));
            add(new MarkerOptions().position(new LatLng(37.764578, -122.4437778)));
            add(new MarkerOptions().position(new LatLng(37.764578, -122.4437732)));
            add(new MarkerOptions().position(new LatLng(37.764532, -122.4437778)));
        }};
        return Observable.just(optionses);
    }
}
