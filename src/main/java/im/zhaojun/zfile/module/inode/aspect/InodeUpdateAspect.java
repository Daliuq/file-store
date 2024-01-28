package im.zhaojun.zfile.module.inode.aspect;

import im.zhaojun.zfile.core.constant.StoreAspectOrderConstants;
import im.zhaojun.zfile.core.constant.ZFileConstant;
import im.zhaojun.zfile.core.util.StringUtils;
import im.zhaojun.zfile.module.inode.model.entity.Inode;
import im.zhaojun.zfile.module.inode.service.InodeService;
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
public class InodeUpdateAspect {

    @Resource
    private InodeService inodeService;
    private static int PATH_IN_PARAM = 0;
    private static int NAME_IN_PARAM = 1;
    private static int NEW_NAME_IN_PARAM = 2;

    /**
     * 切入重命名文件（夹），在重命名文件时给文件更新索引节点
     * @param point
     * @return
     * @throws Throwable
     */
    //这两个都是基本文件服务能实现的，前端不用同步更新
    @Around("( execution(* im.zhaojun.zfile.module.storage.service.impl.*.renameFile(..)))" +
            "||( execution(* im.zhaojun.zfile.module.storage.service.impl.*.renameFolder(..)))")
    public Object aroundRenameFileAndFolder(ProceedingJoinPoint point) throws Throwable {
        Object[] args = point.getArgs();
        Object result = null;
        //获取3个参数
        String path = (String) args[InodeUpdateAspect.PATH_IN_PARAM];
        String name = (String) args[InodeUpdateAspect.NAME_IN_PARAM];
        String newName = (String) args[InodeUpdateAspect.NEW_NAME_IN_PARAM];
        //获取StorageId
        Object targetObject = point.getTarget();
        AbstractProxyTransferService<?> abstractProxyTransferService =
                (AbstractProxyTransferService<?>) targetObject;
        Integer storageId = abstractProxyTransferService.getStorageId();
        //运行方法
        result = point.proceed();
        //更新节点
        Long inodeIdByPath = inodeService.findInodeIdByPath(
                StringUtils.removeDuplicateSlashes(path + ZFileConstant.PATH_SEPARATOR + name )
                , storageId);
        Inode inode =new Inode();
        inode.setId(inodeIdByPath);
        inode.setName(newName);
        inodeService.update(inode);

        return result;
    }
}
