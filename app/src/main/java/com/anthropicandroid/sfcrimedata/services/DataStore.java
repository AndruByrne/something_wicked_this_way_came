package com.anthropicandroid.sfcrimedata.services;

import android.content.Context;
import android.util.Log;

import net.rehacktive.waspdb.WaspDb;
import net.rehacktive.waspdb.WaspFactory;
import net.rehacktive.waspdb.WaspHash;
import net.rehacktive.waspdb.WaspListener;

import org.json.JSONObject;

import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;
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
    private WaspHash districtRankingHash;

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
            districtRankingHash = waspDb.openOrCreateHash("DistrictRankingHash");
            districtActivityHash = waspDb.openOrCreateHash("DistrictActivityHash");
            districtDataHash = waspDb.openOrCreateHash("DistrictDataHash");
        }

    }

    public Observable<List<String>> getStoredDistricts() {
        return dbCreate.map(new Func1<Boolean, List<String>>() {
            @Override
            public List<String> call(Boolean aBoolean) {
                return districtActivityHash.getAllKeys();
            }
        }).take(1);
    }

    public Observable<Boolean> saveDistrictActivity(
            final String district,
            final Integer districtActivity) {
        return dbCreate
                .map(new Func1<Boolean, Boolean>() {
                    @Override
                    public Boolean call(Boolean aBoolean) {
                        Integer storedDistrictActivity = districtActivityHash.get(district);
                        if (storedDistrictActivity != null
                                && storedDistrictActivity.equals(districtActivity))
                            return false;
                        districtActivityHash.put(district, districtActivity);
                        return true;
                    }
                })
                .take(1);
    }

    public Observable<List<JSONObject>> getMarkersForDistrict(String district) {
        Log.d(TAG, "getmarkers for dist");
        return null;
    }

    public List<String> getDistrictsByMostActivity() {
        return dbCreate
                .flatMap(new Func1<Boolean, Observable<String>>() {
                    @Override
                    public Observable<String> call(Boolean aBoolean) {
                        return Observable
                                .range(0, districtRankingHash.getAllKeys().size())
                                .concatMap(new Func1<Integer, Observable<? extends String>>() {
                                    @Override
                                    public Observable<? extends String> call(Integer integer) {
                                        return Observable
                                                .just((String) districtRankingHash.get(integer));
                                    }
                                })
                                .filter(new Func1<String, Boolean>() {
                                    @Override
                                    public Boolean call(String s) {
                                        return s != null;
                                    }
                                });
                    }
                })
                .toList()
                .toBlocking()
                .first();
    }

    public void setDistrictRankingsByDescendingActivity(final List<String> districtsInOrder) {
        districtRankingHash.flush();
        Observable
                .range(0, districtsInOrder.size())
                .doOnNext(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        districtRankingHash.put(integer, districtsInOrder.remove(integer));
                    }
                }).subscribe();
    }
}
