package im.zhaojun.zfile.module.share.model.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value="删除基本信息请求类")
public class DeleteShareLinkRequest {
    @ApiModelProperty(value = "id", required = true, example = "local")
    Long id;
}
