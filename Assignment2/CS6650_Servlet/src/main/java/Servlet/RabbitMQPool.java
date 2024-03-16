package Servlet;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class RabbitMQPool {
    private static final String RABBITMQ_HOST = "localhost"; // Or the private IP if RabbitMQ is on a different EC2 instance
    private static final int POOL_SIZE = 10; // This can be adjusted based on the expected concurrency
    private static final String QUEUE_NAME = "skier1"; // Ensure this queue exists in RabbitMQ
    private static Connection connection;
    private static BlockingQueue<Channel> channelPool = new LinkedBlockingQueue<>();

    public static String getQueueName() {
        return QUEUE_NAME;
    }

    public static void initializePool() throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(RABBITMQ_HOST);
        connection = factory.newConnection();

        for (int i = 0; i < POOL_SIZE; i++) {
            Channel channel = connection.createChannel();
            channel.queueDeclare(QUEUE_NAME, true, false, false, null);
            channelPool.put(channel);
        }
    }

    public static Channel getChannel() throws InterruptedException {
        return channelPool.take();
    }

    public static void returnChannel(Channel channel) throws InterruptedException, IOException {
        if (channel != null && channel.isOpen()) {
            channelPool.put(channel);
        } else {
            // Re-create channel if it's closed
            channel = connection.createChannel();
            channel.queueDeclare(QUEUE_NAME, true, false, false, null);
            channelPool.put(channel);
        }
    }

    public static void closePool() {
        try {
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
