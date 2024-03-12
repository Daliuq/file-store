package im.zhaojun.zfile.module.inode.addition_module.textClassify.controller;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSort;
import im.zhaojun.zfile.core.util.AjaxJson;
import im.zhaojun.zfile.module.inode.addition_module.textClassify.serivce.TextClassifyService;
import im.zhaojun.zfile.module.inode.model.result.InodeInfoResult;
import im.zhaojun.zfile.module.inode.model.result.base.InodeItemResult;
import im.zhaojun.zfile.module.inode.service.impl.InodeAdditionMessageServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Api(tags = "文件索引模块")
@ApiSort(12)
@Slf4j
@RequestMapping("/api/inode/text")
@RestController
public class TextClassifyController {

    @Resource
    private InodeAdditionMessageServiceImpl inodeAdditionMessageService;
    @Resource
    private TextClassifyService textClassifyService;

    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "通过文件名称获取对于的索引节点，用于查询", notes = "右匹配原则")
    @GetMapping("/getClassList")
    public AjaxJson<InodeInfoResult>  getTextClassList(){
        List<String> list = textClassifyService.getTextClass();
        List<InodeItemResult> inodeItemResults=new ArrayList<>();
        for(String s:list){
            InodeItemResult inodeItemResult = new InodeItemResult();
            inodeItemResult.setName(s);
            inodeItemResult.setPath(s);
            inodeItemResults.add(inodeItemResult);
        }
        InodeInfoResult inodeInfoResult = new InodeInfoResult();
        inodeInfoResult.setFiles(inodeItemResults);
        return AjaxJson.getSuccessData(inodeInfoResult);
    }

}
