package com.anthropicandroid.sfcrimedata;

import android.app.Application;

import com.anthropicandroid.sfcrimedata.module.ActivityComponent;
import com.anthropicandroid.sfcrimedata.module.AppModule;
import com.anthropicandroid.sfcrimedata.module.ApplicationComponent;
import com.anthropicandroid.sfcrimedata.module.DaggerActivityComponent;
import com.anthropicandroid.sfcrimedata.module.DaggerApplicationComponent;
import com.anthropicandroid.sfcrimedata.module.NaviModule;
import com.trello.navi.component.NaviActivity;

/*
 * Created by Andrew Brin on 7/8/2016.
 */
public class SFCrimeDataApplication extends Application {

    private SFCrimeDataApplication instance;
    private ApplicationComponent applicationComponent;
    private ActivityComponent activityComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        applicationComponent = DaggerApplicationComponent
                .builder()
                .appModule(new AppModule(this))
                .build();
    }

    SFCrimeDataApplication getInstance() {
        return instance;
    }

    public ActivityComponent getActivityComponent(NaviActivity naviActivity) {
        if (activityComponent == null)
            activityComponent = DaggerActivityComponent
                    .builder()
                    .applicationComponent(applicationComponent)
                    .naviModule(new NaviModule(naviActivity))
                    .build();
        return activityComponent;
    }
}
