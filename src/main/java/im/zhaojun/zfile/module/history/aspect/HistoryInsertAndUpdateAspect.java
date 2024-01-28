package im.zhaojun.zfile.module.history.aspect;

import cn.hutool.core.util.ObjectUtil;
import im.zhaojun.zfile.core.constant.StoreAspectOrderConstants;
import im.zhaojun.zfile.core.constant.ZFileConstant;
import im.zhaojun.zfile.core.util.ParamUtils;
import im.zhaojun.zfile.core.util.StringUtils;
import im.zhaojun.zfile.module.history.modle.entity.FileHistory;
import im.zhaojun.zfile.module.history.service.FileHistoryService;
import im.zhaojun.zfile.module.inode.service.InodeService;
import im.zhaojun.zfile.module.storage.context.StorageSourceContext;
import im.zhaojun.zfile.module.storage.service.base.AbstractProxyTransferService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

@Aspect
@Component
@Slf4j
//切面执行顺序排序
@Order(StoreAspectOrderConstants.HISTORY_SERVICE_ASPECT_ORDER)
public class HistoryInsertAndUpdateAspect {
    @Resource
    private StorageSourceContext storageSourceContext;
    @Resource
    private InodeService inodeService;
    @Resource
    private FileHistoryService fileHistoryService;

    private static int PATH_AND_NAME_IN_PARAM = 0;

    private static int INPUT_STREAM_IN_PARAM = 1;

    /**
     * 切入于插入和更新，用户可以在插入时选择历史服务或在已有文件中开启历史服务
     * @param point
     * @return
     * @throws Throwable
     */
    @Around(value =
            "( execution(* im.zhaojun.zfile.module.storage.service.base." +
                    "AbstractProxyTransferService.uploadFile(..)))")
    public Object aroundUploadFileWhenUploadFile(ProceedingJoinPoint point) throws Throwable {

        //1获取path和name
        Object[] args = point.getArgs();
        Object result = null;
        String pathSeparator = ZFileConstant.PATH_SEPARATOR;
        String pathAndName = (String) args[HistoryInsertAndUpdateAspect.PATH_AND_NAME_IN_PARAM];


        // 1.1使用 split 方法拆分路径与名称
        String[] parts = pathAndName.split(pathSeparator);
        String path;
        String name;
        // 1.2检查分割后的数组是否包含路径和名称
        if (parts.length > 1) {
            path = pathSeparator + String.join(pathSeparator, Arrays.copyOfRange(parts, 0, parts.length - 1));
            name = parts[parts.length - 1];
        } else {
            path = pathSeparator; // 如果没有路径，设置 path 为根目录 "/"
            name = parts[0];
        }
        //2获取StorageId
        Object targetObject = point.getTarget();
        AbstractProxyTransferService<?> abstractProxyTransferService = (AbstractProxyTransferService<?>) targetObject;
        Integer storageId = abstractProxyTransferService.getStorageId();

        //3执行
        result = point.proceed();
        System.out.println("历史");
        System.out.println("执行了历史服务");
        System.out.println(StringUtils.removeDuplicateSlashes(
                path + ZFileConstant.PATH_SEPARATOR
                        + name));
        //4现在手上有存储元id和path和name，获取索引id,由于索引是文件更新后更新的，所有也要等待执行后获取
        Long inodeId = inodeService.findInodeIdByPath(
                StringUtils.removeDuplicateSlashes(
                        path + ZFileConstant.PATH_SEPARATOR
                                + name)
                , storageId);

        //5备份文件

        AbstractProxyTransferService<?> historyService;
        FileHistory oldHistory = fileHistoryService.selectOneByInodeId(inodeId);
        if(!ObjectUtil.isNull(oldHistory)){
            //5.1.1如果曾经在某历史服务备份过
            historyService =
                    (AbstractProxyTransferService<?>)
                            storageSourceContext.getByStorageId(oldHistory.getBakeUpStorageId());
        }else{
            //5.1.2获取要备份的存储源（随机获取）获取历史服务
            historyService =
                    (AbstractProxyTransferService<?>)
            storageSourceContext.getRandomByStorageConfig(
                        ParamUtils.HISTORY_PARAM_NAME,Boolean.TRUE.toString()
            );
        }
        //5.2.1创建文件夹，如果没有的话

        historyService.uncheckNewFolder( StringUtils.removeDuplicateSlashes(
                ZFileConstant.PATH_SEPARATOR), String.valueOf(inodeId));
        Date now = new Date();
        // 使用 SimpleDateFormat 定义自己的日期格式
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
        //5.2.2然后在这个文件夹中存入以文件时间命名的文件

        historyService.uploadFileWithoutInodeChange(
                StringUtils.removeDuplicateSlashes(
                        ZFileConstant.PATH_SEPARATOR
                             +inodeId
                             +ZFileConstant.PATH_SEPARATOR
                             +dateFormat.format(now)
                             +ZFileConstant.FILE_EXTENSION_SEPARATOR
                             +StringUtils.getFileExtension(name)
                )
                ,abstractProxyTransferService.getDownloadStream(pathAndName));

        //5.3记录备份记录
        FileHistory fileHistory =new FileHistory();

        fileHistory.setInodeId(inodeId);
        fileHistory.setBakeUpTime(new Date());
        fileHistory.setBakeUpStorageId(historyService.getStorageId());
        fileHistory.setName(name);
        System.out.println(inodeId);
        fileHistoryService.insert(fileHistory);
        return result;
    }

//   TODO，在文件名更新时
}
