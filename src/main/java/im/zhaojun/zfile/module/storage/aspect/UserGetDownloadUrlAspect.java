package im.zhaojun.zfile.module.storage.aspect;

import im.zhaojun.zfile.core.constant.StoreAspectOrderConstants;
import im.zhaojun.zfile.core.exception.user.StorageCapacityOverflowException;
import im.zhaojun.zfile.module.inode.service.impl.InodeService;
import im.zhaojun.zfile.module.storage.model.entity.StorageSourceConfig;
import im.zhaojun.zfile.module.storage.service.StorageSourceConfigService;
import im.zhaojun.zfile.module.storage.service.base.AbstractBaseFileService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Aspect
@Component
@Slf4j
//切面执行顺序排序
@Order(StoreAspectOrderConstants.USER_STORAGE_CAPACITY_CHECK_ASPECT_ORDER)
public class UserGetDownloadUrlAspect {
    private static final int SIZE_IN_PARAM = 2;
    @Resource
    private InodeService inodeService;
    private final String SIZE_NAME="size";
    @Resource
    private StorageSourceConfigService storageSourceConfigService;
    @Before("execution(* im.zhaojun.zfile.module.storage.service.base.BaseFileService.getUploadUrl(..))")
    public void beforeBaseFileServiceMethods(JoinPoint point) {
        Object[] args = point.getArgs();
        Object targetObject = point.getTarget();
        AbstractBaseFileService<?> abstractBaseFileService =
                (AbstractBaseFileService<?>) targetObject;
        Integer storageId = abstractBaseFileService.getStorageId();
        StorageSourceConfig byStorageIdAndName = storageSourceConfigService.findByStorageIdAndName(storageId, SIZE_NAME);
        long storageSourceCapacity = Long.parseLong(byStorageIdAndName.getValue());
        Long storageSourceTotallySize = inodeService.getStorageSourceTotallySize(storageId);
        Long size= (Long) args[SIZE_IN_PARAM];
        if(size+storageSourceTotallySize>storageSourceCapacity){
            throw new StorageCapacityOverflowException("存储源空间不足，请联系管理员");
        }
    }
}
