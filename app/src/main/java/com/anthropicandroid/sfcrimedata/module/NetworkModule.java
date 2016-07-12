package com.anthropicandroid.sfcrimedata.module;

/*
 * Created by Anderw Brin on 7/11/2016.
 */

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.util.List;

import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import rx.Observable;

@Module
public class NetworkModule {

    public interface SFPDInterface{
        @GET("ritf-b9ki.json")
        Observable<List<JsonObject>> getIncidents();
    }

    @Provides
    @NaviActivityScope
    Gson providesGson(){
        return new GsonBuilder().create();
    }

    @Provides
    @NaviActivityScope
    Retrofit providesRetrofit(Gson gson){
        return new Retrofit.Builder()
                .baseUrl("https://data.sfgov.org/resource/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
    }

    @Provides
    @NaviActivityScope
    SFPDInterface providesSFPDInterface(Retrofit retrofit){
        return retrofit.create(SFPDInterface.class);
    }
}
