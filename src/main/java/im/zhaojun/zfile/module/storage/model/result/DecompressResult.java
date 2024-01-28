package im.zhaojun.zfile.module.storage.model.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@ApiModel(value = "解压信息结果类")
@AllArgsConstructor
public class DecompressResult {
    //对一个文件解压后，要返回它被解压所在的存储源和路径(其实只有一层，也等于文件名称)
    @ApiModelProperty(value = "name=path=文件名称=文件路径" +
            "这时因为对一个文件解压后，要返回它被解压所在的存储源和路径(其实只有一层，也等于文件名称)")
    private String path;
    @ApiModelProperty(value = "存储源KEY")
    private String storageKey;

    public DecompressResult() {

    }
}
