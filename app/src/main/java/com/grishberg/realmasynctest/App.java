package com.grishberg.realmasynctest;

import android.app.Application;
import android.util.Log;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by grishberg on 15.06.16.
 */
public class App extends Application {
    private static final String TAG = App.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();
        initRealm();
    }

    private void initRealm() {
        Log.d(TAG, "initRealm() starts");
        RealmConfiguration.Builder builder = new RealmConfiguration.Builder(getApplicationContext())
                .schemaVersion(1);

        builder.deleteRealmIfMigrationNeeded();
        RealmConfiguration defaultConfiguration = builder.build();
        Realm.setDefaultConfiguration(defaultConfiguration);

        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
        } catch (Exception exception) {
            Log.e(TAG, "Can't migrate realm, deleting realm base to not crush", exception);
            Realm.deleteRealm(defaultConfiguration);
        } finally {
            if (realm != null) {
                realm.close();
            }
        }
    }
}
