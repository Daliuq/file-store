package im.zhaojun.zfile.module.storage.controller.file;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSort;
import im.zhaojun.zfile.core.constant.ZFileConstant;
import im.zhaojun.zfile.core.util.AjaxJson;
import im.zhaojun.zfile.core.util.StringUtils;
import im.zhaojun.zfile.module.password.service.PasswordConfigService;
import im.zhaojun.zfile.module.storage.annotation.CheckPassword;
import im.zhaojun.zfile.module.storage.context.StorageSourceContext;
import im.zhaojun.zfile.module.storage.model.request.base.FileItemRequest;
import im.zhaojun.zfile.module.storage.model.request.operator.DecompressFileRequest;
import im.zhaojun.zfile.module.storage.service.base.AbstractBaseFileService;
import im.zhaojun.zfile.module.storage.service.base.DecompressorService;
import im.zhaojun.zfile.module.storage.service.base.DownloadStreamableService;
import im.zhaojun.zfile.module.storage.service.base.MoverService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipInputStream;

/***
 * 用于实现解压相关业务
 *
 *
 * @author liuquan
 */
@Api(tags = "文件操作模块")
@ApiSort(9)
@Slf4j
@RestController
@RequestMapping("/api/file/decompress")
public class FileDecompressController {
    @Resource
    private StorageSourceContext storageSourceContext;

    @Resource
    private PasswordConfigService passwordConfigService;

    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "解压zip")
    @PostMapping("/zip")
    @CheckPassword(storageKeyFieldExpression = "[0].storageKey",
            pathFieldExpression = "[0].path",
            passwordFieldExpression = "[0].password")
    public AjaxJson<?> zip(@Valid @RequestBody DecompressFileRequest decompressFileRequest) {
        //1通过能下载到本地的接口中的方法，从云端获取文件流
        //1.1获取请求解压的文件服务对象
        AbstractBaseFileService<?> fileService = storageSourceContext.getByStorageKey(decompressFileRequest.getStorageKey());
        //1.2如果该文件服务没有提供解压能力
        if (!(fileService instanceof DownloadStreamableService)) {
            //TODO return 错误
            return AjaxJson.getError("该文件服务下文件无法在线解压");
        }
        DecompressorService decompressorService = (DecompressorService) fileService;

        //1.3获取文件流,Download able能获得下载流
        InputStream inputStream = null;
        try {
            //1.4inputStream
            String pathAndName=StringUtils.removeDuplicateSlashes(decompressFileRequest.getPath()+ ZFileConstant.PATH_SEPARATOR + decompressFileRequest.getName());
            inputStream = decompressorService.getDownloadStreamResource(pathAndName).getInputStream();
            //应该获取解压服务，然后再在解压服务下解压
            String decompressFileName = decompressorService.decompress(decompressFileRequest.getPath(), inputStream);
            return AjaxJson.getSuccessData(decompressFileName);

        } catch (IOException e) {
            e.printStackTrace();
            return AjaxJson.getError("该文件服务获取文件输入流错误或其他错误");
        }finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException ee) {
                    ee.printStackTrace(); // 或者记录日志
                }
            }
        }
    }

}
