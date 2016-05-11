package com.sd.bugsbunny.Singleton;

import android.content.Context;

import com.sd.bugsbunny.Models.Message;
import com.sd.bugsbunny.Models.User;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmObject;
import io.realm.RealmResults;

/**
 * Created by adrianodiasx93 on 5/10/16.
 */
public class Databaser {

    private static Databaser INSTANCE;

    RealmConfiguration realmConfig;

    Realm realm;

    Context context;

    private Databaser() {}

    public static Databaser getINSTANCE() {
        if (INSTANCE == null)
            INSTANCE = new Databaser();
        return INSTANCE;
    }

    public void setContext(Context context) {
        destroy();
        reset();
        INSTANCE.context = context;

        INSTANCE.realmConfig = new RealmConfiguration.Builder(context).build();

    }

    public void createOrAcessUser(String user){
        Sender.getINSTANCE().setUsername(user);
        if(!isUserCreated(user))
            saveToDatabase(new User(user));
    }

    public void saveToDatabase(RealmObject realmObject) {
        if(context==null)
            throw new RuntimeException("chame setContext antes.");
        realm = Realm.getInstance(realmConfig);
        realm.beginTransaction();
        realm.copyToRealm(realmObject);
        realm.commitTransaction();
    }

    public boolean isUserCreated(String user){
        realm = Realm.getInstance(realmConfig);
        RealmResults<User> r = realm.where(User.class)
                .equalTo("name", user)
                .findAll();
        return r.size()>0;
    }

    public RealmResults<Message> getAllChatMessagesOf(String user, String buddy){
        if(context==null)
            throw new RuntimeException("chame setContext antes.");
        realm = Realm.getInstance(realmConfig);
        RealmResults<Message> messages = realm.where(Message.class)
                .beginGroup()
                    .equalTo("sender", user)
                    .equalTo("receiver", buddy)
                .endGroup()
                .or()
                .beginGroup()
                    .equalTo("sender", buddy)
                    .equalTo("receiver", user)
                .endGroup()
                .findAll();

        return messages;
    }

    public Message getLastMessage(String user, String buddy){
        RealmResults<Message> messages = getAllChatMessagesOf(user, buddy);

        return messages.last();
    }

    public void reset(){
        INSTANCE = new Databaser();
    }

    public void destroy(){
//        if(publishThread!=null)
//            publishThread.interrupt();
//        if(subscribeThread!=null)
//            subscribeThread.interrupt();
    }
}
