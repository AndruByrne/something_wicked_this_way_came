package com.anthropicandroid.sfcrimedata.activity;

import android.app.Activity;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.Menu;

import com.anthropicandroid.sfcrimedata.R;
import com.anthropicandroid.sfcrimedata.SFCrimeDataApplication;
import com.anthropicandroid.sfcrimedata.databinding.ActivityCrimeSpotsBinding;
import com.anthropicandroid.sfcrimedata.module.ApplicationComponent;

public class CrimeSpotsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SFCrimeDataApplication application = (SFCrimeDataApplication)getApplication();
        ApplicationComponent applicationComponent = application.getApplicationComponent();
        applicationComponent.inject(this);
        ActivityCrimeSpotsBinding viewDataBinding = DataBindingUtil.setContentView(
                this,
                R.layout.activity_crime_spots,
                applicationComponent);

        setActionBar(viewDataBinding.appBar);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }
}
