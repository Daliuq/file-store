package im.zhaojun.zfile.module.inode.model.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@ApiModel(description = "插入索引节点请求类")
public class InsertInodeRequest {
    @ApiModelProperty(value = "存储源 key", required = true, example = "local")
    @NotBlank(message = "存储源 key 不能为空")
    private String storageKey;

    @ApiModelProperty(value = "上传路径", example = "/movie", notes = "表示上传文件到哪个路径")
    private String path = "/";

    @ApiModelProperty(value = "上传的文件名", example = "test.mp4")
    @NotBlank(message = "上传的文件名不能为空")
    private String name;

    @ApiModelProperty(value = "是文件夹还是", example = "false")
    @NotBlank(message = "是否是文件夹不能为空")
    private Boolean isFolder;
}
