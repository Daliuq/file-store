package im.zhaojun.zfile.module.inode.aspect;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import im.zhaojun.zfile.core.constant.StoreAspectOrderConstants;
import im.zhaojun.zfile.core.constant.ZFileConstant;
import im.zhaojun.zfile.module.inode.mapper.InodeMapper;
import im.zhaojun.zfile.module.inode.model.entity.Inode;
import im.zhaojun.zfile.module.inode.model.entity.InodeLink;
import im.zhaojun.zfile.module.inode.service.InodeLinkService;
import im.zhaojun.zfile.module.inode.service.InodeService;
import im.zhaojun.zfile.module.storage.service.base.AbstractProxyTransferService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;

/***
 * 在进行文件新建操作成功后，将文件信息存入数据库
 *
 * @author liuquan
 */
@Aspect
@Component
@Slf4j
//切面执行顺序排序
@Order(StoreAspectOrderConstants.File_TRACK_ASPECT_ORDER)
public class InodeInsertAspect {

    @Resource
    private InodeService inodeService;

    @Resource
    private InodeMapper inodeMapper;
    @Resource
    private InodeLinkService inodeLinkService;
    private static int PATH_AND_NAME_IN_PARAM = 0;
    private static int PATH_IN_PARAM = 0;
    private static int NAME_IN_PARAM = 1;
    @Around(value =
            "( execution(* im.zhaojun.zfile.module.storage.service.impl.*.uploadFile(..)))")
    public Object aroundUploadFile(ProceedingJoinPoint point) throws Throwable {

        //获取path和name
        Object[] args = point.getArgs();
        Object result = null;
        String pathSeparator = ZFileConstant.PATH_SEPARATOR;
        String pathAndName = (String) args[InodeInsertAspect.PATH_AND_NAME_IN_PARAM];
        // 使用 split 方法拆分路径与名称
        String[] parts = pathAndName.split(pathSeparator);
        String path;
        String name;
        // 检查分割后的数组是否包含路径和名称
        if (parts.length > 1) {
            path = pathSeparator + String.join(pathSeparator, Arrays.copyOfRange(parts, 0, parts.length - 1));
            name = parts[parts.length - 1];
        } else {
            path = pathSeparator; // 如果没有路径，设置 path 为根目录 "/"
            name = parts[0];
        }
        //获取StorageId
        Object targetObject = point.getTarget();
        AbstractProxyTransferService<?> abstractProxyTransferService = (AbstractProxyTransferService<?>) targetObject;
        Integer storageId = abstractProxyTransferService.getStorageId();


            Long parentId = inodeService.findInodeIdByPath(path, storageId);
            result = point.proceed();
            // 如果目标方法执行成功，执行其他逻辑
            //执行插入文件追踪操作
            Inode inode = new Inode();
            // TODO 全局唯一id生成器目前用自增

            inode.setName(name);
            inode.setIsDelete(false);
            inode.setIsHistoricalVersionService(false);
            inode.setStorageId(storageId);
            inode.setIsDirectory(false);
            //在上传文件时新增一条文件id
            Long childId = inodeService.save(inode).getId();

            inodeLinkService.save(new InodeLink(parentId,childId));

        return result;
    }


    @Around(value = "( execution(* im.zhaojun.zfile.module.storage.service.*.saveStorageSource(..)))")
    public Object aroundSaveStorageSource(ProceedingJoinPoint point) throws Throwable{
        QueryWrapper<Inode> queryWrapper=new QueryWrapper<>();

        Integer storageId = (Integer)point.proceed();
        queryWrapper.eq(Inode.STORAGE_ID_COLUMN_NAME,storageId);
        if(inodeMapper.selectCount(queryWrapper)==0){
            //新建存储源时
            Inode inode=new Inode();
            inode.setIsDirectory(true);
            inode.setIsDelete(false);
            inode.setIsHistoricalVersionService(false);
            inode.setStorageId(storageId);
            inode.setName(ZFileConstant.PATH_SEPARATOR);
            inodeService.save(inode);
        }
        return storageId;
    }

    //新建文件夹，boolean newFolder(String path, String name);
    @Around(value = "execution(* im.zhaojun.zfile.module.storage.service.impl.*.newFolder(..))")
    public Object aroundNewFolder(ProceedingJoinPoint point) throws Throwable{
        //获取path和name
        Object[] args = point.getArgs();
        Object result = null;
        String pathSeparator = ZFileConstant.PATH_SEPARATOR;
        String pathAndName = (String) args[InodeInsertAspect.PATH_AND_NAME_IN_PARAM];
        // 使用 split 方法拆分路径与名称
        String[] parts = pathAndName.split(pathSeparator);
        String path = (String) args[InodeInsertAspect.PATH_IN_PARAM];
        String name = (String) args[InodeInsertAspect.NAME_IN_PARAM];
        // 检查分割后的数组是否包含路径和名称
        //获取StorageId
        Object targetObject = point.getTarget();
        AbstractProxyTransferService<?> abstractProxyTransferService = (AbstractProxyTransferService<?>) targetObject;
        Integer storageId = abstractProxyTransferService.getStorageId();

        Long parentId = inodeService.findInodeIdByPath(path, storageId);
        result = point.proceed();
        // 如果目标方法执行成功，执行其他逻辑
        //执行插入文件追踪操作
        Inode inode = new Inode();
        // TODO 全局唯一id生成器目前用自增

        inode.setName(name);
        inode.setIsDelete(false);
        inode.setIsHistoricalVersionService(false);
        inode.setStorageId(storageId);
        inode.setIsDirectory(true);
        //在上传文件时新增一条文件id
        Long childId = inodeService.save(inode).getId();

        inodeLinkService.save(new InodeLink(parentId,childId));

        return result;
    }

}
