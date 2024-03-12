package im.zhaojun.zfile.module.history.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import im.zhaojun.zfile.core.constant.ZFileConstant;
import im.zhaojun.zfile.core.util.FileDownloaderUtil;
import im.zhaojun.zfile.core.util.ParamUtils;
import im.zhaojun.zfile.core.util.StringUtils;
import im.zhaojun.zfile.module.history.mapper.FileHistoryMapper;
import im.zhaojun.zfile.module.history.modle.entity.FileHistory;
import im.zhaojun.zfile.module.inode.mapper.InodeMapper;
import im.zhaojun.zfile.module.inode.model.entity.Inode;
import im.zhaojun.zfile.module.inode.service.impl.InodeService;
import im.zhaojun.zfile.module.storage.context.StorageSourceContext;
import im.zhaojun.zfile.module.storage.model.param.IStorageParam;
import im.zhaojun.zfile.module.storage.service.base.AbstractBaseFileService;
import im.zhaojun.zfile.module.storage.service.base.AbstractProxyTransferService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;

@Service
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Slf4j
public class FileHistoryService {
    @Resource
    private FileHistoryMapper fileHistoryMapper;

    @Resource
    private InodeMapper inodeMapper;

    @Resource
    private InodeService inodeService;

    @Resource
    private StorageSourceContext storageSourceContext;


    public void insert(FileHistory fileHistory) {
        fileHistoryMapper.insert(fileHistory);
    }

    public Boolean openHistoryService(Long inodeId,Integer storageId){
        //1.更新索引节点文件，使其标记为开启服务
        UpdateWrapper<Inode> updateWrapper=new UpdateWrapper<>();
        updateWrapper.eq(Inode.ID_COLUMN_NAME,inodeId);
        updateWrapper.set(Inode.IS_HISTORICAL_VERSION_SERVICE_COLUMN_NAME
                ,true);
        inodeMapper.update(null,updateWrapper);
        //2.备份当前文件
        //2.1获取当前文件所在服务
        AbstractBaseFileService<IStorageParam> abstractBaseFileService =
                storageSourceContext.getByStorageId(storageId);
        String path = inodeService.findInodePathById(inodeId);
        String downloadUrl = abstractBaseFileService.getDownloadUrl(path);
        //2.2获取一个历史服务
        AbstractProxyTransferService<?> randomByStorageConfig = (AbstractProxyTransferService<?>) storageSourceContext.getRandomByStorageConfig(
                ParamUtils.HISTORY_PARAM_NAME, String.valueOf(true));
        //2.3在该历史服务下创建id文件夹（如果没用的话）
        randomByStorageConfig.uncheckNewFolder(ZFileConstant.PATH_SEPARATOR,inodeId.toString());
        //2.4尝试备份
        Date date = new Date();
        try ( InputStream inputStream = FileDownloaderUtil.downloadFile(downloadUrl)){
            randomByStorageConfig.uploadFileWithoutInodeChange(
                    StringUtils.removeDuplicateSlashes(
                      ZFileConstant.PATH_SEPARATOR+inodeId.toString()
                              +ZFileConstant.PATH_SEPARATOR+date
                    )
                    , inputStream
            );
            //上传完毕
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        //3. 更新历史服务记录
        FileHistory fileHistory=new FileHistory();
        fileHistory.setBakeUpTime(date);
        fileHistory.setBakeUpStorageId(randomByStorageConfig.storageId);
        fileHistory.setInodeId(inodeId);

        fileHistoryMapper.insert(fileHistory);
        return true;
    }

    public Boolean stopHistoryService(Long inodeId){
        //1.更新索引节点文件，使其标记为开启服务
        UpdateWrapper<Inode> updateWrapper=new UpdateWrapper<>();
        updateWrapper.eq(Inode.ID_COLUMN_NAME,inodeId);
        updateWrapper.set(Inode.IS_HISTORICAL_VERSION_SERVICE_COLUMN_NAME
                ,false);
        inodeMapper.update(null,updateWrapper);
        return true;
    }

    public FileHistory selectOneByInodeId(Long inodeId) {
        QueryWrapper<FileHistory> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq(FileHistory.INODE_ID_COLUMN_NAME,inodeId);
        queryWrapper.last("limit 1");
        return fileHistoryMapper.selectList(queryWrapper).get(0);
    }

    public List<FileHistory> selectByInodeId(Long inodeId) {
        QueryWrapper<FileHistory> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq(FileHistory.INODE_ID_COLUMN_NAME,inodeId);
        return fileHistoryMapper.selectList(queryWrapper);
    }

    public void updateLastBackUpNameForInodeIdAndOrderByBackUpTime(Long inodeId,String newName){

        QueryWrapper<FileHistory> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq(FileHistory.INODE_ID_COLUMN_NAME,inodeId)
                .orderByDesc(true,FileHistory.BACK_UP_TIME_COLUMN_NAME)
                ;
        FileHistory old = fileHistoryMapper.selectOne(queryWrapper);
        old.setName(newName);
        fileHistoryMapper.updateById(old);
    }
}
