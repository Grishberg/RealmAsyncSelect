package com.grishberg.realmasynctest;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.SmallTest;
import android.util.Log;

import com.grishberg.realmasynctest.data.models.SampleModel;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Locale;
import java.util.concurrent.CountDownLatch;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
@RunWith(AndroidJUnit4.class)
@SmallTest
public class ApplicationTest {

    public static final int COUNT = 30;
    private static final String TAG = ApplicationTest.class.getSimpleName();

    private final CountDownLatch countDownLatch = new CountDownLatch(1);
    private volatile boolean isRunning = true;

    //public ApplicationTest() {
    //super(Application.class);
    //}

    @Before
    public void setUp() throws Exception {
        App app = (App) InstrumentationRegistry.getTargetContext().getApplicationContext();
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

    /**
     * remove data runnable
     */
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            Realm rm = Realm.getDefaultInstance();
            rm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    for (int i = 0; i < COUNT; i++) {
                        realm.where(SampleModel.class)
                                .equalTo("id", i).findAll().deleteAllFromRealm();
                        Log.d(TAG, "execute: deleted id=" + i);
                        sleep(500);
                    }
                }
            });
            RealmResults<SampleModel> results = rm.where(SampleModel.class).findAll();

            Log.d(TAG, "run: transaction ended, left = " + results.size());
            rm.close();
            sleep(5000);
            Log.d(TAG, "run: end");
            isRunning = false;
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

    /**
     * Тест синхронного результата запроса, пока в другом потоке идет удаление данных
     */
    @Test
    public void testSync() {
        Log.d(TAG, "testSync: start");
        new Thread(runnable).start();

        Realm realm = Realm.getDefaultInstance();
        RealmResults<SampleModel> results = realm.where(SampleModel.class).findAll();
        while (isRunning) {
            for (int i = 0; i < results.size(); i++) {
                SampleModel model = results.get(i);
                if (!model.isValid()) {
                    Log.e(TAG, "testSync: is not valid pos =" + i);
                }
            }
        }
        realm.close();
        Log.d(TAG, "testSync: end");
    }

    @Test
    public void testAsync() {
        Log.d(TAG, "testAsync: start");
        new Thread(runnable).start();
        Realm realm = Realm.getDefaultInstance();
        RealmResults<SampleModel> results = realm.where(SampleModel.class).findAllAsync();
        RealmChangeListener callback = new RealmChangeListener() {
            @Override
            public void onChange(Object element) {
                Log.d(TAG, "onChange: " + element);
            }
        };
        results.addChangeListener(callback);
        while (isRunning) {
            for (int i = 0; i < results.size(); i++) {
                SampleModel model = results.get(i);
                if (!model.isValid()) {
                    Log.e(TAG, "testSync: is not valid pos =" + i);
                }
            }
        }
        realm.close();
        Log.d(TAG, "testSync: end");
    }
}