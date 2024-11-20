package sg.lwx.work.config.rabbit.topic;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import sg.lwx.work.config.rabbit.RabbitMQConfig;

import javax.annotation.Resource;

@Component
public class TopicRabbitConfig implements BeanPostProcessor {

    @Resource
    private RabbitAdmin rabbitAdmin;

    // 这里是在 FanoutRabbitConfig里初始化了，所以不用再初始化一次，如果整个项目没有，就需要声明一下
//    /**
//     * 初始化rabbitAdmin对象(没有这个方法，会报错找不到 rabbitAdmin Bean）
//     */
//    @Bean
//    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
//        RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory);
//        // 只有设置为 true，spring 才会加载 RabbitAdmin 这个类
//        rabbitAdmin.setAutoStartup(true);
//        return rabbitAdmin;
//    }

    @Bean
    public TopicExchange rabbitmqDemoTopicExchange() {
        //配置TopicExchange交换机
        return new TopicExchange(RabbitMQConfig.TOPIC_EXCHANGE_DEMO_NAME, true, false);
    }

    @Bean
    public Queue topicExchangeQueueA() {
        //创建队列1
        return new Queue(RabbitMQConfig.TOPIC_EXCHANGE_QUEUE_A, true, false, false);
    }

    @Bean
    public Queue topicExchangeQueueB() {
        //创建队列2
        return new Queue(RabbitMQConfig.TOPIC_EXCHANGE_QUEUE_B, true, false, false);
    }

    @Bean
    public Queue topicExchangeQueueC() {
        //创建队列3
        return new Queue(RabbitMQConfig.TOPIC_EXCHANGE_QUEUE_C, true, false, false);
    }

    /**
     * 可以匹配 a.c或者 a.b 这种，不能匹配 a.b.c这种，也就是*只能代表一个元素
     * @return
     */
    @Bean
    public Binding bindTopicA() {
        //队列A绑定到FanoutExchange交换机
        return BindingBuilder.bind(topicExchangeQueueA())
                .to(rabbitmqDemoTopicExchange())
                .with("a");
    }

    /**
     * 可以匹配 rabbit.b.a.jjj或者 rabbit.b.a这种，也就是#可以匹配多个元素
     * @return
     */
    @Bean
    public Binding bindTopicB() {
        //队列A绑定到FanoutExchange交换机
        return BindingBuilder.bind(topicExchangeQueueB())
                .to(rabbitmqDemoTopicExchange())
                .with("rabbit.b.#");
    }

    @Bean
    public Binding bindTopicC() {
        //队列A绑定到FanoutExchange交换机
        return BindingBuilder.bind(topicExchangeQueueC())
                .to(rabbitmqDemoTopicExchange())
                .with("rabbit.#");
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        rabbitAdmin.declareExchange(rabbitmqDemoTopicExchange());
        rabbitAdmin.declareQueue(topicExchangeQueueA());
        rabbitAdmin.declareQueue(topicExchangeQueueB());
        rabbitAdmin.declareQueue(topicExchangeQueueC());
        return null;
    }
}
