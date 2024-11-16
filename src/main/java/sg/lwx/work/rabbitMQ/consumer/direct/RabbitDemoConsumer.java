package sg.lwx.work.rabbitMQ.consumer.direct;

import com.alibaba.fastjson.JSONObject;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import sg.lwx.work.config.rabbit.RabbitMQConfig;

/**
 * @author lianwenxiu
 */
@Component
@RabbitListener(queues = {RabbitMQConfig.RABBITMQ_DEMO_TOPIC})
public class RabbitDemoConsumer {

    @RabbitHandler
    public void process(String jsonString){
        System.out.println("consumed: "+ JSONObject.parse(jsonString));
    }
}
