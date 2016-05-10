package com.sd.bugsbunny.Utils;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;
import com.rabbitmq.client.QueueingConsumer;
import com.sd.bugsbunny.Models.Message;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;



/**
 * Created by adrianodiasx93 on 5/8/16.
 */
public class Bunny {

    private static final String SERVER_URL = "alexpud.koding.io";


    private static Bunny INSTANCE;

    public static Bunny getInstance() {
        return INSTANCE;
    }

    private boolean durable = true;

    Thread subscribeThread;
    Thread publishThread;

    Context context;

    private BlockingDeque queue;

    ConnectionFactory factory;

    public void send(Message message) {
        Log.v("bunny", "send");
        try {
            Log.d("","[q] " + message);
            queue.putLast(message);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    private void setupConnectionFactory() {
        Log.v("bunny", "setup");
        factory.setAutomaticRecoveryEnabled(false);
        factory.setHost(SERVER_URL);
    }

    private Bunny() {
        queue = new LinkedBlockingDeque();
        factory = new ConnectionFactory();
        setupConnectionFactory();
    }

    public static Bunny getINSTANCE() {
        if (INSTANCE == null)
            INSTANCE = new Bunny();
        return INSTANCE;
    }

//    public void addOnMessageReceivedListener(OnMessageReceivedListener onMessageReceivedListener) {
//        RabbitMQManager.onMessageReceivedListenerList.add(onMessageReceivedListener);
//    }

    public void startService() throws IOException {


            subscribe();
            publishToAMQP();

    }


    public void publishToAMQP()
    {
        publishThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true) {
                    try {
                        Log.i("publishThread", "publishThread");
                        Connection connection = factory.newConnection();
                        Channel ch = connection.createChannel();
                        ch.confirmSelect();

                        while (true) {
                            Message message = (Message) queue.takeFirst();
                            try{
                                ch.queueDeclare(message.getReceiver(), durable, false, false, null);
                                Gson gson = new Gson();
                                ch.basicPublish("amq.direct", message.getReceiver(), MessageProperties.PERSISTENT_TEXT_PLAIN, gson.toJson(message).getBytes());
                                Log.d("", "[s] " + message);
                                ch.waitForConfirmsOrDie();
                            } catch (Exception e){
                                Log.d("","[f] " + message);
                                queue.putFirst(message);
                                throw e;
                            }
                        }
                    } catch (InterruptedException e) {
                        break;
                    } catch (Exception e) {
                        Log.d("", "Connection broken: " + e.getClass().getName());
                        try {
                            Thread.sleep(200); //sleep and then try again
                        } catch (InterruptedException e1) {
                            break;
                        }
                    }
                }
            }
        });
        publishThread.setName("publishThread");
        publishThread.setPriority(Thread.MAX_PRIORITY);
        publishThread.start();
    }



    void subscribe()
    {
        subscribeThread = new Thread(new Runnable() {
            @Override
            public void run() {

                while(true) {
                    try {
                        Log.i("subscribeThread", "subscribeThread");

                        Connection connection = factory.newConnection();
                        Channel channel = connection.createChannel();
                        channel.basicQos(1);
                        AMQP.Queue.DeclareOk q = channel.queueDeclare("adrianodiasx93", true, false, false, null);
                        channel.queueBind(q.getQueue(), "amq.direct", "adrianodiasx93");
                        QueueingConsumer consumer = new QueueingConsumer(channel);
//                        channel.basicConsume(q.getQueue(), true, consumer);

                        while (true) {
                            QueueingConsumer.Delivery delivery = consumer.nextDelivery();

//                            String message = new String(delivery.getBody());
//                            Log.d("","[r] " + message);
//
//                            android.os.Message msg = handler.obtainMessage();
//                            Bundle bundle = new Bundle();
//
//                            bundle.putString("msg", message);
//                            msg.setData(bundle);
//                            handler.sendMessage(msg);
//
//                            QueueingConsumer.Delivery delivery = consumer.nextDelivery();
//
//                            Gson gson = new Gson();
//                            Message message = gson.fromJson(new String(delivery.getBody()), Message.class);
//                            for (OnMessageReceivedListener l : onMessageReceivedListenerList) {
//                                l.onMessageReceived(message);
//                            }
                        }
                    } catch (InterruptedException e) {
                        break;
                    } catch (Exception e1) {
                        Log.e("", "Connection broken: " + e1.getClass().getName());
                        try {
                            Thread.sleep(200); //sleep and then try again
                        } catch (InterruptedException e) {
                            break;
                        }
                    }
                }
            }
        });
        subscribeThread.setName("subscribeThread");
        subscribeThread.setPriority(Thread.MAX_PRIORITY);
        subscribeThread.start();
    }

    public void setContext(Context context) {
        destroy();
        reset();
        INSTANCE.context = context;

    }


    public void reset(){
        INSTANCE = new Bunny();
    }

    public void destroy(){
        if(publishThread!=null)
            publishThread.interrupt();
        if(subscribeThread!=null)
            subscribeThread.interrupt();
    }
}
