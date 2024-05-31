package im.zhaojun.zfile.module.inode.module.classify.imageClassify.service;

import im.zhaojun.zfile.api.baidu.image.UniversalObjectAndSceneRecognition;
import im.zhaojun.zfile.api.baidu.image.model.api.result.JsonResult;
import im.zhaojun.zfile.api.baidu.image.model.api.result.base.ResultItem;
import im.zhaojun.zfile.core.util.InputStreamToBase64Converter;
import im.zhaojun.zfile.module.inode.model.entity.InodeAdditionMessage;
import im.zhaojun.zfile.module.inode.model.enums.FileTypeEnum;
import im.zhaojun.zfile.module.inode.module.classify.imageClassify.messagequeue.producer.ImageSendProducer;
import im.zhaojun.zfile.module.inode.module.classify.imageClassify.model.messagequeue.send.ClassifyMessageSend;
import im.zhaojun.zfile.module.inode.module.classify.textClassify.messagequeue.producer.TextSendProducer;
import im.zhaojun.zfile.module.inode.service.FileClassifyService;
import im.zhaojun.zfile.module.inode.service.impl.InodeAdditionMessageServiceImpl;
import im.zhaojun.zfile.module.inode.service.impl.InodeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@Service
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Slf4j
public class ImageClassifyService {

    @Resource
    private InodeService inodeService;

    @Resource
    private TextSendProducer textSendProducer;

    @Resource
    private FileClassifyService fileClassifyService;

    @Resource
    private InodeAdditionMessageServiceImpl inodeAdditionMessageService;
    @Resource
    UniversalObjectAndSceneRecognition universalObjectAndSceneRecognition;

    @Resource
    ImageSendProducer imageSendProducer;
    public void useMyCNN(byte[] buffer,Long id) {
        ClassifyMessageSend classifyMessageSend = new ClassifyMessageSend();
        classifyMessageSend.setImage(buffer);
        classifyMessageSend.setId(id);

        imageSendProducer.sendMessage(classifyMessageSend);
    }


    public void useBaiduApi(InputStream inputStream, String pathAndName, Integer storageId) throws IOException {
        String convert = InputStreamToBase64Converter.convert(inputStream);
        JsonResult classify = universalObjectAndSceneRecognition.classify(convert);
        Set<String> keywordSet = new HashSet<>();
        Set<String> rootSet = new HashSet<>();

        // 遍历所有的 ResultItem
        for (ResultItem item : classify.getResult()) {
            // 将 keyword 添加到 keywordSet 中
            keywordSet.add(item.getKeyword());

            // 将 root 按 '-' 分割后添加到 rootSet 中
            String[] roots = item.getRoot().split("-");
            rootSet.addAll(Arrays.asList(roots));
        }

        // 去重
        List<String> uniqueKeywords = new ArrayList<>(keywordSet);
        List<String> uniqueRoots = new ArrayList<>(rootSet);
        //TODO 存放两个list
        InodeAdditionMessage inodeAdditionMessage=new InodeAdditionMessage();
        inodeAdditionMessage.setId(inodeService.findInodeIdByPath(pathAndName,storageId));
        inodeAdditionMessage.setFileType(FileTypeEnum.image);
        inodeAdditionMessage.setFileClassifyType(uniqueKeywords);
        inodeAdditionMessage.setDropFileClassifyType(uniqueRoots);
        inodeAdditionMessageService.save(inodeAdditionMessage);
    }
}
