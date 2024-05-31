package im.zhaojun.zfile.module.user.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import im.zhaojun.zfile.core.constant.ZFileConstant;
import im.zhaojun.zfile.module.inode.mapper.InodeMapper;
import im.zhaojun.zfile.module.inode.model.entity.Inode;
import im.zhaojun.zfile.module.inode.service.impl.InodeService;
import im.zhaojun.zfile.module.rootInode.mapper.RootInodeMapper;
import im.zhaojun.zfile.module.rootInode.model.entity.RootInode;
import im.zhaojun.zfile.module.user.mapper.UserMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Resource;

@Service
public class UserService {
    @Resource
    private UserMapper userMapper;

    @Resource
    private InodeMapper inodeMapper;

    @Resource
    private RootInodeMapper rootInodeMapper;

    public List<Inode> getUserRootInode(Long userId){
        QueryWrapper<RootInode> queryWrapper = new QueryWrapper<>();

        queryWrapper.eq(RootInode.USER_ID_COLUMN_NAME,userId);
        List<RootInode> rootInodes = rootInodeMapper.selectList(queryWrapper);

        ArrayList<Inode> inodes = new ArrayList<>();
// 遍历根节点列表，根据每个根节点的inode_id找到对应的实际节点
        for (RootInode rootInode : rootInodes) {
            // 假设根节点对象中有一个方法可以获取到inode_id
            Long inodeId = rootInode.getInodeId();

            // 根据inode_id查找对应的实际节点，并将其添加到列表中
            Inode inode = inodeMapper.selectById(inodeId);
            if (inode != null) {
                inodes.add(inode);
            }
        }
        return inodes;
    }

    public Long getUserStorageSize(Long userId) {
        return userMapper.selectById(userId).getStorageCapacity();
    }

    public String getUserById(Long id) {
        return userMapper.selectById(id).getName();
    }
}
