package com.grishberg.realmasynctest.data;

import android.util.Log;

import com.grishberg.realmasynctest.data.models.SampleModel;
import com.grishberg.realmasynctest.data.models.YetAnotherModel;

import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Created by grishberg on 16.06.16.
 * manage realm queries
 */
public class DataManager {
    private static final String TAG = DataManager.class.getSimpleName();
    public static final int COUNT = 30;

    private Realm realm;

    public DataManager() {
        realm = Realm.getDefaultInstance();
    }

    /**
     * Create sample data
     */
    public void createData() {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.where(SampleModel.class).findAll().deleteAllFromRealm();
        for (int i = 0; i < COUNT; i++) {
            SampleModel sampleModel = new SampleModel();
            sampleModel.setId(i);
            sampleModel.setName(String.format(Locale.US, "item %d", i));
            realm.copyToRealmOrUpdate(sampleModel);
        }
        realm.commitTransaction();
        realm.close();
    }

    public RealmResults<SampleModel> getDataQuery() {
        return realm.where(SampleModel.class)
                .findAllSortedAsync("id", Sort.ASCENDING);
    }

    public void release() {
        if (realm != null && !realm.isClosed()) {
            realm.close();
        }
    }

    public void startFirstDataChange() {
        new Thread(changeDataRunnable1).start();
    }
    public void startFirstDataChangeParallel() {
        new Thread(changeDataRunnableParallel1).start();
    }

    public void startSecondDataChange() {
        new Thread(changeDataRunnable2).start();
    }

    public void startOtherDataChange() {
        new Thread(changeDataRunnable3).start();
    }

    /**
     * change data runnable
     */
    private Runnable changeDataRunnable1 = new Runnable() {

        @Override
        public void run() {
            sleep(2000);
            Realm rm = Realm.getDefaultInstance();
            rm.executeTransaction(new Realm.Transaction() {

                @Override
                public void execute(Realm realm) {
                    for (int i = 0; i < 15; i++) {
                        realm.where(SampleModel.class)
                                .equalTo("id", i).findAll().deleteAllFromRealm();
                        Log.d(TAG, "execute: deleted id=" + i);
                        sleep(300);
                    }

                    for (int i = 100; i < 110; i++) {
                        SampleModel sampleModel = new SampleModel();
                        sampleModel.setId(i);
                        sampleModel.setName(String.format(Locale.US, "new item %d", i));
                        realm.copyToRealmOrUpdate(sampleModel);
                    }
                }
            });
            RealmResults<SampleModel> results = rm.where(SampleModel.class).findAll();
            Log.d(TAG, "changeDataRunnable1: transaction ended, left = " + results.size());
            rm.close();
            sleep(5000);
            Log.d(TAG, "changeDataRunnable1: end");
        }
    };

    private Runnable changeDataRunnableParallel1 = new Runnable() {

        @Override
        public void run() {
            sleep(2000);
            Realm rm = Realm.getDefaultInstance();
            rm.executeTransaction(new Realm.Transaction() {

                @Override
                public void execute(Realm realm) {
                    for (int i = 2; i < 20; i++) {
                        realm.where(SampleModel.class)
                                .equalTo("id", i).findAll().deleteAllFromRealm();
                        Log.d(TAG, "execute: deleted id=" + i);
                        sleep(310);
                    }

                    for (int i = 200; i < 210; i++) {
                        SampleModel sampleModel = new SampleModel();
                        sampleModel.setId(i);
                        sampleModel.setName(String.format(Locale.US, "new item %d", i));
                        realm.copyToRealmOrUpdate(sampleModel);
                    }
                }
            });
            RealmResults<SampleModel> results = rm.where(SampleModel.class).findAll();
            Log.d(TAG, "changeDataRunnable1: transaction ended, left = " + results.size());
            rm.close();
            sleep(5000);
            Log.d(TAG, "changeDataRunnable1: end");
        }
    };

    /**
     * change data runnable
     */
    private Runnable changeDataRunnable2 = new Runnable() {

        @Override
        public void run() {
            sleep(2000);
            Realm rm = Realm.getDefaultInstance();
            rm.executeTransaction(new Realm.Transaction() {

                @Override
                public void execute(Realm realm) {
                    for (int i = 10; i < 20; i++) {
                        realm.where(SampleModel.class)
                                .equalTo("id", i).findAll().deleteAllFromRealm();
                        Log.d(TAG, "execute: deleted id=" + i);
                    }

                    for (int i = 10; i < 20; i++) {
                        SampleModel sampleModel = new SampleModel();
                        sampleModel.setId(i);
                        sampleModel.setName(String.format(Locale.US, "newest item %d", i));
                        realm.copyToRealmOrUpdate(sampleModel);
                    }
                }
            });
            RealmResults<SampleModel> results = rm.where(SampleModel.class).findAll();

            Log.d(TAG, "changeDataRunnable2: transaction ended, left = " + results.size());
            rm.close();
            Log.d(TAG, "changeDataRunnable2: end");
        }
    };

    private Runnable changeDataRunnable3 = new Runnable() {

        @Override
        public void run() {
            Realm rm = Realm.getDefaultInstance();
            rm.executeTransaction(new Realm.Transaction() {

                @Override
                public void execute(Realm realm) {

                    for (int i = 100; i < 110; i++) {
                        YetAnotherModel model = new YetAnotherModel();
                        model.setId(i);
                        model.setName(String.format(Locale.US, "another item %d", i));
                        model.setDescription(String.format(Locale.US, "item description %d", i));
                        realm.copyToRealmOrUpdate(model);
                    }
                }
            });
            RealmResults<SampleModel> results = rm.where(SampleModel.class).findAll();

            Log.d(TAG, "changeDataRunnable3: transaction ended, left = " + results.size());
            rm.close();
            Log.d(TAG, "changeDataRunnable3: end");
        }
    };

    /**
     * Sleep
     */
    private void sleep(int t) {
        try {
            Thread.sleep(t);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
