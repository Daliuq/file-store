package im.zhaojun.zfile.module.inode.module.classify.textClassify.messagequeue.producer;

import cn.hutool.json.JSONUtil;
import im.zhaojun.zfile.core.config.RabbitMqConfig;
import im.zhaojun.zfile.module.inode.module.classify.textClassify.model.messagequeue.send.ClassifyMessageSend;
import org.springframework.amqp.rabbit.core.RabbitMessagingTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class TextSendProducer {
    @Resource
    RabbitMessagingTemplate rabbitMessagingTemplate;

    public boolean sendMessage(ClassifyMessageSend classifyMessageSend){

        rabbitMessagingTemplate.convertAndSend(RabbitMqConfig.EXCHANGE_NAME,
                RabbitMqConfig.QUEUE_TEXT_SEND_NAME
                , JSONUtil.toJsonStr(classifyMessageSend));
        return true;
    }
}
