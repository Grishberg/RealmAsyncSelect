package com.grishberg.realmasynctest.data.models;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by grishberg on 15.06.16.
 */
public class SampleModel extends RealmObject {
    private static final String TAG = SampleModel.class.getSimpleName();
    @PrimaryKey
    private int id;
    private String name;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
