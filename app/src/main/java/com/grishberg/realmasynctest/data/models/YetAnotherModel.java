package com.grishberg.realmasynctest.data.models;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by grishberg on 16.06.16.
 */
public class YetAnotherModel extends RealmObject {
    private static final String TAG = YetAnotherModel.class.getSimpleName();

    @PrimaryKey
    private int id;
    private String name;
    private String description;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
