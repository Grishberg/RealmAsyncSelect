package com.grishberg.realmasynctest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.grishberg.realmasynctest.adapters.SampleAdapter;
import com.grishberg.realmasynctest.models.SampleModel;

import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * sample activity for test async data changing in Realm 1.0
 */
public class MainActivity extends AppCompatActivity {
    public static final String TAG = MainActivity.class.getSimpleName();
    public static final int COUNT = 20;

    private Realm realm;
    private SampleAdapter adapter;
    private RecyclerView rv;
    private Button btChangeData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // create sample data
        createData();

        // init Realm instance
        realm = Realm.getDefaultInstance();

        initDataQuery();

        initViews();

        // start changing data in different thread
        Log.d(TAG, "testSync: start");
        new Thread(changeDataRunnable1).start();

    }

    /**
     * init views
     */
    private void initViews() {
        rv = (RecyclerView) findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setItemAnimator(new DefaultItemAnimator());
        rv.setAdapter(adapter);
        rv.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));

        btChangeData = (Button) findViewById(R.id.btChangeData);
        btChangeData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(changeDataRunnable2).start();
            }
        });
    }

    /**
     * init data query for adapter
     */
    private void initDataQuery() {
        RealmResults<SampleModel> results = realm.where(SampleModel.class)
                .findAllSortedAsync("id", Sort.ASCENDING);
        Log.d(TAG, "onCreate: " + results.isLoaded());

        Log.d(TAG, "testSync: end");
        adapter = new SampleAdapter(results);
    }

    /**
     * Create sample data
     */
    private void createData() {
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        adapter.release();
        realm.close();
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
                    for (int i = 0; i < 10; i++) {
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

            Log.d(TAG, "run: transaction ended, left = " + results.size());
            rm.close();
            Log.d(TAG, "run: end");
        }
    };

    /**
     * change data runnable
     */
    private Runnable changeDataRunnable2 = new Runnable() {

        @Override
        public void run() {
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

            Log.d(TAG, "run: transaction ended, left = " + results.size());
            rm.close();
            Log.d(TAG, "run: end");
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
