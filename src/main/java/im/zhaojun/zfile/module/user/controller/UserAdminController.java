package im.zhaojun.zfile.module.user.controller;

import cn.hutool.core.io.FileUtil;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSort;
import im.zhaojun.zfile.core.util.AjaxJson;
import im.zhaojun.zfile.core.util.StringUtils;
import im.zhaojun.zfile.module.inode.mapper.InodeMapper;
import im.zhaojun.zfile.module.inode.service.impl.InodeService;
import im.zhaojun.zfile.module.storage.context.StorageSourceContext;
import im.zhaojun.zfile.module.storage.service.base.AbstractBaseFileService;
import im.zhaojun.zfile.module.user.mapper.UserMapper;
import im.zhaojun.zfile.module.user.model.entity.User;
import im.zhaojun.zfile.module.user.service.admin.UserAdminService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@Api(tags = "用户管理模块")
@ApiSort(20)
@RestController
@RequestMapping("/admin/user")
public class UserAdminController {
    @Resource
    UserAdminService userAdminService;

    @Resource
    StorageSourceContext storageSourceContext;

    @Resource
    UserMapper userMapper;
    @Resource
    InodeService inodeService;

    @Resource
    InodeMapper inodeMapper;

    @ApiOperationSupport(order = 1, ignoreParameters = {"zfile-token"})
    @ApiOperation(value = "获取用户列表")
    @PostMapping("/totallyInfo")
    public AjaxJson<List<User>> userInfo(){
        List<User> users = userAdminService.getUsers();
        return AjaxJson.getSuccessData(users);
    }
    @ApiOperationSupport(order = 2, ignoreParameters = {"zfile-token"})
    @ApiOperation(value = "修改用户")
    @PostMapping("/modify")
    public AjaxJson<Boolean> userModify(@RequestBody User user){
        Boolean aBoolean = userAdminService.updateUser(user);
        return AjaxJson.getSuccessData(aBoolean);
    }

    @ApiOperationSupport(order = 3, ignoreParameters = {"zfile-token"})
    @ApiOperation(value = "删除用户")
    @PostMapping("/deleteA")
    public AjaxJson<Boolean> userDeleteAn(@RequestBody Integer userId){
        deleteAnUse(userId);
        return AjaxJson.getSuccessData(userAdminService.deleteAnUser(userId));
    }
    @ApiOperationSupport(order = 4, ignoreParameters = {"zfile-token"})
    @ApiOperation(value = "删除用户AllSelect")
    @PostMapping("/deleteBatch")
    public AjaxJson<Boolean> userDeleteBatch(@RequestBody List<Integer> userIds){
        return AjaxJson.getSuccessData(true);
    }
    @ApiOperationSupport(order = 4, ignoreParameters = {"zfile-token"})
    @ApiOperation(value = "new用户")
    @PostMapping("/new")
    public AjaxJson<Boolean> userInsert(@RequestBody User user){
        /*//TODO 要改
        //选取可用存储源生成根
        AbstractBaseFileService<?> usableStorage = storageSourceContext.getUsableStorage(user.getStorageCapacity());
        //生成用户
        usableStorage.newFolder("/",user.getId().toString());
        Long inodeIdByPath = inodeService.findInodeIdByPath("/" + user.getId().toString(), usableStorage.getStorageId());
        */
        userAdminService.insertUser(user);
        return AjaxJson.getSuccessData(true);
    }
    private void deleteAnUse(Integer userId){
        //todo 要改改
     /*   Long rootInode = userMapper.selectById(userId).getRootInode();
        String pathAndName = inodeService.findInodePathAndNameById(rootInode);
        String fileName = FileUtil.getName(pathAndName);
        String folderName = StringUtils.getParentPath(pathAndName);
        Integer storageId = inodeMapper.selectById(rootInode).getStorageId();
        storageSourceContext.getByStorageId(storageId).deleteFolder(folderName,fileName);*/
    }
}
