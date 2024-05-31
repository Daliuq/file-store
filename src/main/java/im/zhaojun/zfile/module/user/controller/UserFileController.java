package im.zhaojun.zfile.module.user.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSort;
import im.zhaojun.zfile.core.util.AjaxJson;
import im.zhaojun.zfile.core.util.inode.InodeUtil;
import im.zhaojun.zfile.module.filter.service.FilterConfigService;
import im.zhaojun.zfile.module.inode.model.entity.Inode;
import im.zhaojun.zfile.module.inode.model.result.InodeInfoResult;
import im.zhaojun.zfile.module.inode.model.result.base.InodeItemResult;
import im.zhaojun.zfile.module.inode.service.impl.InodeService;
import im.zhaojun.zfile.module.rootInode.model.entity.RootInode;
import im.zhaojun.zfile.module.rootInode.service.RootInodeService;
import im.zhaojun.zfile.module.storage.context.StorageSourceContext;
import im.zhaojun.zfile.module.storage.model.enums.FileTypeEnum;
import im.zhaojun.zfile.module.storage.model.request.base.FileListRequest;
import im.zhaojun.zfile.module.storage.model.result.FileInfoResult;
import im.zhaojun.zfile.module.storage.model.result.FileItemResult;
import im.zhaojun.zfile.module.storage.service.StorageSourceService;
import im.zhaojun.zfile.module.storage.service.base.AbstractBaseFileService;
import im.zhaojun.zfile.module.user.mapper.UserMapper;
import im.zhaojun.zfile.module.user.model.request.SelectFileReq;
import im.zhaojun.zfile.module.user.model.result.RootPathResult;
import im.zhaojun.zfile.module.user.model.result.UserSizeResult;
import im.zhaojun.zfile.module.user.model.result.base.UserRootPath;
import im.zhaojun.zfile.module.user.service.UserAndInodeService;
import im.zhaojun.zfile.module.user.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Api(tags = "用户登录模块")
@ApiSort(18)
@RestController
@RequestMapping("/userFile")
public class UserFileController {
    @Resource
    private InodeService inodeService;
    @Resource
    private UserService userService;

    @Resource
    private RootInodeService rootInodeService;
    @Resource
    private UserMapper userMapper;
    @Resource
    private FilterConfigService filterConfigService;
    @Resource
    private UserAndInodeService userAndInodeService;
    @Resource
    private StorageSourceService storageSourceService;
    @Resource
    private StorageSourceContext storageSourceContext;

    @Resource
    private InodeUtil inodeUtil;

    @ApiOperationSupport(order = 1, ignoreParameters = {"zfile-token"})
    @ApiOperation(value = "存储使用的总容量")
    @PostMapping("/totallySize")
    public AjaxJson<UserSizeResult> getTotallySize(){
        long loginIdAsLong = StpUtil.getLoginIdAsLong();
        Long totallySize = inodeService.getUserTotallySize(loginIdAsLong);
        Long userStorageSize = userService.getUserStorageSize(loginIdAsLong);
        UserSizeResult userSizeResult = new UserSizeResult();
        userSizeResult.setTotalSize(totallySize);
        userSizeResult.setUsedSize(userStorageSize);
        return AjaxJson.getSuccessData(userSizeResult);
    }


    @ApiOperationSupport(order = 2, ignoreParameters = {"zfile-token"})
    @ApiOperation(value = "获取user根存储源id")
    @PostMapping("/root")
    public AjaxJson<RootPathResult> getRootInfo(){
        List<Inode> userRootInodes = userService
                .getUserRootInode(Long
                        .valueOf(StpUtil
                                .getLoginIdAsString()));

        ArrayList<UserRootPath> userRootPaths = new ArrayList<>();
        for(Inode userRootInode:userRootInodes){
            UserRootPath userRootPath = new UserRootPath();
            userRootPath.setPath(inodeService.findInodePathAndNameById(userRootInode.getId()));
            userRootPath.setStorageKey(storageSourceService.findStorageKeyById(userRootInode.getStorageId()));
            RootInode rootInodeByInodeId = rootInodeService.findRootInodeByInodeId(userRootInode.getId());
            userRootPath.setType(rootInodeByInodeId.getType());
            userRootPath.setName(rootInodeByInodeId.getName());
            userRootPaths.add(userRootPath);
        }

        RootPathResult rootPathResult = new RootPathResult();

        rootPathResult.setRootPaths(userRootPaths);

        int role = userMapper.selectById(StpUtil.getLoginIdAsLong()).getRole();

        rootPathResult.setRole(role);
        return AjaxJson.getSuccessData(rootPathResult);
    }

    @ApiOperationSupport(order = 2, ignoreParameters = {"zfile-token"})
    @ApiOperation(value = "按名搜索用户文件")
    @PostMapping("/selectFile")
    public AjaxJson<InodeInfoResult> getSelectFile(@RequestBody SelectFileReq selectFile){
        System.out.println(selectFile.getStr());
        long loginIdAsLong = StpUtil.getLoginIdAsLong();
        List<Inode> inodes = userAndInodeService.selectAUserInodeListByLeftName
                (selectFile.getStr(), loginIdAsLong);
        System.out.println(inodes);
        List<InodeItemResult> inodeItemResults=new ArrayList<>();
        for(Inode inode:inodes){
            //4封装成InodeItemResult
            InodeItemResult inodeItemResult = new InodeItemResult();
            //取出对应文件服务(是单例)
            AbstractBaseFileService<?> fileService = storageSourceContext.getByStorageId(
                    inode.getStorageId());

            inodeItemResult.setName(inode.getName());
            inodeItemResult.setPath(inodeService.findInodePathAndNameById(inode.getId()));
            inodeItemResult.setSize(inode.getSize());
            inodeItemResult.setTime(inode.getTime());
            inodeItemResult.setType(inode.getIsDirectory()?FileTypeEnum.FOLDER:FileTypeEnum.FILE);

            inodeItemResult.setStorageId(inode.getStorageId());
            inodeItemResult.setStorageKey(storageSourceService.findStorageKeyById(inode.getStorageId()));
            inodeItemResult.setUrl(inodeUtil.getUrl(inode.getId()));
            inodeItemResults.add(inodeItemResult);

        }
        InodeInfoResult inodeInfoResult = new InodeInfoResult();
        inodeInfoResult.setFiles(inodeItemResults);
        return AjaxJson.getSuccessData(inodeInfoResult);
    }

    @ApiOperationSupport(order = 4, ignoreParameters = {"zfile-token"})
    @ApiOperation(value = "根据文件路径获取inodeid，并且根据id获取inodelist，等同于filelist")
    @PostMapping("/children")
    public AjaxJson<FileInfoResult> getInodeChildrenByPath(@RequestBody FileListRequest fileListRequest){
        Long inodeIdByPath = inodeService.findInodeIdByPath(
                fileListRequest.getPath(),
                storageSourceContext.getByStorageKey(
                        fileListRequest.getStorageKey()
                ).getStorageId());
        List<FileItemResult> inodeChildrenAsFileItemResults = inodeService.getInodeChildrenAsFileItemResults(inodeIdByPath);
        return AjaxJson.getSuccessData(new FileInfoResult(inodeChildrenAsFileItemResults,""));
    }
}
