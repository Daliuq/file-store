package im.zhaojun.zfile.module.share.model.result;

import im.zhaojun.zfile.module.share.model.result.base.ShareUserItemResult;
import im.zhaojun.zfile.module.storage.model.result.FileItemResult;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
@Data
@ApiModel(value="查询用户分享信息包装类")
public class ShareUserInfoResult {
    @ApiModelProperty(value="文件列表")
    private List<ShareUserItemResult> files;
}
