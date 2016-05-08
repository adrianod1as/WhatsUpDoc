package com.sd.bugsbunny.Utils;

import android.content.Context;

import com.sd.bugsbunny.Models.User;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmQuery;
import io.realm.RealmResults;

/**
 * Created by adrianodiasx93 on 5/8/16.
 */
public class DatabaseDAO {

    RealmConfiguration realmConfig;

    Realm realm;

    Context context;

    public List<User> getUsers() {
        if(context==null)
            throw new RuntimeException("chame setContext antes.");
        realm = Realm.getInstance(realmConfig);
        RealmQuery<User> query = realm.where(User.class);
        RealmResults<User> users = query.findAll();
        return users.subList(0, users.size());
    }

    public void saveUser(User user) {
        if(context==null)
            throw new RuntimeException("chame setContext antes.");
        realm = Realm.getInstance(realmConfig);
        realm.beginTransaction();
        realm.copyToRealm(user);
        realm.commitTransaction();
    }

    public boolean isUserCreated(String user){
        realm = Realm.getInstance(realmConfig);
        RealmResults<User> r = realm.where(User.class)
                .equalTo("name", user)
                .findAll();
        return r.size()>0;
    }
}
