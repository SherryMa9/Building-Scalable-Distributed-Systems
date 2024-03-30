package Client;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import com.google.gson.Gson;
import DAO.DailySkiRecordDao;
import DAO.LiftRecordDao;
import DAO.DailySkiRecordDaoImpl;
import DAO.LiftRecordDaoImpl;

import java.nio.charset.StandardCharsets;


public class RabbitMQConsumer {
    private static final String QUEUE_NAME = "skier1";
    private static final String RABBITMQ_HOST = "ASS3-e2aa9a90164b889f.elb.us-west-2.amazonaws.com";

    public static void main(String[] argv) throws Exception {
        // MongoDB DAOs
        DailySkiRecordDao dailySkiRecordDao = new DailySkiRecordDaoImpl();
        LiftRecordDao liftRecordDao = new LiftRecordDaoImpl();

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(RABBITMQ_HOST);
        factory.setUsername("");
        factory.setPassword("");

        factory.setHost(RABBITMQ_HOST);
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {

            channel.queueDeclare(QUEUE_NAME, true, false, false, null);
            System.out.println("Waiting for messages.");

            // Process the message received from RabbitMQ
            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
                Gson gson = new Gson();
                LiftRideEvent event = gson.fromJson(message, LiftRideEvent.class);

                try {
                    liftRecordDao.insertLiftRide(event);
                    dailySkiRecordDao.updateDailySkiRecord(event);

                    System.out.println("Processed message: " + message);
                } catch (Exception e) {
                    System.err.println("Error processing message: " + message);
                    e.printStackTrace();
                }
            };

            channel.basicConsume(QUEUE_NAME, true, deliverCallback, consumerTag -> {});
        }
    }
}