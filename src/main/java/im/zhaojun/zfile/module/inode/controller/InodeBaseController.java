package im.zhaojun.zfile.module.inode.controller;

import cn.hutool.core.util.ObjectUtil;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSort;
import im.zhaojun.zfile.core.constant.ZFileConstant;
import im.zhaojun.zfile.core.util.AjaxJson;
import im.zhaojun.zfile.core.util.StringUtils;
import im.zhaojun.zfile.module.inode.chain.InodeChain;
import im.zhaojun.zfile.module.inode.chain.InodeContext;
import im.zhaojun.zfile.module.inode.model.entity.Inode;
import im.zhaojun.zfile.module.inode.model.request.BatchDeleteInodeRequest;
import im.zhaojun.zfile.module.inode.model.request.InodeListRequest;
import im.zhaojun.zfile.module.inode.model.request.InsertInodeRequest;
import im.zhaojun.zfile.module.inode.model.request.UpdateInodeRequest;
import im.zhaojun.zfile.module.inode.model.result.base.InodeItemResult;
import im.zhaojun.zfile.module.inode.service.impl.InodeService;
import im.zhaojun.zfile.module.storage.context.StorageSourceContext;
import im.zhaojun.zfile.module.storage.model.enums.FileTypeEnum;
import im.zhaojun.zfile.module.storage.model.request.operator.BatchDeleteRequest;
import im.zhaojun.zfile.module.storage.service.StorageSourceService;
import im.zhaojun.zfile.module.storage.service.base.AbstractBaseFileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

@Api(tags = "文件索引模块")
@ApiSort(11)
@Slf4j
@RequestMapping("/api/inode")
@RestController

public class InodeBaseController {
    /**
     *
     * 前端在搜索栏搜索东西，只传输名字,返回元数据
     *
     * @param inodeListRequest
     * @return
     */
    @Resource
    private InodeService inodeService;

    @Resource
    private StorageSourceContext storageSourceContext;
    @Resource
    private InodeChain inodeChain;
    @Resource
    private StorageSourceService storageSourceService;
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "通过文件名称获取对于的索引节点，用于查询", notes = "右匹配原则")
    @GetMapping("/name")
    public AjaxJson<List<InodeItemResult>> findInodeListByName(InodeListRequest inodeListRequest) throws Exception {
        //设置默认值
        inodeListRequest.handleDefaultValue();

        List<InodeItemResult> listByName = inodeService.findListByName(inodeListRequest.getName());

        InodeContext inodeContext=InodeContext.builder()
                .inodeItemResultList(listByName)
                .inodeListRequest(inodeListRequest).build();

        inodeChain.execute(inodeContext);
        return  AjaxJson.getSuccessData(listByName);
    }
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "用于在非本地代理上传文件服务的上传结束后更新索引节点",notes = "更新索引节点")
    @PostMapping("/insert")
    public AjaxJson<Boolean> insertInode(@RequestBody InsertInodeRequest insertInodeRequest){
        Inode inode = new Inode();
        inode.setIsDelete(false);
        inode.setIsDirectory(false);
        inode.setName(insertInodeRequest.getName());
        inode.setStorageId(storageSourceService.findIdByKey(insertInodeRequest.getStorageKey()));
        System.out.println(insertInodeRequest);
        Long parentId = inodeService.findInodeIdByPath(insertInodeRequest.getPath(),
                inode.getStorageId());//找到父节点的id
        inode.setOwnUserId(inodeService.findById(parentId).getOwnUserId());
        inode.setSize(insertInodeRequest.getSize());
        inode.setTime(new Timestamp(new Date().getTime()));

        inodeService.insertWhileBuiltLink(
                inode,
                insertInodeRequest.getPath());
        return AjaxJson.getSuccessData(true);
    }

    @ApiOperationSupport(order = 3)
    @ApiOperation(value = "用于在非本地代理上传文件服务的上传结束后更新索引节点",notes = "更新索引节点")
    @PostMapping("/update")
    public AjaxJson<Boolean> updateInode(UpdateInodeRequest updateInodeRequest){
        //1.由path和name获取Inode_id；
        Long inodeIdByPath = inodeService.findInodeIdByPath(
                StringUtils.removeDuplicateSlashes(
                        ZFileConstant.PATH_SEPARATOR + updateInodeRequest.getPath()
                                + ZFileConstant.PATH_SEPARATOR + updateInodeRequest.getName()
                )
                , storageSourceContext.getByStorageKey(
                        updateInodeRequest.getStorageKey()
                ).getStorageId()
        );
        Inode inode = new Inode();
        inode.setName(updateInodeRequest.getNewName());
        inode.setId(inodeIdByPath);
        inodeService.update(inode);
        return AjaxJson.getSuccessData(true);
    }

    @ApiOperationSupport(order = 4)
    @ApiOperation(value = "用于在非本地代理上传文件服务的上传结束后更新索引节点",notes = "更新索引节点")
    @PostMapping("/delete")
    public AjaxJson<Boolean> deleteInode(BatchDeleteInodeRequest batchDeleteInodeRequest){
        AbstractBaseFileService<?> fileService = storageSourceContext.getByStorageKey(batchDeleteInodeRequest.getStorageKey());
        Integer storageId = fileService.getStorageId();
        List<BatchDeleteRequest.DeleteItem> deleteItems = batchDeleteInodeRequest.getDeleteItems();
        for (BatchDeleteRequest.DeleteItem deleteItem : deleteItems) {
            String path = deleteItem.getPath();
            FileTypeEnum type = deleteItem.getType();
            String name = deleteItem.getName();
            Long inodeId = inodeService.findInodeIdByPath(
                    StringUtils.removeDuplicateSlashes(
                            path + ZFileConstant.PATH_SEPARATOR
                                    + name
                    ), storageId
            );
            Inode newInode = new Inode();
            newInode.setId(inodeId);
            if(ObjectUtil.equal(type,FileTypeEnum.FOLDER)){
                inodeService.deleteWithChildInLogically(newInode);
            }else{
                inodeService.deleteInLogically(newInode);
            }
        }
        return AjaxJson.getSuccessData(true);
    }
}
