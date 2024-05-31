package im.zhaojun.zfile.module.share.model.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value="获取用户基本信息请求类")
public class SelectShareLinkRequest {
    @ApiModelProperty(value = "文件(夹)名称", required = true, example = "local")
    String fileName;
    @ApiModelProperty(value = "时间begin", required = true, example = "local")
    String beginTime;
    @ApiModelProperty(value = "时间end", required = true, example = "local")
    String endTime;
    @ApiModelProperty(value = "文件(夹)名称", required = true, example = "local")
    Integer selectModel = 0;
}
