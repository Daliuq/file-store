package im.zhaojun.zfile.core.util.inode;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.extension.service.IService;
import im.zhaojun.zfile.core.constant.ZFileConstant;
import im.zhaojun.zfile.core.util.DateUtils;
import im.zhaojun.zfile.core.util.StringUtils;
import im.zhaojun.zfile.module.history.mapper.FileHistoryMapper;
import im.zhaojun.zfile.module.history.modle.entity.FileHistory;
import im.zhaojun.zfile.module.history.service.FileHistoryService;
import im.zhaojun.zfile.module.inode.model.entity.Inode;
import im.zhaojun.zfile.module.inode.service.impl.InodeService;
import im.zhaojun.zfile.module.storage.context.StorageSourceContext;
import im.zhaojun.zfile.module.storage.service.base.AbstractBaseFileService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class InodeUtil {
    @Resource
    StorageSourceContext storageSourceContext;
    @Resource
    InodeService inodeService;

    @Resource
    FileHistoryMapper fileHistoryMapper;
    public String getUrl(Long inodeId){
        Inode byId = inodeService.findById(inodeId);
        if(ObjectUtil.isNull(byId.getLinkHisId())) {
            AbstractBaseFileService<?> fileService = storageSourceContext.
                    getByStorageId(byId.getStorageId());
            return fileService.getDownloadUrl(
                    inodeService.findInodePathAndNameById(
                            inodeId));
        }else{
            FileHistory item=fileHistoryMapper.selectById(byId.getLinkHisId());
            System.out.println(ZFileConstant.PATH_SEPARATOR+item.getInodeId()+ ZFileConstant.PATH_SEPARATOR
                    + DateUtils.historyServiceFormatDate(item.getBakeUpTime())
                    + ZFileConstant.FILE_EXTENSION_SEPARATOR
                    + StringUtils.getFileExtension(
                    item.getName()));
            return storageSourceContext.getByStorageId(
                    item.getBakeUpStorageId()).getDownloadUrl(
                    ZFileConstant.PATH_SEPARATOR+item.getInodeId()+ ZFileConstant.PATH_SEPARATOR
                            + DateUtils.historyServiceFormatDate(item.getBakeUpTime())
                            + ZFileConstant.FILE_EXTENSION_SEPARATOR
                            + StringUtils.getFileExtension(
                            item.getName()));

        }
    }
}
