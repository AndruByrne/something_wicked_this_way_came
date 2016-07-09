package com.anthropicandroid.sfcrimedata.activity;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.anthropicandroid.sfcrimedata.R;
import com.anthropicandroid.sfcrimedata.SFCrimeDataApplication;
import com.anthropicandroid.sfcrimedata.module.ApplicationComponent;

public class CrimeSpotsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SFCrimeDataApplication application = (SFCrimeDataApplication)getApplication();
        ApplicationComponent applicationComponent = application.getApplicationComponent();
        applicationComponent.inject(this);
        ViewDataBinding viewDataBinding = DataBindingUtil.setContentView(
                this,
                R.layout.activity_crime_spots,
                applicationComponent);
    }
}
