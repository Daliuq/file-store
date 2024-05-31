package im.zhaojun.zfile.module.inode.module.classify.imageClassify.aspect;

import im.zhaojun.zfile.api.baidu.image.UniversalObjectAndSceneRecognition;
import im.zhaojun.zfile.api.baidu.image.model.api.result.JsonResult;
import im.zhaojun.zfile.api.baidu.image.model.api.result.base.ResultItem;
import im.zhaojun.zfile.core.constant.StoreAspectOrderConstants;
import im.zhaojun.zfile.core.util.ImageUtils;
import im.zhaojun.zfile.core.util.InputStreamToBase64Converter;
import im.zhaojun.zfile.module.inode.module.classify.imageClassify.messagequeue.producer.ImageSendProducer;
import im.zhaojun.zfile.module.inode.module.classify.imageClassify.model.messagequeue.send.ClassifyMessageSend;
import im.zhaojun.zfile.module.inode.module.classify.imageClassify.service.ImageClassifyService;
import im.zhaojun.zfile.module.inode.module.classify.textClassify.messagequeue.producer.TextSendProducer;
import im.zhaojun.zfile.module.inode.model.entity.InodeAdditionMessage;
import im.zhaojun.zfile.module.inode.model.enums.FileTypeEnum;
import im.zhaojun.zfile.module.inode.service.FileClassifyService;
import im.zhaojun.zfile.module.inode.service.impl.InodeAdditionMessageServiceImpl;
import im.zhaojun.zfile.module.inode.service.impl.InodeService;
import im.zhaojun.zfile.module.storage.service.base.AbstractProxyTransferService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Aspect
@Component
@Slf4j
//切面执行顺序排序
@Order(StoreAspectOrderConstants.CLASSIFY_ASPECT_ORDER_2)
public class  ImageClassifyAspect {
    @Resource
    private InodeService inodeService;
    @Resource
    private ImageClassifyService imageClassifyService;

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
    private static final int PATH_AND_NAME_IN_PARAM = 0;
    private static final int INPUT_STREAM_IN_PARAM = 1;
    private static final int PATH_IN_PARAM = 0;
    private static final int NAME_IN_PARAM = 1;

    /**
     * 切入文件更新，同步更新索引节点upload
     * @param point
     * @return
     * @throws Throwable
     */
    @Around(value =
            "( execution(* im.zhaojun.zfile.module.storage.service.base." +
                    "AbstractProxyTransferService.uploadFile(..)))")
    public Object aroundUploadFile(ProceedingJoinPoint point) throws Throwable {
        //获取path和name
        Object[] args = point.getArgs();
        Object result = null;
        String pathAndName = (String) args[ImageClassifyAspect.PATH_AND_NAME_IN_PARAM];
        byte[] buffer= (byte[]) args[ImageClassifyAspect.INPUT_STREAM_IN_PARAM];
        InputStream inputStream = new ByteArrayInputStream(buffer);
        //获取StorageId
        Object targetObject = point.getTarget();
        AbstractProxyTransferService<?> abstractProxyTransferService =
                (AbstractProxyTransferService<?>) targetObject;
        Integer storageId = abstractProxyTransferService.getStorageId();
        //执行
        result = point.proceed();
        // 如果目标方法执行成功，执行其他逻辑
        //1.判断文件是否可以被读取文本

        boolean image = ImageUtils.
                isImagePathWithSupportedExtension(pathAndName);
        if(image) {
            imageClassifyService.useMyCNN(buffer, inodeService.findInodeIdByPath(pathAndName,storageId));
//            imageClassifyService.useBaiduApi(inputStream,pathAndName,storageId);
        }

        inputStream.close();
        return result;
    }

//    private void useMyCNN(byte[] buffer,Long id) {
//        ClassifyMessageSend classifyMessageSend = new ClassifyMessageSend();
//        classifyMessageSend.setImage(buffer);
//        classifyMessageSend.setId(id);
//
//        imageSendProducer.sendMessage(classifyMessageSend);
//    }
//
//
//    public void useBaiduApi(InputStream inputStream, String pathAndName,Integer storageId) throws IOException {
//        String convert = InputStreamToBase64Converter.convert(inputStream);
//        JsonResult classify = universalObjectAndSceneRecognition.classify(convert);
//        Set<String> keywordSet = new HashSet<>();
//        Set<String> rootSet = new HashSet<>();
//
//        // 遍历所有的 ResultItem
//        for (ResultItem item : classify.getResult()) {
//            // 将 keyword 添加到 keywordSet 中
//            keywordSet.add(item.getKeyword());
//
//            // 将 root 按 '-' 分割后添加到 rootSet 中
//            String[] roots = item.getRoot().split("-");
//            for (String root : roots) {
//                rootSet.add(root);
//            }
//        }
//
//        // 去重
//        List<String> uniqueKeywords = new ArrayList<>(keywordSet);
//        List<String> uniqueRoots = new ArrayList<>(rootSet);
//        //TODO 存放两个list
//        InodeAdditionMessage inodeAdditionMessage=new InodeAdditionMessage();
//        inodeAdditionMessage.setId(inodeService.findInodeIdByPath(pathAndName,storageId));
//        inodeAdditionMessage.setFileType(FileTypeEnum.image);
//        inodeAdditionMessage.setFileClassifyType(uniqueKeywords);
//        inodeAdditionMessage.setDropFileClassifyType(uniqueRoots);
//        inodeAdditionMessageService.save(inodeAdditionMessage);
//    }
}
