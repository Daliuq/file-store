package im.zhaojun.zfile.module.inode.controller;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.ObjectUtil;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSort;
import im.zhaojun.zfile.core.constant.ZFileConstant;
import im.zhaojun.zfile.core.util.*;
import im.zhaojun.zfile.core.util.inode.InodeUtil;
import im.zhaojun.zfile.module.config.utils.SpringMvcUtils;
import im.zhaojun.zfile.module.history.modle.entity.FileHistory;
import im.zhaojun.zfile.module.inode.module.classify.imageClassify.service.ImageClassifyService;
import im.zhaojun.zfile.module.inode.module.classify.model.request.TextClassModifyRequest;
import im.zhaojun.zfile.module.inode.module.classify.model.request.TextClassifyInodeRequest;
import im.zhaojun.zfile.module.inode.module.classify.model.request.base.TextClassModifyItem;
import im.zhaojun.zfile.module.inode.module.classify.model.result.TextClassModifyResult;
import im.zhaojun.zfile.module.inode.module.classify.textClassify.messagequeue.producer.TextSendProducer;
import im.zhaojun.zfile.module.inode.module.classify.textClassify.model.messagequeue.send.ClassifyMessageSend;
import im.zhaojun.zfile.module.inode.service.FileClassifyService;
import im.zhaojun.zfile.module.inode.model.entity.Inode;
import im.zhaojun.zfile.module.inode.model.entity.InodeAdditionMessage;
import im.zhaojun.zfile.module.inode.model.result.InodeInfoResult;
import im.zhaojun.zfile.module.inode.model.result.base.InodeItemResult;
import im.zhaojun.zfile.module.inode.service.impl.InodeAdditionMessageServiceImpl;
import im.zhaojun.zfile.module.inode.service.impl.InodeLinkService;
import im.zhaojun.zfile.module.inode.service.impl.InodeService;
import im.zhaojun.zfile.module.storage.context.StorageSourceContext;
import im.zhaojun.zfile.module.inode.model.enums.FileTypeEnum;
import im.zhaojun.zfile.module.storage.service.StorageSourceService;
import im.zhaojun.zfile.module.storage.service.base.AbstractBaseFileService;
import im.zhaojun.zfile.module.storage.service.base.AbstractProxyTransferService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.IOUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Api(tags = "文件索引模块")
@ApiSort(12)
@Slf4j
@RequestMapping("/api/inode/text")
@RestController
public class FileClassifyController {

    @Resource
    private TextSendProducer textSendProducer;
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
    @Resource
    private InodeUtil inodeUtil;

    @Resource
    private ImageClassifyService imageClassifyService;
    @Resource
    private StorageSourceService storageSourceService;
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "获取文本分类的所有类")
    @GetMapping("/getClassList/{type}")
    public AjaxJson<InodeInfoResult>  getTextClassList(@PathVariable FileTypeEnum type){

        //首先查询role，先跳过，先搞出来再说z
        textClassifyService.setOperateType(type.getValue());
        List<String> list = textClassifyService.getTextClasses();
        List<InodeItemResult> inodeItemResults=new ArrayList<>();
        for(String s:list){
            //设置索引节点属性，path为类型
            InodeItemResult inodeItemResult = new InodeItemResult();
            inodeItemResult.setName(s);
            inodeItemResult.setPath(type.getValue());
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

        System.out.println(StpUtil.getLoginIdAsLong());
        textClassifyService.setOperateType(type.getValue().toString());
        List<InodeAdditionMessage> itemByTextClass = textClassifyService.getItemByTextClass(_class, page, size);

        List<InodeItemResult> inodeItemResults=new ArrayList<>();
        for(InodeAdditionMessage iAM:itemByTextClass){
            InodeItemResult inodeItemResult=new InodeItemResult();
            Inode inode=inodeService.findById(iAM.getId());
            inodeItemResult.setName(inode.getName());
            inodeItemResult.setPath(
                    inodeService.findInodePathAndNameById(
                            inodeLinkService.findParentId(iAM.getId()))
            );
            Integer storageId = inode.getStorageId();
            inodeItemResult.setStorageId(storageId);
            inodeItemResult.setStorageKey(storageSourceService.findStorageKeyById(inode.getStorageId()));
            inodeItemResult.setType(im.zhaojun.zfile.module.storage.model.enums.FileTypeEnum.FILE);
            inodeItemResult.setSize(inode.getSize());
            System.out.println(inodeService.findInodePathAndNameById(
                    iAM.getId()));
            AbstractBaseFileService<?> fileService = storageSourceContext.getByStorageId(storageId);
            inodeItemResult.setUrl(inodeUtil.getUrl(inode.getId()));
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
        Integer storageId= request.getTextClassifyInodeRequest().getStorageId();
        if(ObjectUtil.isNull(request.getTextClassifyInodeRequest().getStorageId())){
            //说明没传，传输的是
            if(ObjectUtil.isNull(request.getTextClassifyInodeRequest().getStorageKey())){
                //说明根本没东西，
                throw new RuntimeException("请联系管理员");
            }
            storageId = storageSourceService.findIdByKey(request.getTextClassifyInodeRequest().getStorageKey())   ;
        }
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
                ), storageId);
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
        Integer storageId= textClassifyInodeRequest.getStorageId();
        if(ObjectUtil.isNull(textClassifyInodeRequest.getStorageId())){
            //说明没传，传输的是
            if(ObjectUtil.isNull(textClassifyInodeRequest.getStorageKey())){
                //说明根本没东西，
                throw new RuntimeException("请联系管理员");
            }
            storageId = storageSourceService.findIdByKey(textClassifyInodeRequest.getStorageKey())   ;
        }
        TextClassModifyResult textClassModifyResult = new TextClassModifyResult();
        textClassifyService.setOperateType(textClassifyInodeRequest.getType().getValue());
        Long inodeIdByPath = inodeService.findInodeIdByPath(
                StringUtils.removeDuplicateSlashes(
                        textClassifyInodeRequest.getPath()
                                + ZFileConstant.PATH_SEPARATOR_CHAR
                                + textClassifyInodeRequest.getName()
                        ), storageId);
        List<String> textClassList =
                textClassifyService.findRemainTextClassList(inodeIdByPath);
        List<TextClassModifyItem> totalList=new ArrayList<>();
        int k=1;
        if(!ObjectUtil.isNull(textClassList)){
            for(String item:textClassList){
                TextClassModifyItem textClassModifyItem = new TextClassModifyItem();
                textClassModifyItem.setKey(String.valueOf(k));
                k++;
                textClassModifyItem.setLabel(item);
                totalList.add(textClassModifyItem);
            };
        }
        textClassModifyResult.setTotalList(totalList);
        List<String> textDropClassList = textClassifyService.findTextClassDropList(inodeIdByPath);
        List<String> deleteList=new ArrayList<>();
        if(!ObjectUtil.isNull(textDropClassList))
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

    @ApiOperationSupport(order = 6)
    @ApiOperation(value = "分类")
    @PostMapping("/classify/{type}/{storageKey}/**")
    public AjaxJson<?> upload(@RequestParam MultipartFile file,@PathVariable("type")String type, @PathVariable("storageKey")
    String storageKey) throws IOException {
        if (file == null) {
            throw new RuntimeException("空文件不能为空");
        }
        // 获取上传路径
        String filePath = SpringMvcUtils.getExtractPathWithinPattern();
        filePath = filePath.replaceFirst("/([^/]+)$", ".$1");
        System.out.println(filePath);
        System.out.println(type);
        System.out.println(storageKey);
        AbstractBaseFileService<?> storageServiceByKey =
                storageSourceContext.getByStorageKey(storageKey);
        Integer storageId = storageServiceByKey.getStorageId();
        //4现在手上有存储元id和path和name，获取索引id,由于索引是文件更新后更新的，所有也要等待执行后获取
        Long inodeId = inodeService.findInodeIdByPath(
                StringUtils.removeDuplicateSlashes(
                        filePath)
                , storageId);
        InputStream inputStream = file.getInputStream();
        if(ObjectUtil.equal(type, im.zhaojun.zfile.module.inode.module.classify.model.FileTypeEnum.IMAGE.getValue())){

            imageClassifyService.useMyCNN(IOUtils.toByteArray(inputStream),inodeId);

        }else if(ObjectUtil.equal(type, im.zhaojun.zfile.module.inode.module.classify.model.FileTypeEnum.TEXT.getValue())){
            String context = FileContentReader.readTextFile(filePath,file.getInputStream());
            if(context!=null) {
                //2.如果可以，读取文本并发送至消息队列
                ClassifyMessageSend classifyMessageSend = new ClassifyMessageSend();
                classifyMessageSend.setText(context);
                classifyMessageSend.setId(inodeId);
                //接下来由python将该额外信息放入mongodb
                textSendProducer.sendMessage(classifyMessageSend);
            }

        }
        inputStream.close();
        return AjaxJson.getSuccessData(true);
    }
}
