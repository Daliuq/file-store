package im.zhaojun.zfile.module.inode.controller;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSort;
import im.zhaojun.zfile.core.util.AjaxJson;
import im.zhaojun.zfile.module.inode.model.request.InodeListRequest;
import im.zhaojun.zfile.module.storage.model.entity.StorageSource;
import im.zhaojun.zfile.module.storage.model.result.StorageSourceResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(tags = "文件索引模块")
@ApiSort(11)
@Slf4j
@RequestMapping("/api/inode")
@RestController

public class InodeController {
    /**
     *
     * 前端在搜索栏搜索东西，只传输名字,返回元数据
     *
     * @param inodeListRequest
     * @return
     */
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "获取存储源列表", notes = "获取所有已启用的存储源, 并且按照后台顺序排序")
    @GetMapping("/list")
    public AjaxJson<List<StorageSourceResult>> findInodeListByName(InodeListRequest inodeListRequest) {
        //设置默认值
        inodeListRequest.handleDefaultValue();

        return null;
    }
}
