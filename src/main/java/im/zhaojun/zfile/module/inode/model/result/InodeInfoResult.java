package im.zhaojun.zfile.module.inode.model.result;

import im.zhaojun.zfile.module.inode.model.result.base.InodeItemResult;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(description = "索引服务返回")
public class InodeInfoResult {
    @ApiModelProperty(value="文件列表")
    private List<InodeItemResult> files;
}
