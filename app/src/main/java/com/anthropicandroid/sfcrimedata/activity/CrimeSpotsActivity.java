package com.anthropicandroid.sfcrimedata.activity;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import com.anthropicandroid.sfcrimedata.R;
import com.anthropicandroid.sfcrimedata.SFCrimeDataApplication;
import com.anthropicandroid.sfcrimedata.databinding.ActivityCrimeSpotsBinding;
import com.anthropicandroid.sfcrimedata.module.ActivityComponent;
import com.trello.navi.component.NaviActivity;
import com.trello.rxlifecycle.ActivityLifecycleProvider;

import java.util.ArrayList;

import javax.inject.Inject;

public class CrimeSpotsActivity extends NaviActivity {

    @Inject ActivityLifecycleProvider lifecycleProvider;

    public static final String TAG = CrimeSpotsActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SFCrimeDataApplication application = (SFCrimeDataApplication)getApplication();
        ActivityComponent activityComponent = application.getActivityComponent(this);
        activityComponent.inject(this);
        ActivityCrimeSpotsBinding viewDataBinding = DataBindingUtil.setContentView(
                this,
                R.layout.activity_crime_spots,
                activityComponent);
        setActionBar(viewDataBinding.appBar);
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        final MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.app_bar_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.search_districts);
        AutoCompleteTextView searchField = (AutoCompleteTextView) MenuItemCompat.getActionView(
                searchItem);

        searchField.setEms(14);

        final ArrayList<String> tempDistricts = new ArrayList<String>(){{
            add("NORTHERN");
            add("SOUTHERN");
            add("VALDERON");
            add("MORIA");
        }};

        ArrayAdapter<String> districtSearchAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                tempDistricts);
        searchField.setAdapter(districtSearchAdapter);

        searchItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem menuItem) {
                AutoCompleteTextView  actionView = (AutoCompleteTextView )MenuItemCompat.getActionView(
                        menuItem);
                actionView.requestFocus();
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context
                        .INPUT_METHOD_SERVICE);
                inputMethodManager.toggleSoftInput(
                        InputMethodManager.SHOW_IMPLICIT,
                        InputMethodManager.HIDE_IMPLICIT_ONLY);
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem menuItem) {
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }
}
