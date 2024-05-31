package im.zhaojun.zfile.module.rootInode.controller;

import cn.hutool.core.util.ObjectUtil;
import com.beust.ah.A;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSort;
import im.zhaojun.zfile.core.util.AjaxJson;
import im.zhaojun.zfile.module.inode.mapper.InodeMapper;
import im.zhaojun.zfile.module.inode.model.result.InodeInfoResult;
import im.zhaojun.zfile.module.inode.service.impl.InodeService;
import im.zhaojun.zfile.module.rootInode.model.entity.RootInode;
import im.zhaojun.zfile.module.rootInode.model.request.RootInodeInsertAndUpdateRequest;
import im.zhaojun.zfile.module.rootInode.model.request.RootInodeRequest;
import im.zhaojun.zfile.module.rootInode.service.RootInodeService;
import im.zhaojun.zfile.module.storage.service.StorageSourceService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@Api(tags = "用户根索引模块")
@ApiSort(25)
@Slf4j
@RequestMapping("/api/rootInode/admin")
@RestController
public class RootInodeAdminController {
    @Resource
    RootInodeService rootInodeService;

    @Resource
    InodeService inodeService;

    @Resource
    StorageSourceService storageSourceService;
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "获取所有")
    @PostMapping("/getRootInode")
    public AjaxJson<List<RootInode>>  getRootInodes(){
        List<RootInode> rootInodes = rootInodeService.findRootInodes();
        return AjaxJson.getSuccessData(rootInodes);
    }

    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "获取ByUser")
    @PostMapping("/getRootInodeByUserId")
    public AjaxJson<List<RootInode>>  getRootInodeByUserId(@RequestBody RootInodeRequest rootInodeRequest){
        List<RootInode> rootInodes = rootInodeService.findRootInodesByUserId(rootInodeRequest.getUserId());
        return AjaxJson.getSuccessData(rootInodes);
    }

    @ApiOperationSupport(order = 3)
    @ApiOperation(value = "modify")
    @PostMapping("/upadte")
    public AjaxJson<Boolean>  modify(@RequestBody RootInodeInsertAndUpdateRequest rootInodeInsertAndUpdateRequest){

        RootInode rootInode=new RootInode();
        rootInode.setId(rootInodeInsertAndUpdateRequest.getId());
        rootInode.setInodeId(inodeService.findInodeIdByPath(rootInodeInsertAndUpdateRequest.getPath(),storageSourceService.findIdByKey(rootInodeInsertAndUpdateRequest.getStorageKey())));
        rootInode.setName(rootInodeInsertAndUpdateRequest.getName());
        rootInode.setStorageCapacity(rootInodeInsertAndUpdateRequest.getStorageCapacity());
        rootInode.setType(1);
        rootInode.setUserId(rootInodeInsertAndUpdateRequest.getUserId());

        if(ObjectUtil.isNotNull(rootInode.getId()))
            rootInodeService.upadte(rootInode);
        else
            rootInodeService.insert(rootInode);
        return AjaxJson.getSuccessData(true);
    }


    @ApiOperationSupport(order = 4)
    @ApiOperation(value = "delete")
    @PostMapping("/delete")
    public AjaxJson<Boolean>  delete(@RequestBody RootInodeRequest rootInodeRequest){
        return AjaxJson.getSuccessData(rootInodeService.delete(rootInodeRequest.getRootInodeId()));
    }

    @ApiOperationSupport(order = 5)
    @ApiOperation(value = "insert")
    @PostMapping("/insert")
    public AjaxJson<Boolean>  insert(@RequestBody RootInode rootInode){
        return AjaxJson.getSuccessData(rootInodeService.insert(rootInode));
    }
}
