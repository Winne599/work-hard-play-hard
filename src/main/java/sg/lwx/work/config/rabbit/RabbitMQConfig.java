package sg.lwx.work.config.rabbit;

/**
 * @author lianwenxiu
 */
public class RabbitMQConfig {

/**
 * direct 类型
 */
    /**
     * RabbitMQ 的队列主题名称
     */
    public static final String RABBITMQ_DEMO_TOPIC = "rabbitmqDemoTopic";

    /**
     * RabbitMQ 的 Direct 交换机名称
     */
    public static final String RABBITMQ_DEMO_DIRECT_EXCHANGE = "rabbitmqDemoDirectExchange";

    /**
     * RabbitMQ 的 Direct 交换机和队列绑定的匹配键 DirectRouting
     */
    public static final String RABBITMQ_DEMO_DIRECT_ROUTING = "rabbitmqDemoDirectRouting";


/**
 * fanout 类型
 */

    /**
     * RabbitMQ的FANOUT_EXCHANG交换机类型的队列 A 的名称
     */
    public static final String FANOUT_EXCHANGE_QUEUE_TOPIC_A = "fanout.A";

    /**
     * RabbitMQ的FANOUT_EXCHANG交换机类型的队列 B 的名称
     */
    public static final String FANOUT_EXCHANGE_QUEUE_TOPIC_B = "fanout.B";

    /**
     * RabbitMQ的FANOUT_EXCHANG交换机类型的名称
     */
    public static final String FANOUT_EXCHANGE_DEMO_NAME = "fanout.exchange.demo.name";


/**
 * topic 类型
 * 通配符规则：
 * * 符号：有且只匹配一个词。比如 a.*可以匹配到"a.b"、"a.c"，但是匹配不了"a.b.c"。
 * # 符号：匹配一个或多个词。比如"rabbit.#"既可以匹配到"rabbit.a.b"、"rabbit.a"，也可以匹配到"rabbit.a.b.c"。
 */

    /**
     * RabbitMQ的TOPIC_EXCHANGE交换机名称
     */
    public static final String TOPIC_EXCHANGE_DEMO_NAME = "topic.exchange.demo.name";

    /**
     * RabbitMQ的TOPIC_EXCHANGE交换机的队列A的名称
     */
    public static final String TOPIC_EXCHANGE_QUEUE_A = "topic.queue.a";

    /**
     * RabbitMQ的TOPIC_EXCHANGE交换机的队列B的名称
     */
    public static final String TOPIC_EXCHANGE_QUEUE_B = "topic.queue.b";

    /**
     * RabbitMQ的TOPIC_EXCHANGE交换机的队列C的名称
     */
    public static final String TOPIC_EXCHANGE_QUEUE_C = "topic.queue.c";


/**
 * headers 类型
 */

    /**
     * HEADERS_EXCHANGE交换机名称
     */
    public static final String HEADERS_EXCHANGE_DEMO_NAME = "headers.exchange.demo.name";

    /**
     * RabbitMQ的HEADERS_EXCHANGE交换机的队列A的名称
     */
    public static final String HEADERS_EXCHANGE_QUEUE_A = "headers.queue.a";

    /**
     * RabbitMQ的HEADERS_EXCHANGE交换机的队列B的名称
     */
    public static final String HEADERS_EXCHANGE_QUEUE_B = "headers.queue.b";
}
