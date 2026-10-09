package im.zhaojun.zfile.module.share.controller;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.ObjectUtil;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSort;

import im.zhaojun.zfile.core.util.AjaxJson;
import im.zhaojun.zfile.module.inode.model.entity.Inode;
import im.zhaojun.zfile.module.inode.service.impl.InodeService;
import im.zhaojun.zfile.module.link.model.result.BatchGenerateLinkResponse;
import im.zhaojun.zfile.module.share.model.entity.ShareLink;

import im.zhaojun.zfile.module.share.model.request.DeleteShareLinkRequest;
import im.zhaojun.zfile.module.share.model.request.SelectShareLinkRequest;
import im.zhaojun.zfile.module.share.model.result.ShareLinkItemResult;
import im.zhaojun.zfile.module.share.service.ShareLinkService;
import im.zhaojun.zfile.module.storage.service.StorageSourceService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import im.zhaojun.zfile.module.share.model.request.AddShareLinkRequest;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Api(tags = "分享管理模块")
@ApiSort(20)
@RestController
@RequestMapping("/shareLink")
public class ShareLinkController {

    @Resource
    ShareLinkService shareLinkService;
    @Resource
    InodeService inodeService;

    @Resource
    StorageSourceService storageSourceService;
    @ApiOperationSupport(order = 1, ignoreParameters = {"zfile-token"})
    @ApiOperation(value = "添加分享")
    @PostMapping("/add")
    public AjaxJson<BatchGenerateLinkResponse> addShareLink(@RequestBody
                                              AddShareLinkRequest addShareLinkRequest){
        Long inodeId = inodeService.findInodeIdByPath(addShareLinkRequest.getPath()
                , storageSourceService.findIdByKey(addShareLinkRequest.getStorageKey()));
        ShareLink shareLink = shareLinkService.generatorShareLink(inodeId,
                StpUtil.getLoginIdAsLong(), new Date().getTime()+addShareLinkRequest.getExpireTime());
        String sharePrivateKey = shareLink.getSharePrivateKey();
        String sharePublicKey = shareLink.getSharePublicKey();
        BatchGenerateLinkResponse batchGenerateLinkResponse = new BatchGenerateLinkResponse();
        batchGenerateLinkResponse.setSharePublicKey(sharePublicKey);
        batchGenerateLinkResponse.setSharePrivateKey(sharePrivateKey);

        return AjaxJson.getSuccessData(batchGenerateLinkResponse);
    }


    @ApiOperationSupport(order = 1, ignoreParameters = {"zfile-token"})
    @ApiOperation(value = "搜索分享信息")
    @PostMapping("/select")
    public AjaxJson<List<ShareLinkItemResult>> selectShareLink(@RequestBody SelectShareLinkRequest selectShareLinkRequest){
        String fileName = selectShareLinkRequest.getFileName();
        List<ShareLink> shareLinks = shareLinkService.selectByUser(StpUtil.getLoginIdAsLong());
        List<ShareLinkItemResult> shareLinkItemResults=new ArrayList<>();
        for (ShareLink shareLink:shareLinks){
            Inode byId = inodeService.findById(shareLink.getInodeId());
            if(ObjectUtil.isNull(fileName)){
                ShareLinkItemResult shareLinkItemResult = new ShareLinkItemResult();
                shareLinkItemResult.setPathAndName(
                        inodeService.findInodePathAndNameById(shareLink.getInodeId()));
                shareLinkItemResult.setCreateTime(shareLink.getCreateTime());
                shareLinkItemResult.setExpireTime(shareLink.getExpireTime());
                shareLinkItemResult.setPrivateKey(shareLink.getSharePrivateKey());
                shareLinkItemResult.setPublicKey(shareLink.getSharePublicKey());
                shareLinkItemResults.add(shareLinkItemResult);
            }else if(byId.getName().startsWith(fileName)) {
                ShareLinkItemResult shareLinkItemResult = new ShareLinkItemResult();
                shareLinkItemResult.setPathAndName(inodeService.findInodePathAndNameById(shareLink.getInodeId()));
                shareLinkItemResult.setCreateTime(shareLink.getCreateTime());
                shareLinkItemResult.setExpireTime(shareLink.getExpireTime());
                shareLinkItemResult.setPrivateKey(shareLink.getSharePrivateKey());
                shareLinkItemResult.setPublicKey(shareLink.getSharePublicKey());
                shareLinkItemResults.add(shareLinkItemResult);
            }
        }
        return AjaxJson.getSuccessData(shareLinkItemResults);

    }

    @ApiOperationSupport(order = 1, ignoreParameters = {"zfile-token"})
    @ApiOperation(value = "搜索分享信息")
    @PostMapping("/delete")
    public AjaxJson<Boolean> deleteShareLink(@RequestBody String publicKey){
        shareLinkService.deleteLink(publicKey);
        return AjaxJson.getSuccessData(true);
    }
    @ApiOperationSupport(order = 1, ignoreParameters = {"zfile-token"})
    @ApiOperation(value = "搜索分享信息")
    @PostMapping("/admin/select")
    public AjaxJson<List<ShareLinkItemResult>> AdminSelectShareLink(){
        List<ShareLink> shareLinks = shareLinkService.selectAll();
        List<ShareLinkItemResult> shareLinkItemResults=new ArrayList<>();
        for (ShareLink shareLink:shareLinks){
            Inode byId = inodeService.findById(shareLink.getInodeId());
                ShareLinkItemResult shareLinkItemResult = new ShareLinkItemResult();
                shareLinkItemResult.setPathAndName(
                        inodeService.findInodePathAndNameById(shareLink.getInodeId()));
                shareLinkItemResult.setCreateTime(shareLink.getCreateTime());
                shareLinkItemResult.setExpireTime(shareLink.getExpireTime());
                shareLinkItemResult.setPrivateKey(shareLink.getSharePrivateKey());
                shareLinkItemResult.setPublicKey(shareLink.getSharePublicKey());
                shareLinkItemResults.add(shareLinkItemResult);
        }
        return AjaxJson.getSuccessData(shareLinkItemResults);

    }
}
