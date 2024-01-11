package im.zhaojun.zfile.module.inode.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import im.zhaojun.zfile.core.constant.ZFileConstant;
import im.zhaojun.zfile.core.util.MapUtile;
import im.zhaojun.zfile.module.inode.mapper.InodeLinkMapper;
import im.zhaojun.zfile.module.inode.mapper.InodeMapper;
import im.zhaojun.zfile.module.inode.model.entity.Inode;
import im.zhaojun.zfile.module.inode.model.entity.InodeLink;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

@Service
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Slf4j
public class InodeService {
    @Resource
    InodeMapper inodeMapper;

    //TODO 查

    public Inode save(Inode inode){
        inodeMapper.insert(inode);
        return inode;
    }

    @CacheEvict(value = "file-track", key = "#inode.id")
    public Inode update(Inode inode){
        UpdateWrapper<Inode> wrapper= new UpdateWrapper<>();
        wrapper.eq("id",inode.getId());
        inodeMapper.update(inode,wrapper);
        return inode;
    }

    @CacheEvict(value = "file-track", key = "#inode.id")
    public Inode deleteInLogically(Inode inode){
        UpdateWrapper<Inode> wrapper= new UpdateWrapper<>();
        inode.setIsDelete(Boolean.TRUE);
        wrapper.eq("id",inode.getId());
        inodeMapper.update(inode,wrapper);
        return inode;
    }

    @CacheEvict(value = "file-track", key = "#inode.id")
    public Inode deleteIndeed(Inode inode){
        QueryWrapper<Inode> wrapper= new QueryWrapper<>();
        wrapper.eq("id",inode.getId());
        inodeMapper.delete(wrapper);
        return inode;
    }


    @Resource
    InodeLinkMapper inodeLinkMapper;

    @Cacheable(value = "find-path-by-id",key = "#storeId+'-'+#path")
    public Long findInodeIdByPath(String path,int storeId){

        QueryWrapper<Inode> inodeQueryWrapper=new QueryWrapper<>();
        inodeQueryWrapper.eq(Inode.STORAGE_ID_COLUMN_NAME,storeId).
                eq(Inode.NAME_COLUMN_NAME, ZFileConstant.PATH_SEPARATOR);
        Inode rootInode = inodeMapper.selectOne(inodeQueryWrapper);
        Long rootId = rootInode.getId();//根节点id

        inodeQueryWrapper.clear();
        inodeQueryWrapper.eq(Inode.STORAGE_ID_COLUMN_NAME,storeId)
                .eq(Inode.IS_DELETE_COLUMN_NAME,false);
        //只搬这个存储源下全部索引，把数据库的东西搬上了
        List<Inode> inodes = inodeMapper.selectList(inodeQueryWrapper);
        HashMap<Long, Inode> inodeHashMap = MapUtile.<Long, Inode>changeListToMapWithId(inodes);

        QueryWrapper<InodeLink> inodeLinkQueryWrapper=new QueryWrapper<>();

        String[] parts=path.split(ZFileConstant.PATH_SEPARATOR);
        //"/AAA/BBB/CCC" ，storeId:“/",”AAA“，”BBB”
        //
        for(String part:parts){
            inodeLinkQueryWrapper.clear();
            inodeLinkQueryWrapper.eq(InodeLink.PARENT_ID_COLUMN_NAME,rootId);
            List<InodeLink> inodeLinks = inodeLinkMapper.selectList(inodeLinkQueryWrapper);
            for(InodeLink inodeLink:inodeLinks){
                Long childId = inodeLink.getChildId();
                Inode inode = inodeHashMap.get(childId);
                if (inode!=null)
                    if(inode.getName().equals(part)){
                        rootId = childId;
                        break;
                    };
            }
        }
        return rootId;
    }

    public void deleteWithChildInLogically(Inode inode) {
        //获取当前节点
        QueryWrapper<InodeLink> inodeLinkQueryWrapper = new QueryWrapper<>();
        Queue<Long> queue=new LinkedList<>();
        queue.add(inode.getId());
        while(!queue.isEmpty()){
            //删除当前节点
            Long nowId=queue.remove();
            Inode inode1 = new Inode();
            inode1.setId(nowId);
            deleteInLogically(inode1);


            //子节点加入队列
            inodeLinkQueryWrapper.clear();
            inodeLinkQueryWrapper.eq(InodeLink.PARENT_ID_COLUMN_NAME,nowId);
            List<InodeLink> inodeLinks = inodeLinkMapper.selectList(inodeLinkQueryWrapper);

            for(InodeLink ee:inodeLinks){
                queue.add(ee.getChildId());
            }
        }
    }

    public void deleteAllInodeInAStorageInLogically(Integer storageId) {
        UpdateWrapper<Inode> updateWrapper=new UpdateWrapper<>();
        updateWrapper.eq(Inode.STORAGE_ID_COLUMN_NAME,storageId);
        Inode inode=new Inode();
        inode.setIsDelete(true);
        inodeMapper.update(inode,updateWrapper);
    }
}
