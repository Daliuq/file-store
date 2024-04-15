package im.zhaojun.zfile.module.inode.controller;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSort;
import im.zhaojun.zfile.core.constant.ZFileConstant;
import im.zhaojun.zfile.core.util.AjaxJson;
import im.zhaojun.zfile.core.util.StringUtils;
import im.zhaojun.zfile.module.inode.addition_module.textClassify.model.request.TextClassModifyRequest;
import im.zhaojun.zfile.module.inode.addition_module.textClassify.model.request.TextClassifyInodeRequest;
import im.zhaojun.zfile.module.inode.addition_module.textClassify.model.request.base.TextClassModifyItem;
import im.zhaojun.zfile.module.inode.addition_module.textClassify.model.result.TextClassModifyResult;
import im.zhaojun.zfile.module.inode.service.FileClassifyService;
import im.zhaojun.zfile.module.inode.addition_module.utile.ModelNames;
import im.zhaojun.zfile.module.inode.model.entity.Inode;
import im.zhaojun.zfile.module.inode.model.entity.InodeAdditionMessage;
import im.zhaojun.zfile.module.inode.model.result.InodeInfoResult;
import im.zhaojun.zfile.module.inode.model.result.base.InodeItemResult;
import im.zhaojun.zfile.module.inode.service.impl.InodeAdditionMessageServiceImpl;
import im.zhaojun.zfile.module.inode.service.impl.InodeLinkService;
import im.zhaojun.zfile.module.inode.service.impl.InodeService;
import im.zhaojun.zfile.module.storage.context.StorageSourceContext;
import im.zhaojun.zfile.module.inode.model.enums.FileTypeEnum;
import im.zhaojun.zfile.module.storage.service.base.AbstractBaseFileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Api(tags = "文件索引模块")
@ApiSort(12)
@Slf4j
@RequestMapping("/api/inode/text")
@RestController
public class FileClassifyController {

    @Resource
    private InodeAdditionMessageServiceImpl inodeAdditionMessageService;
    @Resource
    private FileClassifyService textClassifyService;
    @Resource
    private StorageSourceContext storageSourceContext;
    @Resource
    private InodeLinkService inodeLinkService;
    @Resource
    private InodeService inodeService;
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "获取文本分类的所有类")
    @GetMapping("/getClassList/{type}")
    public AjaxJson<InodeInfoResult>  getTextClassList(@PathVariable FileTypeEnum type){
        textClassifyService.setOperateType(type.getValue());
        List<String> list = textClassifyService.getTextClasses();
        List<InodeItemResult> inodeItemResults=new ArrayList<>();
        for(String s:list){
            //设置索引节点属性，path为类型
            InodeItemResult inodeItemResult = new InodeItemResult();
            inodeItemResult.setName(s);
            inodeItemResult.setPath(ModelNames.TEXT_CLASSIFY.getValue());
            inodeItemResult.setType(im.zhaojun.zfile.module.storage.model.enums.FileTypeEnum.FOLDER);
            inodeItemResults.add(inodeItemResult);
        }
        InodeInfoResult inodeInfoResult = new InodeInfoResult();
        inodeInfoResult.setFiles(inodeItemResults);
        return AjaxJson.getSuccessData(inodeInfoResult);
    }


    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "获取某类下的文件，分页获取")
    @GetMapping("/getClassifyItem/{type}/{_class}/{page}/{size}")
    public AjaxJson<InodeInfoResult>  getClassItemList(@PathVariable String _class,
                                                       @PathVariable Integer page,
                                                       @PathVariable Integer size,
                                                       @PathVariable FileTypeEnum type){
        textClassifyService.setOperateType(type.getValue().toString());
        List<InodeAdditionMessage> itemByTextClass = textClassifyService.getItemByTextClass(_class, page, size);

        List<InodeItemResult> inodeItemResults=new ArrayList<>();
        for(InodeAdditionMessage iAM:itemByTextClass){
            InodeItemResult inodeItemResult=new InodeItemResult();
            Inode inode=inodeService.findById(iAM.getId());
            inodeItemResult.setName(inode.getName());
            inodeItemResult.setPath(
                    inodeService.findInodePathById(
                            inodeLinkService.findParentId(iAM.getId()))
            );
            Integer storageId = inode.getStorageId();
            inodeItemResult.setStorageId(storageId);
            inodeItemResult.setType(im.zhaojun.zfile.module.storage.model.enums.FileTypeEnum.FILE);
            System.out.println(inodeService.findInodePathById(
                    iAM.getId()));
            AbstractBaseFileService<?> fileService = storageSourceContext.getByStorageId(storageId);
            inodeItemResult.setUrl(fileService.getDownloadUrl(
                    inodeService.findInodePathById(
                            iAM.getId())));
            inodeItemResults.add(inodeItemResult);
        }
        InodeInfoResult inodeInfoResult=new InodeInfoResult();
        inodeInfoResult.setFiles(inodeItemResults);
        return AjaxJson.getSuccessData(inodeInfoResult);
    }
    @ApiOperationSupport(order = 3)
    @ApiOperation(value = "对某文件修改分类")
    @PostMapping("/textClassModify")
    public AjaxJson<Boolean> ModifyTextClassifyList(@RequestBody
                                 TextClassModifyRequest request){
        textClassifyService.setOperateType(request.getType().getValue());
        // 假设 totalList 和 deleteList 分别是包含字符串的列表
        List<String> totalList = request.getTotalList().stream()
                .map(obj -> obj.getLabel())
                .collect(Collectors.toList());

        List<Integer> deleteIndexList = request.getDeleteList().stream()
                .map(index -> Integer.parseInt(index) - 1)
                .collect(Collectors.toList());

        // 创建两个新的列表，一个用于存放不删除的元素，另一个用于存放待删除的元素
        List<String> remainList = new ArrayList<>();
        List<String> deleteItemList = new ArrayList<>();

        // 根据 deleteIndexList 中的下标将 totalList 分成两部分
        for (int i = 0; i < totalList.size(); i++) {
            String item = totalList.get(i);
            if (deleteIndexList.contains(i)) {
                deleteItemList.add(item); // 待删除的元素
            } else {
                remainList.add(item); // 不删除的元素
            }
        }
        TextClassifyInodeRequest textClassifyInodeRequest = request.getTextClassifyInodeRequest();

        Long inodeIdByPath = inodeService.findInodeIdByPath(
                StringUtils.removeDuplicateSlashes(
                        textClassifyInodeRequest.getPath()
                                + ZFileConstant.PATH_SEPARATOR_CHAR
                                + textClassifyInodeRequest.getName()
                ), textClassifyInodeRequest.getStorageId());
        return AjaxJson.getSuccessData(
                textClassifyService.textClassModify(inodeIdByPath,remainList ,deleteItemList));
    }

//    @ApiOperationSupport(order = 4)
//    @ApiOperation(value = "对某文件删除分类")
//    @PostMapping("/deleteTextClassifyList")
//    public AjaxJson<Boolean> deleteTextClassifyList(@RequestBody Long id,@RequestBody String aimClass){
//        return AjaxJson.getSuccessData(
//                textClassifyService.deleteTextClassList(id,aimClass));
//    }

    @ApiOperationSupport(order = 5)
    @ApiOperation(value = "搜索某文件分类")
    @PostMapping("/findTextClassifyList")
    public AjaxJson<TextClassModifyResult> findTextClassifyList(@RequestBody TextClassifyInodeRequest textClassifyInodeRequest){
        TextClassModifyResult textClassModifyResult = new TextClassModifyResult();
        textClassifyService.setOperateType(textClassifyInodeRequest.getType().getValue());
        Long inodeIdByPath = inodeService.findInodeIdByPath(
                StringUtils.removeDuplicateSlashes(
                        textClassifyInodeRequest.getPath()
                                + ZFileConstant.PATH_SEPARATOR_CHAR
                                + textClassifyInodeRequest.getName()
                        ), textClassifyInodeRequest.getStorageId());
        List<String> textClassList =
                textClassifyService.findRemainTextClassList(inodeIdByPath);
        List<TextClassModifyItem> totalList=new ArrayList<>();
        int k=1;
        for(String item:textClassList){
            TextClassModifyItem textClassModifyItem = new TextClassModifyItem();
            textClassModifyItem.setKey(String.valueOf(k));
            k++;
            textClassModifyItem.setLabel(item);
            totalList.add(textClassModifyItem);
        };
        textClassModifyResult.setTotalList(totalList);
        List<String> textDropClassList = textClassifyService.findTextClassDropList(inodeIdByPath);
        List<String> deleteList=new ArrayList<>();
        for(String item: textDropClassList){
            TextClassModifyItem textClassModifyItem = new TextClassModifyItem();
            textClassModifyItem.setKey(String.valueOf(k));
            deleteList.add(String.valueOf(k));
            k++;
            textClassModifyItem.setLabel(item);
            totalList.add(textClassModifyItem);
        };
        textClassModifyResult.setDeleteList(deleteList);
        return AjaxJson.getSuccessData(textClassModifyResult);
    }
}
