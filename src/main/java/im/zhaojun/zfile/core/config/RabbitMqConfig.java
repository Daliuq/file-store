package im.zhaojun.zfile.core.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;

public class RabbitMqConfig {
    public final static String EXCHANGE_NAME = "zfile_direct_exchange";
    public final static String QUEUE_TEXT_SEND_NAME = "text_send";
    public final static String QUEUE_CLASSIFY_RECEIVE_NAME = "classify_receive";
    @Bean
    public DirectExchange directExchange() {
        return new DirectExchange(EXCHANGE_NAME);
    }

    @Bean
    public Queue textSendQueue() {
        return new Queue(QUEUE_TEXT_SEND_NAME, true, false, false);
    }

    @Bean
    public Queue classifyReceiveQueue() {
        return new Queue(QUEUE_CLASSIFY_RECEIVE_NAME, true, false, false);
    }
    @Bean
    public Binding textSendQueueBinding(FanoutExchange fanoutExchange, Queue textSendQueue) {
        return BindingBuilder.bind(textSendQueue).to(fanoutExchange);
    }
    @Bean
    public Binding classifyReceiveQueueBinding(FanoutExchange fanoutExchange, Queue classifyReceiveQueue) {
        return BindingBuilder.bind(classifyReceiveQueue).to(fanoutExchange);
    }
}
