package im.zhaojun.zfile.module.rootInode.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import im.zhaojun.zfile.module.inode.mapper.InodeMapper;
import im.zhaojun.zfile.module.rootInode.mapper.RootInodeMapper;
import im.zhaojun.zfile.module.rootInode.model.entity.RootInode;
import im.zhaojun.zfile.module.storage.model.param.MicrosoftDriveParam;
import im.zhaojun.zfile.module.storage.service.base.AbstractMicrosoftDriveService;
import im.zhaojun.zfile.module.user.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import java.util.List;
import javax.annotation.Resource;

@Service
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Slf4j
public class RootInodeService {
    @Resource
    RootInodeMapper rootInodeMapper;

    @Resource
    UserMapper userMapper;

    @Resource
    InodeMapper inodeMapper;

    public RootInode findRootInodeByInodeId(Long inodeId) {
        QueryWrapper<RootInode> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(RootInode.INODE_ID_COLUMN_NAME,inodeId);
        return rootInodeMapper.selectOne(queryWrapper);
    }

    public List<RootInode> findRootInodes() {
        return rootInodeMapper.selectList(null);
    }

    public List<RootInode> findRootInodesByUserId(Long userId) {
        QueryWrapper<RootInode> queryWrapper =new QueryWrapper<>();
        queryWrapper.eq(RootInode.USER_ID_COLUMN_NAME,userId);
        return rootInodeMapper.selectList(queryWrapper);
    }

    public Boolean upadte(RootInode rootInode) {
     rootInodeMapper.updateById(rootInode);
        return true;
    }

    public Boolean delete(Long id) {
       rootInodeMapper.deleteById(id);
       return true;
    }

    public Boolean insert(RootInode rootInode) {
        rootInodeMapper.insert(rootInode);
        return true;
    }
}
