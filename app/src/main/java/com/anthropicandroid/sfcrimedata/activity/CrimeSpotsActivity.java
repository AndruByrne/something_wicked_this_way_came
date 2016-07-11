package com.anthropicandroid.sfcrimedata.activity;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import com.anthropicandroid.sfcrimedata.R;
import com.anthropicandroid.sfcrimedata.SFCrimeDataApplication;
import com.anthropicandroid.sfcrimedata.databinding.ActivityCrimeSpotsBinding;
import com.anthropicandroid.sfcrimedata.module.ActivityComponent;
import com.trello.navi.component.NaviActivity;
import com.trello.rxlifecycle.ActivityLifecycleProvider;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

public class CrimeSpotsActivity extends NaviActivity {

    @Inject ActivityLifecycleProvider lifecycleProvider;
    @Inject @Named("DistrictNames") Observable<ArrayList<String>> districtNames;

    public static final String TAG = CrimeSpotsActivity.class.getSimpleName();
    private ActivityCrimeSpotsBinding viewDataBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SFCrimeDataApplication application = (SFCrimeDataApplication) getApplication();
        ActivityComponent activityComponent = application.getActivityComponent(this);
        activityComponent.inject(this);
        viewDataBinding = DataBindingUtil.setContentView(
                this,
                R.layout.activity_crime_spots,
                activityComponent);
        setActionBar(viewDataBinding.appBar);
        viewDataBinding.setDistrict(null);
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        final MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.app_bar_menu, menu);
        final MenuItem searchItem = menu.findItem(R.id.search_districts);
        final AutoCompleteTextView searchField = (AutoCompleteTextView) MenuItemCompat.getActionView(
                searchItem);

        final ArrayAdapter<String> districtSearchAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                new ArrayList<String>());

        districtNames
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Action1<List<String>>() {
                            @Override
                            public void call(List<String> strings) {
                                districtSearchAdapter.clear();
                                districtSearchAdapter.addAll(strings);
                                districtSearchAdapter.notifyDataSetChanged();
                            }
                        },
                        new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {
                                Log.e(TAG, "Error in populating district names: "+throwable
                                        .getMessage());
                                throwable.printStackTrace();
                            }
                        }
                );

        searchField.setEms(14);
        searchField.setAdapter(districtSearchAdapter);
        searchField.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        searchField.setInputType(EditorInfo.TYPE_CLASS_TEXT);
        searchField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_SEARCH) {
                    viewDataBinding.setDistrict(textView.getText().toString());
                    searchItem.collapseActionView();
                    return true;
                }
                return false;
            }
        });
        searchField.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String district = districtSearchAdapter.getItem(i);
                viewDataBinding.setDistrict(district);
                searchItem.collapseActionView();
            }
        });

        searchItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem menuItem) {
                AutoCompleteTextView actionView = (AutoCompleteTextView) MenuItemCompat
                        .getActionView(menuItem);
                actionView.requestFocus();
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(
                        Context.INPUT_METHOD_SERVICE);
                inputMethodManager.toggleSoftInput(
                        InputMethodManager.SHOW_IMPLICIT,
                        InputMethodManager.HIDE_IMPLICIT_ONLY);
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem menuItem) {
                return true;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }
}
