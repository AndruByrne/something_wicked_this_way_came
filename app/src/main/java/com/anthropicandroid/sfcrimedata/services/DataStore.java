package com.anthropicandroid.sfcrimedata.services;

import android.content.Context;

import net.rehacktive.waspdb.WaspDb;
import net.rehacktive.waspdb.WaspFactory;
import net.rehacktive.waspdb.WaspHash;
import net.rehacktive.waspdb.WaspListener;

import org.json.JSONObject;

import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;
import rx.observables.ConnectableObservable;
import rx.schedulers.Schedulers;

/*
 * Created by Andrew Brin on 7/10/2016.
 */
public class DataStore {

    public static final String PD_DATABASE = "PDDB";
    public static final String TAG = DataStore.class.getSimpleName();
    private WaspDb waspDb;
    private final ConnectableObservable<Boolean> dbCreate;
    private WaspHash districtActivityHash;
    private WaspHash districtDataHash;

    public DataStore(final Context context) {
        dbCreate = Observable
                .create(new Observable.OnSubscribe<Boolean>() {
                    @Override
                    public void call(final Subscriber<? super Boolean> subscriber) {
                        String path = context.getFilesDir().getPath();
                        WaspFactory.openOrCreateDatabase(
                                path,
                                PD_DATABASE,
                                "password",
                                new WaspListener<WaspDb>() {
                                    @Override
                                    public void onDone(WaspDb waspDb) {
                                        initTables(waspDb);
                                        subscriber.onNext(true);
                                    }
                                }
                        );
                    }
                })
                .take(1)
                .subscribeOn(Schedulers.computation())
                .replay(Schedulers.io());
        dbCreate.connect();
    }

    private void initTables(WaspDb waspDb) {
        if (this.waspDb == null) {
            this.waspDb = waspDb;
            districtActivityHash = waspDb.openOrCreateHash("DistrictActivityHash");
            districtDataHash = waspDb.openOrCreateHash("DistrictDataHash");
        }

    }

    public Observable<List<String>> getStoredDistricts() {
        return dbCreate.map(new Func1<Boolean, List<String>>() {
            @Override
            public List<String> call(Boolean aBoolean) {
                return districtDataHash.getAllKeys();
            }
        }).take(1);
    }

    public Observable<List<JSONObject>> getMarkersForDistrict(final String district) {
        return dbCreate
                .map(new Func1<Boolean, List<JSONObject>>() {
                    @Override
                    public List<JSONObject> call(Boolean aBoolean) {
                        return districtDataHash.get(district);
                    }
                });
    }

    public Observable<Boolean> setMarkersForDistrict(
            final List<JSONObject> newMarkers, final String
            district) {
        return dbCreate
                .map(new Func1<Boolean, Boolean>() {
                    @Override
                    public Boolean call(Boolean aBoolean) {
                        List oldMarkers = districtDataHash.get(district);
                        if (oldMarkers == null) {
                            districtDataHash.put(district, newMarkers);
                            return true;
                        } else if (newMarkers == null || oldMarkers.equals(newMarkers))
                            return false;
                        districtDataHash.put(district, newMarkers);
                        return true;
                    }
                });
    }
}
