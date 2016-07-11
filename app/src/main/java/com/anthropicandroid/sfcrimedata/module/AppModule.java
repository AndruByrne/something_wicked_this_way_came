package com.anthropicandroid.sfcrimedata.module;

/*
 * Created by Andrew Brin on 7/8/2016.
 */

import android.app.Application;
import android.content.Context;

import dagger.Module;
import dagger.Provides;

@Module
public class AppModule {

    private Application application;

    public AppModule(Application application){ this.application = application; }

    @Provides Application getApplication(){ return application; }

    @Provides Context getContext(){ return application; }
}
