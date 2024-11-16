package sg.lwx.work.rabbitMQ.consumer.fanout;

import com.alibaba.fastjson.JSONObject;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import sg.lwx.work.config.rabbit.RabbitMQConfig;

import java.util.Map;

@Component
@RabbitListener(queuesToDeclare = @Queue(RabbitMQConfig.FANOUT_EXCHANGE_QUEUE_TOPIC_A))
public class FanoutExchangeConsumerA {

    @RabbitHandler
    public void process(String jsonString) {
        System.out.println("fanout consumerA consumed: " + JSONObject.parse(jsonString));
    }

}
