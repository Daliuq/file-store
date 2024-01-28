package im.zhaojun.zfile.module.history.modle.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.ibatis.annotations.Param;

import javax.validation.constraints.NotBlank;
@Data
@ApiModel(description = "获取某索引节点历史信息请求类")
public class HistoryInfoRequest {
    @ApiModelProperty(value = "索引节点id", example = "1")
    private String path;

    private String storageKey;

    private String password;
}
