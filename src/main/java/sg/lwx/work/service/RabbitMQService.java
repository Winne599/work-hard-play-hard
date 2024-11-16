package sg.lwx.work.service;

import com.alibaba.fastjson.JSONObject;

public interface RabbitMQService {

    String sendMsg(String msg);

    String sendMsgByFanoutExchange(String msg) throws Exception;

    String sendMsgByTopicExchange(String msg, String routingKey);

    String sendMsgByHeadersExchange(String msg, JSONObject jsonObject);
}
