package im.zhaojun.zfile.module.user.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import im.zhaojun.zfile.module.inode.mapper.InodeMapper;
import im.zhaojun.zfile.module.inode.model.entity.Inode;
import im.zhaojun.zfile.module.inode.service.impl.InodeService;
import im.zhaojun.zfile.module.user.mapper.UserMapper;
//import org.apache.catalina.LifecycleState;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class UserAndInodeService {
    @Resource
    private UserMapper userMapper;
    @Resource
    private InodeMapper inodeMapper;

    public List<Inode> selectAUserInodeListByLeftName(String left,Long userid){
        QueryWrapper<Inode> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq(Inode.OWN_USER_ID_COLUMN_NAME,userid).
                likeRight(Inode.NAME_COLUMN_NAME,left)
                .eq(Inode.IS_DELETE_COLUMN_NAME,false);
        return inodeMapper.selectList(queryWrapper);
    }
}
