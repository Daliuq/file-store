package im.zhaojun.zfile.module.history.controller;

import cn.hutool.core.util.ObjectUtil;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSort;
import im.zhaojun.zfile.core.constant.ZFileConstant;
import im.zhaojun.zfile.core.util.*;
import im.zhaojun.zfile.module.config.utils.SpringMvcUtils;
import im.zhaojun.zfile.module.history.modle.entity.FileHistory;
import im.zhaojun.zfile.module.history.modle.request.HistoryInfoRequest;
import im.zhaojun.zfile.module.history.modle.result.HistoryInfoResult;
import im.zhaojun.zfile.module.history.modle.result.base.HistoryItemResult;
import im.zhaojun.zfile.module.history.service.FileHistoryService;
import im.zhaojun.zfile.module.inode.service.impl.InodeService;
import im.zhaojun.zfile.module.storage.annotation.CheckPassword;
import im.zhaojun.zfile.module.storage.context.StorageSourceContext;
import im.zhaojun.zfile.module.storage.model.enums.FileTypeEnum;
import im.zhaojun.zfile.module.storage.service.StorageSourceService;
import im.zhaojun.zfile.module.storage.service.base.AbstractBaseFileService;
import im.zhaojun.zfile.module.storage.service.base.AbstractProxyTransferService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Api(tags = "历史服务模块")
@ApiSort(10)
@Slf4j
@RequestMapping("/api/history")
@RestController
public class historyController {

    @Resource
    InodeService inodeService;

    @Resource
    private StorageSourceContext storageSourceContext;

    @Resource
    private FileHistoryService fileHistoryService;

    @Resource
    private StorageSourceService storageSourceService;


    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "上传备份")
    @PostMapping("/backup/{storageKey}/**")
    public AjaxJson<?> upload(@RequestParam MultipartFile file, @PathVariable("storageKey")
    String storageKey) throws IOException {
        if (file == null) {
            throw new RuntimeException("空文件不能为空");
        }
        // 获取上传路径
        String filePath  = SpringMvcUtils.getExtractPathWithinPattern();
        filePath = filePath.replaceFirst("/([^/]+)$", ".$1");

        AbstractBaseFileService<?> storageServiceByKey =
                storageSourceContext.getByStorageKey(storageKey);
        System.out.println(storageKey);
        Integer storageId = storageServiceByKey.getStorageId();
        //4现在手上有存储元id和path和name，获取索引id,由于索引是文件更新后更新的，所有也要等待执行后获取
        Long inodeId = inodeService.findInodeIdByPath(
                StringUtils.removeDuplicateSlashes(
                        filePath)
                , storageId);
        AbstractProxyTransferService<?> historyService;
        FileHistory oldHistory = fileHistoryService.selectOneByInodeId(inodeId);
        if(!ObjectUtil.isNull(oldHistory)){
            //5.1.1如果曾经在某历史服务备份过
            historyService =
                    (AbstractProxyTransferService<?>)
                            storageSourceContext.getByStorageId(oldHistory.getBakeUpStorageId());
        }else{
            //5.1.2获取要备份的存储源（随机获取）获取历史服务
            historyService =
                    (AbstractProxyTransferService<?>)
                            storageSourceContext.getRandomByStorageConfig(
                                    ParamUtils.HISTORY_PARAM_NAME,Boolean.TRUE.toString()
                            );
        }
        //5.2.1创建文件夹，如果没有的话

        historyService.uncheckNewFolder( StringUtils.removeDuplicateSlashes(
                ZFileConstant.PATH_SEPARATOR), String.valueOf(inodeId));
        Date now = new Date();
        //5.2.2然后在这个文件夹中存入以文件时间命名的文件

        Long size = historyService.uploadFileWithoutInodeChange(
                StringUtils.removeDuplicateSlashes(
                        ZFileConstant.PATH_SEPARATOR
                                + inodeId
                                + ZFileConstant.PATH_SEPARATOR
                                + DateUtils.historyServiceFormatDate(now)
                                + ZFileConstant.FILE_EXTENSION_SEPARATOR
                                + StringUtils.getFileExtension(filePath)
                )
                , file.getInputStream());

        //5.3记录备份记录
        FileHistory fileHistory =new FileHistory();

        fileHistory.setInodeId(inodeId);
        fileHistory.setBakeUpTime(now);
        fileHistory.setBakeUpStorageId(historyService.getStorageId());
        fileHistory.setName(StringUtils.getFilename(filePath));
        fileHistory.setSize(size);

        fileHistoryService.insert(fileHistory);

        return AjaxJson.getSuccess();
    }

    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "获取索引节点历史记录")
    @PostMapping("/select")
    @CheckPassword(storageKeyFieldExpression = "[0].storageKey",
            pathFieldExpression = "[0].path",
            pathIsDirectory = false,
            passwordFieldExpression = "[0].password")
    public AjaxJson<HistoryInfoResult> findInodeListByName(
            @Valid @RequestBody  HistoryInfoRequest historyItemRequest) throws Exception {
        List<FileHistory> fileHistories = fileHistoryService.selectByInodeId(
                inodeService.findInodeIdByPath(
                        //将/{path}/{name}/{type}改成/path/name.type
                        FilePathUtils.convertPathToBackend(
                                historyItemRequest.getPath()
                        ),
                        storageSourceContext.
                                getByStorageKey(
                                        historyItemRequest.getStorageKey()).getStorageId()
                ));

        List<HistoryItemResult> historyItemResults=new ArrayList<>();

        for (FileHistory item: fileHistories) {
            String path = ZFileConstant.PATH_SEPARATOR+
                    storageSourceService.findKeyById(item.getBakeUpStorageId())
                    +ZFileConstant.PATH_SEPARATOR+
                    item.getInodeId()+ ZFileConstant.PATH_SEPARATOR
                    + DateUtils.historyServiceFormatDate(item.getBakeUpTime());
            String relativePath=item.getInodeId()+ ZFileConstant.PATH_SEPARATOR
                    + DateUtils.historyServiceFormatDate(item.getBakeUpTime());
            HistoryItemResult historyItemResult = new HistoryItemResult();
            historyItemResult.setName(item.getName());
            historyItemResult.setPath(
                    StringUtils.removeDuplicateSlashes(path)
            );
            historyItemResult.setTime(item.getBakeUpTime());
            historyItemResult.setName(item.getName());
            historyItemResult.setUrl(
                    storageSourceContext.getByStorageId(
                            item.getBakeUpStorageId()).getDownloadUrl(
                                    relativePath
                            +ZFileConstant.FILE_EXTENSION_SEPARATOR
                                            + StringUtils.getFileExtension(
                                            item.getName()
                                    )));
            historyItemResult.setType(FileTypeEnum.FILE);
            historyItemResult.setSize(item.getSize());

            historyItemResults.add(historyItemResult);
        }

        HistoryInfoResult historyInfoResult = new HistoryInfoResult();
        historyInfoResult.setFiles(historyItemResults);
        return AjaxJson.getSuccessData(historyInfoResult);
    }
}
