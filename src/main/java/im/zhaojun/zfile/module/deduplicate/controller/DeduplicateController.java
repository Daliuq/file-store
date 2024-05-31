package im.zhaojun.zfile.module.deduplicate.controller;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.ObjectUtil;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSort;
import im.zhaojun.zfile.core.constant.ZFileConstant;
import im.zhaojun.zfile.core.util.AjaxJson;
import im.zhaojun.zfile.core.util.StringUtils;
import im.zhaojun.zfile.core.util.redis.RedisUtil;
import im.zhaojun.zfile.module.deduplicate.modle.FileDeduplicate;
import im.zhaojun.zfile.module.deduplicate.modle.SetMd5Req;
import im.zhaojun.zfile.module.deduplicate.service.DeduplicateRedisService;
import im.zhaojun.zfile.module.history.modle.entity.FileHistory;
import im.zhaojun.zfile.module.history.service.FileHistoryService;
import im.zhaojun.zfile.module.inode.model.entity.Inode;
import im.zhaojun.zfile.module.inode.model.entity.InodeLink;
import im.zhaojun.zfile.module.inode.service.impl.InodeLinkService;
import im.zhaojun.zfile.module.inode.service.impl.InodeService;
import im.zhaojun.zfile.module.storage.context.StorageSourceContext;
import im.zhaojun.zfile.module.storage.model.request.operator.UploadFileRequest;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Api(tags = "文件去重模块")
@ApiSort(23)
@RestController
@RequestMapping("/dedup")
public class DeduplicateController {
    @Resource
    RedisUtil redisUtil;
    @Resource
    DeduplicateRedisService deduplicateRedisService;

    @Resource
    InodeService inodeService;

    @Resource
    InodeLinkService inodeLinkService;

    @Resource
    FileHistoryService fileHistoryService;

    @Resource
    StorageSourceContext storageSourceContext;

    @ApiOperationSupport(order = 1, ignoreParameters = {"zfile-token"})
    @ApiOperation(value = "是否重复")
    @PostMapping("/isRepeat")
    public AjaxJson<Boolean> isRepeat(@RequestBody String md5){
        return AjaxJson.getSuccessData(ObjectUtil.isNotNull(redisUtil.get(md5)));
    }

    @ApiOperationSupport(order = 2, ignoreParameters = {"zfile-token"})
    @ApiOperation(value = "设置MD5")
    @PostMapping("/setMd5")
    public AjaxJson<Boolean> setMd5(@RequestBody SetMd5Req uploadFileRequest){
        //首先，获取主inodeid
        Long inodeIdByPath = inodeService.findInodeIdByPath(
                StringUtils.removeDuplicateSlashes(
                        uploadFileRequest.getPath() + ZFileConstant.PATH_SEPARATOR + uploadFileRequest.getName())
                , storageSourceContext.getByStorageKey(uploadFileRequest.getStorageKey()).getStorageId()
        );
        //然后获得historyid
        List<FileHistory> fileHistories = fileHistoryService.selectByInodeId(inodeIdByPath);
        if (!fileHistories.isEmpty()) {
            FileHistory lastFileHistory = fileHistories.get(fileHistories.size() - 1);
            // 现在 lastFileHistory 包含了列表中的最后一个元素
            Long historyId = lastFileHistory.getId();
            deduplicateRedisService.SetKey(
                    uploadFileRequest.getMd5(),new FileDeduplicate(inodeIdByPath,historyId));
            System.out.println(redisUtil.get(uploadFileRequest.getMd5()));
            return AjaxJson.getSuccessData(true);
        }else {
            return AjaxJson.getSuccessData(false);
        }

    }
    @ApiOperationSupport(order = 3, ignoreParameters = {"zfile-token"})
    @ApiOperation(value = "是否添加成功")
    @PostMapping("/upload/inode")
    public AjaxJson<Boolean> inode(@RequestBody SetMd5Req uploadFileRequest){
        Inode inode=new Inode();

        inode.setIsDelete(false);
        inode.setIsDirectory(false);
        inode.setIsHistoricalVersionService(false);
        inode.setLinkHisId(deduplicateRedisService.getByKey(uploadFileRequest.getMd5()).getHistoryId());
        inode.setName(uploadFileRequest.getName());
        inode.setOwnUserId(StpUtil.getLoginIdAsLong());
        inode.setSize(uploadFileRequest.getSize());
        Integer storageId = storageSourceContext.getByStorageKey(uploadFileRequest.getStorageKey()).getStorageId();
        inode.setStorageId(storageId);
        inode.setTime(Timestamp.valueOf(LocalDateTime.now()));
        System.out.println(inode);
        Inode insert = inodeService.insert(inode);

        Long inodeIdByPath = inodeService.findInodeIdByPath(uploadFileRequest.getPath(), storageId);
        //链接一下
        InodeLink inodeLink=new InodeLink(inodeIdByPath,insert.getId());
        inodeLinkService.insert(inodeLink);
        return AjaxJson.getSuccessData(true);
    }
}
