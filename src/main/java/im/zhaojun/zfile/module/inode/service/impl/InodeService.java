package im.zhaojun.zfile.module.inode.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import im.zhaojun.zfile.core.constant.ZFileConstant;
import im.zhaojun.zfile.core.util.MapUtile;
import im.zhaojun.zfile.core.util.StringUtils;
import im.zhaojun.zfile.core.util.inode.InodeUtil;
import im.zhaojun.zfile.module.filter.service.FilterConfigService;
import im.zhaojun.zfile.module.inode.mapper.InodeLinkMapper;
import im.zhaojun.zfile.module.inode.mapper.InodeMapper;
import im.zhaojun.zfile.module.inode.model.entity.Inode;
import im.zhaojun.zfile.module.inode.model.entity.InodeLink;

import im.zhaojun.zfile.module.inode.model.result.base.InodeItemResult;
import im.zhaojun.zfile.module.share.model.result.base.ShareFileItemResult;
import im.zhaojun.zfile.module.storage.context.StorageSourceContext;
import im.zhaojun.zfile.module.storage.model.enums.FileTypeEnum;
import im.zhaojun.zfile.module.storage.model.result.FileItemResult;
import im.zhaojun.zfile.module.storage.service.StorageSourceService;
import im.zhaojun.zfile.module.storage.service.base.AbstractBaseFileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;


import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Slf4j
public class InodeService {
    @Resource
    InodeMapper inodeMapper;
    @Resource
    private InodeUtil inodeUtil;
    @Resource
    InodeLinkMapper inodeLinkMapper;
    @Resource
    StorageSourceService storageSourceService;
    @Resource
    FilterConfigService filterConfigService;

    @Resource
    private StorageSourceContext storageSourceContext;

    //TODO 查

    /**
     * 新建
     * @param inode
     * @return
     */
    public Inode insert(Inode inode){
        inodeMapper.insert(inode);
        return inode;
    }

    /**
     * 更新
     * @param inode
     * @return
     */

    public Inode update(Inode inode){
        UpdateWrapper<Inode> wrapper= new UpdateWrapper<>();
        wrapper.eq("id",inode.getId());
        inodeMapper.update(inode,wrapper);
        return inode;
    }

    /**
     * 逻辑删除索引节点
     * @param inode
     * @return
     */

    public Inode deleteInLogically(Inode inode){
        UpdateWrapper<Inode> wrapper= new UpdateWrapper<>();
        inode.setIsDelete(Boolean.TRUE);
        wrapper.eq("id",inode.getId());
        inodeMapper.update(inode,wrapper);
        return inode;
    }

    /**
     * 删除索引节点
     * @param inode
     * @return
     */

    public Inode deleteIndeed(Inode inode){
        QueryWrapper<Inode> wrapper= new QueryWrapper<>();
        wrapper.eq("id",inode.getId());
        inodeMapper.delete(wrapper);
        return inode;
    }


    /**
     * 跟路径找到索引节点id
     * @param path
     * @param storeId
     * @return
     */

    public Long findInodeIdByPath(String path,Integer storeId){

        QueryWrapper<Inode> inodeQueryWrapper=new QueryWrapper<>();
        inodeQueryWrapper.eq(Inode.STORAGE_ID_COLUMN_NAME,storeId).
                eq(Inode.NAME_COLUMN_NAME, ZFileConstant.PATH_SEPARATOR);
        Inode rootInode = inodeMapper.selectOne(inodeQueryWrapper);
        Long rootId = rootInode.getId();//根节点id
        if(ObjectUtil.equal(path,"/"))
            return rootId;
        inodeQueryWrapper.clear();
        inodeQueryWrapper.eq(Inode.STORAGE_ID_COLUMN_NAME,storeId)
                .eq(Inode.IS_DELETE_COLUMN_NAME,false);
        //TODO 考虑到换源存储，这样的化要改
        //只搬这个存储源下全部索引，把数据库的东西搬上了
        List<Inode> inodes = inodeMapper.selectList(inodeQueryWrapper);
        HashMap<Long, Inode> inodeHashMap = MapUtile.<Long, Inode>changeListToMapWithId(inodes);

        QueryWrapper<InodeLink> inodeLinkQueryWrapper=new QueryWrapper<>();

        String[] parts=path.split(ZFileConstant.PATH_SEPARATOR);
        // 将数组转换为列表
        List<String> partsList = new ArrayList<>(Arrays.asList(parts));

// 检查并删除空串
        partsList.removeIf(part -> ObjectUtil.equal(part, ""));
        //"/AAA/BBB/CCC" ，storeId:“/",”AAA“，”BBB”

        for(String part:partsList){
            inodeLinkQueryWrapper.clear();
            inodeLinkQueryWrapper.eq(InodeLink.PARENT_ID_COLUMN_NAME,rootId);
            List<InodeLink> inodeLinks = inodeLinkMapper.selectList(inodeLinkQueryWrapper);
            boolean flag=true;
            for(InodeLink inodeLink:inodeLinks){
                Long childId = inodeLink.getChildId();
                Inode inode = inodeHashMap.get(childId);
                if (inode!=null) {
                    if (inode.getName().equals(part)) {
                        flag=false;
                        rootId = childId;
                        break;
                    }
                }
            }
            if(flag){
                //没找到
                return null;
            }
        }
        return rootId;
    }

    /**
     * 根据id找到索引节点的path
     * @param id
     * @return
     */
    public String findInodePathAndNameById(Long id){
        QueryWrapper<InodeLink> inodeLinkQueryWrapper = new QueryWrapper<>();
        Long now=id;
        StringBuilder path=new StringBuilder();
        while(true){
            //1根据id去inode_link找p_id,
            // 于此同时SB=去重分隔符(inode_name(id)+分隔符+SB)
            //2 重复上述过程
            inodeLinkQueryWrapper.clear();

            //查找自己的信息
            Inode inode = inodeMapper.selectById(now);
            //头部插入'/'和路径名称
            path.insert(0,ZFileConstant.PATH_SEPARATOR);
            path.insert(0,inode.getName());

            //查找父节点
            inodeLinkQueryWrapper.eq(InodeLink.CHILD_ID_COLUMN_NAME,now);
            InodeLink inodeLink = inodeLinkMapper.selectOne(inodeLinkQueryWrapper);
            if(inodeLink==null){
                // 去除末尾的斜杠('/')
                String result = StringUtils.removeDuplicateSlashes(
                        path.insert(0,ZFileConstant.PATH_SEPARATOR).toString());
                // 如果末尾有斜杠('/')，则去除之后再返回，否则直接返回
                if (result.equals("/")) {
                    return result;
                } else {
                    return result.endsWith("/") ? result.substring(0, result.length() - 1) : result;
                }
            }
            now=inodeLink.getParentId();
        }
    }

    /**
     * 逻辑删除所有索引节点
     * @param inode
     */
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

    /**
     * 逻辑删除一个存储源的所有索引节点(逻辑上)
     * @param storageId
     */
    public void deleteAllInodeInAStorageInLogically(Integer storageId) {
        UpdateWrapper<Inode> updateWrapper=new UpdateWrapper<>();
        updateWrapper.eq(Inode.STORAGE_ID_COLUMN_NAME,storageId);
        Inode inode=new Inode();
        inode.setIsDelete(true);
        inodeMapper.update(inode,updateWrapper);
    }

    /**
     * 根据name右like搜索索引详情，用于前端搜索
     * @param name
     * 搜索的名称
     * @return
     *
     */
    public List<InodeItemResult> findListByName(String name){
        //这个过程非常长
        //1获取全部的name右like的存储源id
        QueryWrapper<Inode> inodeQueryWrapper=new QueryWrapper<>();
        inodeQueryWrapper.likeRight(Inode.NAME_COLUMN_NAME,name);

        List<Inode> inodes = inodeMapper.selectList(inodeQueryWrapper);
        List<InodeItemResult> inodeItemResults=new ArrayList<>();
        for(Inode inode:inodes){
            //2获取这些id的全部路径
            String inodePathById = findInodePathAndNameById(inode.getId());

            //3校验这些路径的过滤器
            if(filterConfigService.checkFileIsInaccessible(inode.getStorageId(),inodePathById)
            ||filterConfigService.checkFileIsHidden(inode.getStorageId(),inodePathById)){
                continue;
            }
            //4封装成InodeItemResult
            InodeItemResult inodeItemResult = new InodeItemResult();
                //取出对应文件服务(是单例)
            AbstractBaseFileService<?> fileService = storageSourceContext.getByStorageId(
                    inode.getStorageId());
            FileItemResult fileItem = fileService.getFileItem(inodePathById);

            inodeItemResult.setName(fileItem.getName());
            inodeItemResult.setPath(fileItem.getPath());
            inodeItemResult.setSize(fileItem.getSize());
            inodeItemResult.setTime(fileItem.getTime());
            inodeItemResult.setType(fileItem.getType());
            inodeItemResult.setStorageId(inode.getStorageId());
            inodeItemResult.setStorageKey(storageSourceService.findStorageKeyById(inode.getStorageId()));

            inodeItemResults.add(inodeItemResult);

        }
        return inodeItemResults;
    }

    public boolean insertWhileBuiltLink(Inode inode,String path) {
        //不仅插入索引，并且构建索引链
        inodeMapper.insert(inode);

        Long id = inode.getId();
        Long parentId = findInodeIdByPath(path, inode.getStorageId());//找到父节点的id

        // 构建他与父节点的链接
        inodeLinkMapper.insert(new InodeLink(
                parentId,id
        ));
        return true;
    }

    public Inode findById(Long id) {
        return inodeMapper.selectById(id);
    }

    public List<Inode> findChildrenById(Long id){
        QueryWrapper<InodeLink> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq(InodeLink.PARENT_ID_COLUMN_NAME,id);
        List<InodeLink> inodeLinks = inodeLinkMapper.selectList(queryWrapper);
        List<Inode> inodes=new ArrayList<>();
        for(InodeLink inodeLink:inodeLinks){
            Inode inode = inodeMapper.selectById(inodeLink.getChildId());
            if (!inode.getIsDelete())
                inodes.add(inode);
        }
        return inodes;
    }

    public List<Long> findOwnUserIdInodeId(Long userId){
        QueryWrapper<Inode> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq(Inode.OWN_USER_ID_COLUMN_NAME,userId);
        List<Inode> inodeLinks = inodeMapper.selectList(queryWrapper);
        return inodeLinks.stream().map(Inode::getId).collect(Collectors.toList());
    }

    public Long getUserTotallySize(Long userId) {
        QueryWrapper<Inode> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(Inode.OWN_USER_ID_COLUMN_NAME, userId).eq(Inode.IS_DELETE_COLUMN_NAME,false);; // 设置查询条件，own_user_id等于给定userId

        List<Inode> inodes = inodeMapper.selectList(queryWrapper); // 执行查询，获取符合条件的记录列表

        Long totalSize = 0L; // 初始化累加值为0

        for (Inode inode : inodes) {
            if(!ObjectUtil.isNull(inode.getSize()))
                totalSize += inode.getSize(); // 将每条记录的size值累加到totalSize中
        }

        return totalSize; // 返回累加值
    }

    public Long getStorageSourceTotallySize(Integer storageId) {
        QueryWrapper<Inode> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(Inode.STORAGE_ID_COLUMN_NAME,storageId).eq(Inode.IS_DELETE_COLUMN_NAME,false);
        List<Inode> inodes = inodeMapper.selectList(queryWrapper); // 执行查询，获取符合条件的记录列表

        Long totalSize = 0L; // 初始化累加值为0

        for (Inode inode : inodes) {
            if(!ObjectUtil.isNull(inode.getSize()))
                totalSize += inode.getSize(); // 将每条记录的size值累加到totalSize中
        }

        return totalSize;
    }

    public Long findStorageSourceNowTotalSize(Integer id) {
        QueryWrapper<Inode> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq(Inode.STORAGE_ID_COLUMN_NAME,id);
        return inodeMapper.selectCount(queryWrapper);
    }

    public Boolean isChild(Long childId, Long parentId) {
        Queue<Long> queue=new LinkedList<>();
        queue.add(childId);
        while(!queue.isEmpty()){
            //删除当前节点
            Long nowId=queue.remove();
            System.out.println(nowId.longValue());
            System.out.println(parentId.longValue());
            System.out.println(nowId.longValue() == parentId.longValue());
            //判断当前节点是否等于parentId
            if(nowId.longValue()==parentId.longValue()){
                return true;
            }
            //父节点加入队列
            QueryWrapper<InodeLink> inodeLinkQueryWrapper = new QueryWrapper<>();
            inodeLinkQueryWrapper.eq(InodeLink.CHILD_ID_COLUMN_NAME,nowId);
            InodeLink inodeLink = inodeLinkMapper.selectOne(inodeLinkQueryWrapper);

            if(ObjectUtil.isNotNull(inodeLink)){
                queue.add(inodeLink.getParentId());
            }

        }
//        没找到
        return false;
    }

    public List<FileItemResult> getInodeChildrenAsFileItemResults(Long inodeIdByPath) {
        List<Inode> childrenById = findChildrenById(inodeIdByPath);
        List<FileItemResult> fileItemResults=new ArrayList<>();
        for(Inode inode:childrenById){
            FileItemResult fileItemResult = new FileItemResult();
            fileItemResult.setName(inode.getName());
            String pathAndName = findInodePathAndNameById(inode.getId());
            String path = pathAndName.replaceAll("/[^/]+$", "").isEmpty() ? "/" : pathAndName.replaceAll("/[^/]+$", "");;
            fileItemResult.setPath(path);
            fileItemResult.setSize(inode.getSize());
            fileItemResult.setTime(inode.getTime());
            fileItemResult.setType(inode.getIsDirectory()? FileTypeEnum.FOLDER:FileTypeEnum.FILE);
            fileItemResult.setUrl(inodeUtil.getUrl(inode.getId()));
            fileItemResults.add(fileItemResult);
        }
        return fileItemResults;
    }

    public List<FileItemResult> getByIdListAsFileItemResult(List<Long> inodeIds) {
        List<FileItemResult> fileItemResults=new ArrayList<>();
        for(Long id:inodeIds){
            Inode inode = inodeMapper.selectById(id);
            FileItemResult fileItemResult = new FileItemResult();
            fileItemResult.setName(inode.getName());
            String pathAndName = findInodePathAndNameById(inode.getId());
            String path = pathAndName.replaceAll("/[^/]+$", "").isEmpty() ? "/" : pathAndName.replaceAll("/[^/]+$", "");;
            fileItemResult.setPath(path);
            fileItemResult.setSize(inode.getSize());
            fileItemResult.setTime(inode.getTime());
            fileItemResult.setType(inode.getIsDirectory()? FileTypeEnum.FOLDER:FileTypeEnum.FILE);
            fileItemResult.setUrl(inodeUtil.getUrl(inode.getId()));
            fileItemResults.add(fileItemResult);
        }
        return fileItemResults;
    }


    public List<ShareFileItemResult> getByIdListWhenGetBaseShareItemResult(List<Long> inodeIds) {
        List<ShareFileItemResult> fileItemResults=new ArrayList<>();
        for(Long id:inodeIds){
            Inode inode = inodeMapper.selectById(id);
            ShareFileItemResult fileItemResult = new ShareFileItemResult();
            fileItemResult.setName(inode.getName());
            String pathAndName = findInodePathAndNameById(inode.getId());
            String path = pathAndName.replaceAll("/[^/]+$", "").isEmpty() ? "/" : pathAndName.replaceAll("/[^/]+$", "");;
            fileItemResult.setPath(path);
            fileItemResult.setSize(inode.getSize());
            fileItemResult.setTime(inode.getTime());
            fileItemResult.setType(inode.getIsDirectory()? FileTypeEnum.FOLDER:FileTypeEnum.FILE);
            fileItemResult.setUrl(inodeUtil.getUrl(inode.getId()));
            fileItemResult.setBaseSharePath(findInodePathAndNameById(id));
            fileItemResult.setBaseShareStorageKey(storageSourceService.findStorageKeyById(inode.getStorageId()));
            fileItemResults.add(fileItemResult);
        }
        return fileItemResults;
    }

    public boolean isDelete(Long id) {
        return inodeMapper.selectById(id).getIsDelete();
    }
}
