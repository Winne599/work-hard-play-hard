package sg.lwx.work.service;

public interface RabbitMQService {

    String sendMsg(String msg);

    String sendMsgByFanoutExchange(String msg) throws Exception;

     String sendMsgByTopicExchange(String msg, String routingKey) ;

    }
