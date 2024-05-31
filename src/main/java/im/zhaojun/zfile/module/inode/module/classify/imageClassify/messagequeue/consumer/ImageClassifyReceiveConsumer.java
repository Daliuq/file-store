package im.zhaojun.zfile.module.inode.module.classify.imageClassify.messagequeue.consumer;

import com.alibaba.fastjson2.JSON;
import im.zhaojun.zfile.core.config.RabbitMqConfig;
import im.zhaojun.zfile.module.inode.model.entity.InodeAdditionMessage;
import im.zhaojun.zfile.module.inode.model.enums.FileTypeEnum;
import im.zhaojun.zfile.module.inode.module.classify.imageClassify.model.messagequeue.receive.ClassifyMessageReceive;
import im.zhaojun.zfile.module.inode.module.classify.imageClassify.model.messagequeue.receive.base.ResultItem;
import im.zhaojun.zfile.module.inode.service.impl.InodeAdditionMessageServiceImpl;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Resource;
import java.util.Collections;

@Component
public class ImageClassifyReceiveConsumer {
    @Resource
    InodeAdditionMessageServiceImpl inodeAdditionMessageService;
    @RabbitListener(queues = RabbitMqConfig.QUEUE_IMAGE_CLASSIFY_RECEIVE_NAME)
    public void receiveMessage(String message) {
        ClassifyMessageReceive classifyMessageReceive= JSON.parseObject(message,ClassifyMessageReceive.class);
        List<String> highScoreList = new ArrayList<>();
        List<String> lowScoreList = new ArrayList<>();

        // Separate the items into the two lists
        for (ResultItem item : classifyMessageReceive.getResult()) {
            if (item.getScore() > 0.6) {
                highScoreList.add(item.getKey());
            } else {
                lowScoreList.add(item.getKey());
            }
        }

        System.out.println(classifyMessageReceive);

        //2.调用mongodb，存入该索引的信息到inode表

        InodeAdditionMessage inodeAdditionMessage=new InodeAdditionMessage();

        inodeAdditionMessage.setId(classifyMessageReceive.getInodeId());
        inodeAdditionMessage.setFileClassifyType(highScoreList);
        inodeAdditionMessage.setDropFileClassifyType(lowScoreList);
        inodeAdditionMessage.setFileType(FileTypeEnum.image);
        System.out.println(inodeAdditionMessage);
        //2.2
        inodeAdditionMessageService.save(inodeAdditionMessage);
    }
}
