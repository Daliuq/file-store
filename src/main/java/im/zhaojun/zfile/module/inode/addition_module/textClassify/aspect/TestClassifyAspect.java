package im.zhaojun.zfile.module.inode.addition_module.textClassify.aspect;

import im.zhaojun.zfile.core.constant.StoreAspectOrderConstants;
import im.zhaojun.zfile.core.util.FileContentReader;
import im.zhaojun.zfile.core.util.IoUtil2;
import im.zhaojun.zfile.module.inode.addition_module.textClassify.messagequeue.producer.TextSendProducer;
import im.zhaojun.zfile.module.inode.addition_module.textClassify.model.messagequeue.send.ClassifyMessageSend;
import im.zhaojun.zfile.module.inode.mapper.InodeMapper;
import im.zhaojun.zfile.module.inode.service.impl.InodeLinkService;
import im.zhaojun.zfile.module.inode.service.impl.InodeService;
import im.zhaojun.zfile.module.storage.service.base.AbstractProxyTransferService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.InputStream;

@Aspect
@Component
@Slf4j
//切面执行顺序排序
@Order(StoreAspectOrderConstants.TEST_CLASSIFY_ASPECT_ORDER)
public class TestClassifyAspect {
    @Resource
    private InodeService inodeService;
    @Resource
    private InodeMapper inodeMapper;
    @Resource
    private InodeLinkService inodeLinkService;

    @Resource
    private TextSendProducer textSendProducer;

    private static int PATH_AND_NAME_IN_PARAM = 0;
    private static int INPUT_STREAM_IN_PARAM = 1;
    private static int PATH_IN_PARAM = 0;
    private static int NAME_IN_PARAM = 1;

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
        String pathAndName = (String) args[TestClassifyAspect.PATH_AND_NAME_IN_PARAM];
        InputStream inputStream=(InputStream) args[TestClassifyAspect.INPUT_STREAM_IN_PARAM];
        inputStream = IoUtil2.copyInputStream(inputStream);
        //获取StorageId
        Object targetObject = point.getTarget();
        AbstractProxyTransferService<?> abstractProxyTransferService =
                (AbstractProxyTransferService<?>) targetObject;
        Integer storageId = abstractProxyTransferService.getStorageId();
        //执行
        result = point.proceed();
        // 如果目标方法执行成功，执行其他逻辑
        //1.判断文件是否可以被读取文本
        String context = FileContentReader.readTextFile(pathAndName, inputStream);
        if(context!=null) {
            //2.如果可以，读取文本并发送至消息队列
            ClassifyMessageSend classifyMessageSend = new ClassifyMessageSend();
            classifyMessageSend.setText(context);
            classifyMessageSend.setId(inodeService.findInodeIdByPath(pathAndName,storageId));
            //接下来由python将该额外信息放入mongodb
            textSendProducer.sendMessage(classifyMessageSend);
        }
        return result;
    }
}
