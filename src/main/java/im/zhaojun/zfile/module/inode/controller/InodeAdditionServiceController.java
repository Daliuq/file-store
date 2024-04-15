package im.zhaojun.zfile.module.inode.controller;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSort;
import im.zhaojun.zfile.core.util.AjaxJson;
import im.zhaojun.zfile.module.inode.model.result.InodeInfoResult;
import im.zhaojun.zfile.module.inode.service.InodeAdditionMessageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Api(tags = "文件索引模块")
@ApiSort(11)
@Slf4j
@RequestMapping("/api/inode")
@RestController

public class InodeAdditionServiceController {

    @Resource
    private InodeAdditionMessageService inodeAdditionMessageService;
    @ApiOperationSupport(order = 6)
    @ApiOperation(value = "buzid",notes = "索引模块")
    @PostMapping("/module/textClassify")
    public AjaxJson<InodeInfoResult> getModuleService(){
       return null;
    }
}
