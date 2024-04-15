package im.zhaojun.zfile.module.inode.addition_module.textClassify.messagequeue.consumer;

import com.alibaba.fastjson2.JSON;
import im.zhaojun.zfile.core.config.RabbitMqConfig;
import im.zhaojun.zfile.module.inode.addition_module.textClassify.model.messagequeue.receive.ClassifyMessageReceive;
import im.zhaojun.zfile.module.inode.model.entity.InodeAdditionMessage;
import im.zhaojun.zfile.module.inode.model.enums.FileTypeEnum;
import im.zhaojun.zfile.module.inode.service.impl.InodeAdditionMessageServiceImpl;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collections;

@Component
public class ClassifyReceiveConsumer {
    @Resource
    InodeAdditionMessageServiceImpl inodeAdditionMessageService;
    @RabbitListener(queues = RabbitMqConfig.QUEUE_CLASSIFY_RECEIVE_NAME)
    public void receiveMessage(String message) {
        //1.将message（json）加载成ClassMessageReceive类
        ClassifyMessageReceive classifyMessageReceive = JSON.parseObject(message, ClassifyMessageReceive.class);
        //2.调用mongodb，存入该索引的信息到inode表
        InodeAdditionMessage inodeAdditionMessage=new InodeAdditionMessage();
        System.out.println(classifyMessageReceive);
        inodeAdditionMessage.setId(classifyMessageReceive.getInodeId());
        inodeAdditionMessage.setFileClassifyType(Collections.singletonList(classifyMessageReceive.getTextClass()));
        inodeAdditionMessage.setFileType(FileTypeEnum.text);
        //2.2
        inodeAdditionMessageService.save(inodeAdditionMessage);
    }
}
