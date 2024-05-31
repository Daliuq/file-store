package im.zhaojun.zfile.module.share.controller.admin;

import cn.dev33.satoken.stp.StpUtil;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSort;
import im.zhaojun.zfile.core.util.AjaxJson;
import im.zhaojun.zfile.module.inode.service.impl.InodeService;
import im.zhaojun.zfile.module.share.model.entity.Share;
import im.zhaojun.zfile.module.share.model.request.ShareDeleteRequest;
import im.zhaojun.zfile.module.share.model.result.ShareItemResult;
import im.zhaojun.zfile.module.share.service.ShareLinkService;
import im.zhaojun.zfile.module.share.service.ShareService;
import im.zhaojun.zfile.module.user.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Resource;

@Api(tags = "分享管理模块")
@ApiSort(20)
@RestController
@RequestMapping("/share/")
public class ShareAdminController {
    @Resource
    private ShareService shareService;
    @Resource
    private InodeService inodeService;
    @Resource
    private UserService userService;

    @ApiOperationSupport(order = 1, ignoreParameters = {"zfile-token"})
    @ApiOperation(value = "添加分享")
    @PostMapping("/select")
    public AjaxJson<List<ShareItemResult>> selectShare(){
        long loginIdAsLong = StpUtil.getLoginIdAsLong();
        List<Share> shares = shareService.getShares(loginIdAsLong);
        List<ShareItemResult> shareItemResults=new ArrayList<>();
        for (Share share:shares){
            ShareItemResult shareItemResult = new ShareItemResult();
            shareItemResult.setId(share.getId().toString());
            shareItemResult.setPathAndName(inodeService.findInodePathAndNameById(share.getInodeId()));
            shareItemResult.setReceiveTime(share.getReceiveTime());
            shareItemResult.setReceiveUserName(userService.getUserById(share.getRecipientUserId()));
            shareItemResult.setShareLinkId(share.getShareLinkId().toString());
            shareItemResults.add(shareItemResult);
        }
        return AjaxJson.getSuccessData(shareItemResults);
    }

    @ApiOperationSupport(order = 1, ignoreParameters = {"zfile-token"})
    @ApiOperation(value = "添加分享")
    @PostMapping("/delete")
    public AjaxJson<Boolean> deleteShare(@RequestBody ShareDeleteRequest shareDeleteRequest){

        shareService.deleteById(shareDeleteRequest.getId());
        return AjaxJson.getSuccessData(true);
    }
}
