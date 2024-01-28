package im.zhaojun.zfile.module.inode.model.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@ApiModel(description = "插入索引节点请求类")
public class UpdateInodeRequest {
    @ApiModelProperty(value = "存储源 key", required = true, example = "local")
    @NotBlank(message = "存储源 key 不能为空")
    private String storageKey;

    @ApiModelProperty(value = "上传路径", example = "/movie", notes = "表示上传文件到哪个路径")
    private String path = "/";

    @ApiModelProperty(value = "重命名的原文件名称", example = "test.txt")
    @NotBlank(message = "原文件名不能为空")
    private String name;

    @ApiModelProperty(value = "重命名后的文件名称", example = "text-1.txt")
    @NotBlank(message = "新文件名不能为空")
    private String newName;
}
