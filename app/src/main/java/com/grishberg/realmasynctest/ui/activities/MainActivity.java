package com.grishberg.realmasynctest.ui.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.grishberg.realmasynctest.R;
import com.grishberg.realmasynctest.data.DataManager;
import com.grishberg.realmasynctest.ui.adapters.DividerItemDecoration;
import com.grishberg.realmasynctest.ui.adapters.SampleAdapter;
import com.grishberg.realmasynctest.data.models.SampleModel;

import io.realm.RealmResults;

/**
 * sample activity for test async data changing in Realm 1.0
 */
public class MainActivity extends AppCompatActivity {
    public static final String TAG = MainActivity.class.getSimpleName();

    private SampleAdapter adapter;
    private RecyclerView rv;
    private Button btChangeData;
    private Button btChangeOtherData;
    private DataManager dataManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // init Realm instance
        dataManager = new DataManager();

        // create sample data
        dataManager.createData();

        initDataQuery();

        initViews();

        // start changing data in different thread
        Log.d(TAG, "testSync: start");
        dataManager.startFirstDataChange();
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
                dataManager.startSecondDataChange();
            }
        });

        btChangeOtherData = (Button)findViewById(R.id.btChangeOtherData);
        btChangeOtherData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataManager.startOtherDataChange();
            }
        });
    }

    /**
     * init data query for adapter
     */
    private void initDataQuery() {
        RealmResults<SampleModel> results = dataManager.getDataQuery();
        Log.d(TAG, "initDataQuery: isLoaded = " + results.isLoaded());
        adapter = new SampleAdapter(this, results);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        adapter.release();
        if (dataManager != null) {
            dataManager.release();
            dataManager = null;
        }
    }
}
