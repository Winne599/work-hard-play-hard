package sg.lwx.work.controller;

import com.alibaba.fastjson.JSONObject;
import org.springframework.web.bind.annotation.*;
import sg.lwx.work.service.RabbitMQService;

import javax.annotation.Resource;

@RestController
@RequestMapping("/rabbitmq")
public class RabbitMQController {
    @Resource
    private RabbitMQService rabbitMQService;

    /**
     * 发送消息 Direct exchange 类型
     *
     * @author java技术爱好者
     */
    @PostMapping("/sendMsg")
    public String sendMsg(@RequestParam(name = "msg") String msg) {
        return rabbitMQService.sendMsg(msg);
    }

    /**
     * 发布消息 fanout exchange 类型
     *
     * @author java技术爱好者
     */
    @PostMapping("/publish")
    public String publish(@RequestParam(name = "msg") String msg) throws Exception {
        return rabbitMQService.sendMsgByFanoutExchange(msg);
    }


    /**
     * 通配符交换机(topic exchange) 发送消息
     *
     * @author java技术爱好者
     */
    @PostMapping("/topicSend")
    public String topicSend(@RequestParam(name = "msg") String msg, @RequestParam(name = "routingKey") String routingKey) throws Exception {
        return rabbitMQService.sendMsgByTopicExchange(msg, routingKey);
    }

    /**
     * headers exchange 发送消息
     *
     * @param msg
     * @param json
     * @return
     * @throws Exception
     */
    @PostMapping("/headersSend")
    @SuppressWarnings("unchecked")
    public String headersSend(@RequestParam(name = "msg") String msg,
                              @RequestBody JSONObject json) {
        return rabbitMQService.sendMsgByHeadersExchange(msg, json);
    }
}
