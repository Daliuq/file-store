package im.zhaojun.zfile.module.inode.model.result;

import com.fasterxml.jackson.annotation.JsonIgnore;
import im.zhaojun.zfile.core.util.StringUtils;
import im.zhaojun.zfile.module.storage.model.enums.FileTypeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.models.auth.In;
import lombok.Data;

import java.util.Date;

@Data
@ApiModel(value="文件索引列表信息结果类")
public class InodeItemResult {
    @ApiModelProperty(value = "所在路径", example = "/home/")
    private String path;

    @ApiModelProperty(value = "所在存储源id", example = "/home/")
    private Integer storageId;
    @ApiModelProperty(value = "文件名", example = "a.mp4")
    private String name;

    @ApiModelProperty(value = "时间", example = "2020-01-01 15:22")
    private Date time;

    @ApiModelProperty(value = "大小", example = "1024")
    private Long size;

    @ApiModelProperty(value = "类型", example = "file")
    private FileTypeEnum type;
    /**
     * 获取路径和名称的组合, 并移除重复的路径分隔符 /.
     *
     * @return  路径和名称的组合
     */
    @JsonIgnore
    public String getFullPath() {
        return StringUtils.concat(path, name);
    }
}
