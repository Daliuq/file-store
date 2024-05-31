package im.zhaojun.zfile.module.inode.module.classify.aspect;

import im.zhaojun.zfile.core.constant.StoreAspectOrderConstants;
import im.zhaojun.zfile.module.inode.model.entity.Inode;
import im.zhaojun.zfile.module.inode.module.classify.textClassify.aspect.TestClassifyAspect;
import im.zhaojun.zfile.module.inode.service.InodeAdditionMessageService;
import im.zhaojun.zfile.module.inode.service.impl.InodeAdditionMessageServiceImpl;
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
@Order(StoreAspectOrderConstants.INODE_DELETE_ASPECT_ORDER)
public class AClassifyDeleteAspect {

    @Resource
    private InodeAdditionMessageServiceImpl inodeAdditionMessageService;
    private static final int INODE_ID_IN_PARAM = 0;
    @Around(value =
            "( execution(* im.zhaojun.zfile.module.inode.service.impl.InodeService." +
                    "deleteInLogically(..)))")
    public Object aroundUploadFile(ProceedingJoinPoint point) throws Throwable {

        Object[] args = point.getArgs();
        Object result = null;
        Inode inode= (Inode) args[AClassifyDeleteAspect.INODE_ID_IN_PARAM];

        result = point.proceed();

        inodeAdditionMessageService.delete(inode.getId());

        return result;
    }
}
