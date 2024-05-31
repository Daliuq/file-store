package im.zhaojun.zfile.module.user.aspect;

import cn.dev33.satoken.stp.StpUtil;
import im.zhaojun.zfile.core.constant.StoreAspectOrderConstants;
import im.zhaojun.zfile.core.exception.user.StorageCapacityOverflowException;
import im.zhaojun.zfile.module.inode.service.impl.InodeService;
import im.zhaojun.zfile.module.user.service.UserService;
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
@Order(StoreAspectOrderConstants.STORAGE_SOURCE_STORAGE_CAPACITY_CHECK_ASPECT_ORDER)
public class StoageSourceGetDownloadUrlAspect {

    @Resource
    private UserService userService;
    @Resource
    private InodeService inodeService;
    private static final int SIZE_IN_PARAM = 2;
    @Before("execution(* im.zhaojun.zfile.module.storage.service.base.BaseFileService.getUploadUrl(..))")
    public void beforeBaseFileServiceMethods(JoinPoint point) {
        Object[] args = point.getArgs();
        Long size= (Long) args[SIZE_IN_PARAM];
        Long userId = StpUtil.getLoginIdAsLong();
        Long userStorageSize = userService.getUserStorageSize(userId);
        Long totallySize = inodeService.getUserTotallySize(userId);
        if(size+totallySize>userStorageSize){
            throw new StorageCapacityOverflowException("用户存储空间不足");
        }
    }
}
