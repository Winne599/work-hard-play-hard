package sg.lwx.work.service.Impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import sg.lwx.work.config.rabbit.RabbitMQConfig;
import sg.lwx.work.service.RabbitMQService;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author lianwenxiu
 */
@Service
public class RabbitMQServiceImpl implements RabbitMQService {
    private static final Logger LOGGER = LoggerFactory.getLogger(RabbitMQServiceImpl.class);

    /**
     * 日期格式化
     */
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Resource
    private RabbitTemplate rabbitTemplate;

    @Override
    public String sendMsg(String msg) {
        try {
            String msgId = UUID.randomUUID().toString().replace("-", "").substring(0, 32);
            String sendTime = sdf.format(new Date());
            JSONObject content = new JSONObject();
            content.put("msgId", msgId);
            content.put("sendTime", sendTime);
            content.put("msg", msg);
            LOGGER.info("send message, content: {}", content);
            //  exchange, routingKey, object (object最好转为String，要不Rabbit管理后台会自动显示成base64格式）
            rabbitTemplate.convertAndSend(RabbitMQConfig.RABBITMQ_DEMO_DIRECT_EXCHANGE, RabbitMQConfig.RABBITMQ_DEMO_DIRECT_ROUTING, JSON.toJSONString(content));

            return "ok";
        } catch (Exception e) {
            LOGGER.error("sendMsg error, error: {}", e.getMessage(), e);
            return "error";
        }
    }

    /**
     *  发布消息
     * @param msg
     * @return
     * @throws Exception
     */
    @Override
    public String sendMsgByFanoutExchange(String msg) throws Exception {
       JSONObject message = getMessage(msg);
        try {
            rabbitTemplate.convertAndSend(RabbitMQConfig.FANOUT_EXCHANGE_DEMO_NAME, "", message.toString());
            return "ok";
        } catch (Exception e) {
            LOGGER.error("sendMsg fanout error, error: {}", e.getMessage(), e);
            return "error";
        }
    }

    @Override
    public String sendMsgByTopicExchange(String msg, String routingKey)  {
        JSONObject message = getMessage(msg);
        try {
            //发送消息
            rabbitTemplate.convertAndSend(RabbitMQConfig.TOPIC_EXCHANGE_DEMO_NAME, routingKey, message.toString());
            return "ok";
        } catch (Exception e) {
            LOGGER.error("sendMsg topic error, error: {}", e.getMessage(), e);
            return "error";
        }
    }

    /**
     * 组装消息体
     * @param msg
     * @return
     */
    private JSONObject getMessage(String msg) {
        String msgId = UUID.randomUUID().toString().replace("-", "").substring(0, 32);
        String sendTime = sdf.format(new Date());
        JSONObject content = new JSONObject();
        content.put("msgId", msgId);
        content.put("sendTime", sendTime);
        content.put("msg", msg);
        return content;
    }



}
