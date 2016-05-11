package com.sd.bugsbunny.Utils;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.google.gson.Gson;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;
import com.rabbitmq.client.QueueingConsumer;
import com.sd.bugsbunny.Models.Message;

import java.io.IOException;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Created by adrianodiasx93 on 5/10/16.
 */
public class Databaser {

    private static final String SERVER_URL = "alexpud.koding.io";


    private static Databaser INSTANCE;

    public static Databaser getInstance() {
        return INSTANCE;
    }

    RealmConfiguration realmConfig;

    Realm realm;





    Context context;







    private Databaser() {

    }

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

    public void saveToDatabase(RealmObject realmObject) {
        if(context==null)
            throw new RuntimeException("chame setContext antes.");
        realm = Realm.getInstance(realmConfig);
        realm.beginTransaction();
        realm.copyToRealm(realmObject);
        realm.commitTransaction();
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
