package sg.lwx.work.rabbitMQ.consumer.topic;

import com.alibaba.fastjson.JSONObject;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import sg.lwx.work.config.rabbit.RabbitMQConfig;

import java.util.Map;

@Component
@RabbitListener(queuesToDeclare = @Queue(RabbitMQConfig.TOPIC_EXCHANGE_QUEUE_A))
public class TopicExchangeConsumerA {

    @RabbitHandler
    public void process(String jsonString) {
        System.out.println("queue[" + RabbitMQConfig.TOPIC_EXCHANGE_QUEUE_A + "]received message: " + JSONObject.parse(jsonString));
    }
}
