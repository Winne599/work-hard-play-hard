package sg.lwx.work.rabbitMQ.consumer.headers;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import sg.lwx.work.config.rabbit.RabbitMQConfig;

@Component
public class HeadersExchangeConsumerB {

    @RabbitListener(queuesToDeclare = @Queue(RabbitMQConfig.HEADERS_EXCHANGE_QUEUE_B))
    public void process(Message message) throws Exception {

        MessageProperties messageProperties = message.getMessageProperties();
        String contentType = messageProperties.getContentType();
        System.out.println("queue[" + RabbitMQConfig.HEADERS_EXCHANGE_QUEUE_B + "]received message: " + new String(message.getBody(), contentType));
    }
}
