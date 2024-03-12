package im.zhaojun.zfile.module.inode.model.request;

import im.zhaojun.zfile.module.inode.model.enums.AdditionServiceType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@ApiModel(description = "索引附件服务请求")
public class InodeAdditionServiceRequest {
    @ApiModelProperty(value = "额外服务类型", required = true, example = "local")
    @NotBlank(message = "服务类型不能为空")
    private AdditionServiceType additionServiceType;


}
