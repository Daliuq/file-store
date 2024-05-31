package im.zhaojun.zfile.module.share.controller;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.ObjectUtil;
import com.beust.ah.A;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSort;
import im.zhaojun.zfile.core.constant.ZFileConstant;
import im.zhaojun.zfile.core.exception.share.CrossBorderAccessDuringSharingException;
import im.zhaojun.zfile.core.util.AjaxJson;
import im.zhaojun.zfile.core.util.StringUtils;
import im.zhaojun.zfile.module.inode.service.impl.InodeService;
import im.zhaojun.zfile.module.share.model.entity.Share;
import im.zhaojun.zfile.module.share.model.entity.ShareLink;
import im.zhaojun.zfile.module.share.model.request.AddShareRequest;
import im.zhaojun.zfile.module.share.model.request.GetShareFileItemListRequest;
import im.zhaojun.zfile.module.share.model.result.ShareFileInfoResult;
import im.zhaojun.zfile.module.share.model.result.ShareUserInfoResult;
import im.zhaojun.zfile.module.share.model.result.base.ShareFileItemResult;
import im.zhaojun.zfile.module.share.model.result.base.ShareUserItemResult;
import im.zhaojun.zfile.module.share.service.ShareLinkService;
import im.zhaojun.zfile.module.share.service.ShareService;
import im.zhaojun.zfile.module.storage.context.StorageSourceContext;
import im.zhaojun.zfile.module.storage.model.result.FileInfoResult;
import im.zhaojun.zfile.module.storage.model.result.FileItemResult;
import im.zhaojun.zfile.module.storage.service.base.AbstractBaseFileService;
import im.zhaojun.zfile.module.user.model.entity.User;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.thymeleaf.util.ObjectUtils;

import javax.annotation.Resource;

import static org.aspectj.weaver.tools.cache.SimpleCacheFactory.path;

@Api(tags = "分享管理模块")
@ApiSort(20)
@RestController
@RequestMapping("/share")
public class ShareController {
    @Resource
    private ShareService shareService;
    @Resource
    private InodeService inodeService;
    @Resource
    private ShareLinkService shareLinkService;
    @Resource
    private StorageSourceContext storageSourceContext;
    @ApiOperationSupport(order = 1, ignoreParameters = {"zfile-token"})
    @ApiOperation(value = "获取用户列表")
    @PostMapping("/shareUser")
    public AjaxJson<ShareUserInfoResult> receiveUsers(){
        Long userId = StpUtil.getLoginIdAsLong();
        List<User> users =  shareService.getShareUsers(userId);
        // 使用 Set 来去重
        Set<User> uniqueUsers = new HashSet<>(users);


        ArrayList<ShareUserItemResult> shareUserItemResults = new ArrayList<>();
        for(User user:uniqueUsers){
            ShareUserItemResult shareUserItemResult = new ShareUserItemResult();
            shareUserItemResult.setName(user.getName());
            shareUserItemResult.setUserId(user.getId().toString());
            shareUserItemResults.add(shareUserItemResult);
        }

        ShareUserInfoResult shareUserInfoResult = new ShareUserInfoResult();
        shareUserInfoResult.setFiles(shareUserItemResults);

        return AjaxJson.getSuccessData(shareUserInfoResult);
    }

    @ApiOperationSupport(order = 1, ignoreParameters = {"zfile-token"})
    @ApiOperation(value = "获取分享列表,只要不传path就访问所有分享的根索引（包括文件）")
    @PostMapping("/getItem")
    public AjaxJson<ShareFileInfoResult> getItem(@RequestBody GetShareFileItemListRequest getShareFileItemListRequest){
        System.out.println(getShareFileItemListRequest);
        String baseSharePath = getShareFileItemListRequest.getBaseSharePath();
        if(ObjectUtil.isNotNull(baseSharePath)){
            String baseShareStorageKey = getShareFileItemListRequest.getBaseShareStorageKey();
            Integer baseStorageId = storageSourceContext.getByStorageKey(getShareFileItemListRequest.getBaseShareStorageKey()).getStorageId();
            Long baseShareId=inodeService.
                    findInodeIdByPath(baseSharePath,baseStorageId);


            Long inodeIdByPath = inodeService.
                    findInodeIdByPath(StringUtils.removeDuplicateSlashes(
                    baseSharePath+ ZFileConstant.PATH_SEPARATOR +
                            getShareFileItemListRequest.getFullPath())
                    , baseStorageId);
            System.out.println(inodeIdByPath);
            System.out.println(baseShareId);
            //检查访问的inode是否是baseinode的child
            if(!inodeService.isChild(inodeIdByPath, baseShareId)){
                throw new CrossBorderAccessDuringSharingException("越界访问");
            };
            List<FileItemResult> inodeChildrenAsFileItemResults = inodeService.getInodeChildrenAsFileItemResults(inodeIdByPath);
            List<ShareFileItemResult> shareFileItemResults= new ArrayList<>();
            for (FileItemResult fileItemResult:inodeChildrenAsFileItemResults){
                ShareFileItemResult shareFileItemResult = new ShareFileItemResult(fileItemResult,baseShareStorageKey,baseSharePath);
                shareFileItemResults.add(shareFileItemResult);
            }
            return AjaxJson.getSuccessData(new ShareFileInfoResult(shareFileItemResults));
        }else {
            //访问全部根
            List<Long> inodeIds= shareService.getBaseInodesInOneUser(getShareFileItemListRequest.getShareUserId(),StpUtil.getLoginIdAsLong());
            List<ShareFileItemResult> byIdListAsFileItemResult = inodeService.getByIdListWhenGetBaseShareItemResult(inodeIds);

            return AjaxJson.getSuccessData(new ShareFileInfoResult(byIdListAsFileItemResult));
        }
    }

    @ApiOperationSupport(order = 1, ignoreParameters = {"zfile-token"})
    @ApiOperation(value = "添加分享")
    @PostMapping("/add")
    public AjaxJson<Boolean> addShare(@RequestBody AddShareRequest addShareRequest){
        String sharePrivateKey = addShareRequest.getSharePrivateKey();
        String sharePublicKey = addShareRequest.getSharePublicKey();
        Long shareUserId = addShareRequest.getShareUserId();
        ShareLink shareLink = new ShareLink();
        shareLink.setShareUserId(shareUserId);
        shareLink.setSharePrivateKey(sharePrivateKey);
        shareLink.setSharePublicKey(sharePublicKey);
        long loginIdAsLong = StpUtil.getLoginIdAsLong();

        shareService.generatorShare(shareLink,loginIdAsLong);
        return AjaxJson.getSuccessData(true);
    }
}
