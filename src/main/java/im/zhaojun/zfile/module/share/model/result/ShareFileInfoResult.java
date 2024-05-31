package im.zhaojun.zfile.module.share.model.result;

import im.zhaojun.zfile.module.share.model.result.base.ShareFileItemResult;
import im.zhaojun.zfile.module.storage.model.result.FileItemResult;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(value="分享文件文件列表信息结果类")
@AllArgsConstructor
public class ShareFileInfoResult {
    @ApiModelProperty(value="文件列表")
    private List<ShareFileItemResult> files;

}
