package im.zhaojun.zfile.module.inode.model.request;

import cn.hutool.core.util.StrUtil;
import im.zhaojun.zfile.core.util.StringUtils;
import im.zhaojun.zfile.core.validation.StringListValue;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@ApiModel(description = "获取文件夹下文件列表请求类")
public class InodeListRequest {
    @ApiModelProperty(value = "文件(夹)名称", required = true, example = "local")
    private String name;

    @StringListValue(message = "排序字段参数异常，只能是 name、size、time", vals = {"name", "size", "time"})
    private String orderBy;

    public void handleDefaultValue() {
        if (StrUtil.isEmpty(name)) {
            name = "";
        }
        if (StrUtil.isEmpty(orderBy)) {
            orderBy = "name";
        }
    }
}
