package im.zhaojun.zfile.module.deduplicate.modle;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class FileDeduplicate implements Serializable {
    @ApiModelProperty(value = "对应的主索引id")
    Long inodeId;
    Long historyId;

    public FileDeduplicate(Long inodeIdByPath, Long historyId) {
        this.inodeId=inodeIdByPath;
        this.historyId=historyId;
    }
}
