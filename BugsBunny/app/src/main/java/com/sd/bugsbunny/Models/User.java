package com.sd.bugsbunny.Models;

import io.realm.RealmObject;
import io.realm.annotations.Required;

/**
 * Created by adrianodiasx93 on 5/8/16.
 */
public class User extends RealmObject {

    @Required
    private String name;

    public User() {
    }

    public User(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof User)
            return name.equals(((User)o).getName());
        return false;
    }

}