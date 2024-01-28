package im.zhaojun.zfile.module.history.modle.result;

import im.zhaojun.zfile.module.history.modle.result.base.HistoryItemResult;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
@Data
@ApiModel(description = "文件备份历史")
public class HistoryInfoResult {
    @ApiModelProperty(value="文件列表")
    private List<HistoryItemResult> files;
}
