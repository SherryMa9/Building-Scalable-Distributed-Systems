package Client2;

import com.rabbitmq.client.*;
        import java.nio.charset.StandardCharsets;

public class RabbitMQConsumer {
    private static final String QUEUE_NAME = "skier1";
    private static final String RABBITMQ_HOST = "CS6650-40527052.us-west-2.elb.amazonaws.com";

    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(RABBITMQ_HOST);
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {

            channel.queueDeclare(QUEUE_NAME, true, false, false, null);
            System.out.println("Waiting for messages.");

            // Process the message received from RabbitMQ
            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
                System.out.println("Received '" + message + "'");
            };
            channel.basicConsume(QUEUE_NAME, true, deliverCallback, consumerTag -> {});
        }
    }
}
