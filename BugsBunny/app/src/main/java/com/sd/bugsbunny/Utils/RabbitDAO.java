package com.sd.bugsbunny.Utils;

import com.rabbitmq.client.ConnectionFactory;

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Created by adrianodiasx93 on 5/8/16.
 */
public class RabbitDAO {

    private static final String SERVER_URL = "alexpud.koding.io";
    private static RabbitDAO INSTANCE;

    Thread subscribeThread;
    Thread publishThread;

    private BlockingDeque<String> queue;

    ConnectionFactory factory;

    private void setupConnectionFactory() {
        factory.setAutomaticRecoveryEnabled(false);
        factory.setHost(SERVER_URL);
    }



}
