package im.zhaojun.zfile.module.inode.module.classify.imageClassify.messagequeue.producer;

import cn.hutool.json.JSONUtil;
import im.zhaojun.zfile.core.config.RabbitMqConfig;
import im.zhaojun.zfile.module.inode.module.classify.imageClassify.model.messagequeue.send.ClassifyMessageSend;
import org.springframework.amqp.rabbit.core.RabbitMessagingTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class ImageSendProducer {
    @Resource
    RabbitMessagingTemplate rabbitMessagingTemplate;

    public boolean sendMessage(ClassifyMessageSend classifyMessageSend){
        System.out.println("分类发送");
        rabbitMessagingTemplate.convertAndSend(RabbitMqConfig.EXCHANGE_NAME,
                RabbitMqConfig.QUEUE_IMAGE_SEND_NAME
                , JSONUtil.toJsonStr(classifyMessageSend));
        return true;
    }
}
