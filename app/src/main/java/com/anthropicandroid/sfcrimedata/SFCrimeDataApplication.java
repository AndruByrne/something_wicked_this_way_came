package com.anthropicandroid.sfcrimedata;

import android.app.Application;

import com.anthropicandroid.sfcrimedata.module.AppModule;
import com.anthropicandroid.sfcrimedata.module.ApplicationComponent;
import com.anthropicandroid.sfcrimedata.module.DaggerApplicationComponent;

/*
 * Created by Andrew Brin on 7/8/2016.
 */
public class SFCrimeDataApplication extends Application {

    private SFCrimeDataApplication instance;
    private ApplicationComponent applicationComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        applicationComponent = DaggerApplicationComponent
                .builder()
                .appModule(new AppModule(this))
                .build();
    }

    public ApplicationComponent getApplicationComponent() {
        return applicationComponent;
    }


    public SFCrimeDataApplication getInstance() {
        return instance;
    }
}
