package im.zhaojun.zfile.module.inode.aspect;

import im.zhaojun.zfile.core.constant.StoreAspectOrderConstants;
import im.zhaojun.zfile.core.constant.ZFileConstant;
import im.zhaojun.zfile.core.util.StringUtils;
import im.zhaojun.zfile.module.inode.model.entity.Inode;
import im.zhaojun.zfile.module.inode.service.impl.InodeService;
import im.zhaojun.zfile.module.storage.service.base.AbstractProxyTransferService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Aspect
@Component
@Slf4j
//切面执行顺序排序
@Order(StoreAspectOrderConstants.INODE_TRACK_ASPECT_ORDER)
public class InodeDeleteAspect {
    @Resource
    private InodeService inodeService;


    private static int PATH_AND_NAME_IN_PARAM = 0;

    private static int PATH_IN_PARAM = 0;
    private static int NAME_IN_PARAM = 1;

    private static int STORAGE_ID_IN_PARAM = 0;

    /**
     * 切入删除操作，同步删除(逻辑)索引节点
     * @param point
     * @return
     * @throws Throwable
     */
    @Around("( execution(* im.zhaojun.zfile.module.storage.service.base.BaseFileService.deleteFile(..)))")
    public Object aroundDeleteFile(ProceedingJoinPoint point) throws Throwable {
        Object[] args = point.getArgs();
        Object result = null;

        //获取2个参数
        String path = (String) args[InodeDeleteAspect.PATH_IN_PARAM];
        String name = (String) args[InodeDeleteAspect.NAME_IN_PARAM];
        //获取存储源id
        Object targetObject = point.getTarget();
        AbstractProxyTransferService<?> abstractProxyTransferService = (AbstractProxyTransferService<?>) targetObject;
        Integer storageId = abstractProxyTransferService.getStorageId();
        Long inodeIdByPath = inodeService.findInodeIdByPath(
                StringUtils.removeDuplicateSlashes(path + ZFileConstant.PATH_SEPARATOR + name )
                , storageId);

        //运行方法
        result = point.proceed();

        //删除索引节点
        Inode inode = new Inode();
        inode.setId(inodeIdByPath);
        inodeService.deleteInLogically(inode);

        return result;
    }

    @Around("( execution(* im.zhaojun.zfile.module.storage.service.base.BaseFileService.deleteFolder(..)))")
    public Object aroundDeleteFolder(ProceedingJoinPoint point) throws Throwable {
        Object[] args = point.getArgs();
        Object result = null;
        //获取2个参数
        String path = (String) args[InodeDeleteAspect.PATH_IN_PARAM];
        String name = (String) args[InodeDeleteAspect.NAME_IN_PARAM];
        //获取存储源id
        Object targetObject = point.getTarget();
        AbstractProxyTransferService<?> abstractProxyTransferService = (AbstractProxyTransferService<?>) targetObject;
        Integer storageId = abstractProxyTransferService.getStorageId();
        Long inodeIdByPath = inodeService.findInodeIdByPath(
                StringUtils.removeDuplicateSlashes(path + ZFileConstant.PATH_SEPARATOR + name )
                , storageId);
        //运行方法
        result = point.proceed();
        //删除索引节点//不仅如此，在(逻辑)删除时，要修改所有的子节点
        Inode inode = new Inode();
        inode.setId(inodeIdByPath);
        inodeService.deleteWithChildInLogically(inode);
        return result;
    }

    @Around(value = "( execution(* im.zhaojun.zfile.module.storage.service.*.deleteById(..)))")
    public Object aroundDeleteById(ProceedingJoinPoint point) throws Throwable{
        Object[] args = point.getArgs();
        Object result = null;
        //获取1个参数
        Integer storageId = (Integer) args[InodeDeleteAspect.STORAGE_ID_IN_PARAM];

        result = point.proceed();

        inodeService.deleteAllInodeInAStorageInLogically(storageId);

        return result;
    }
}
