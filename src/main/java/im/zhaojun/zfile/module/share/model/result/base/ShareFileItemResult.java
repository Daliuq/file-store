package im.zhaojun.zfile.module.share.model.result.base;

import com.fasterxml.jackson.annotation.JsonIgnore;
import im.zhaojun.zfile.core.util.StringUtils;
import im.zhaojun.zfile.module.storage.model.enums.FileTypeEnum;
import im.zhaojun.zfile.module.storage.model.result.FileItemResult;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
@ApiModel(value="文件列表信息结果类")
public class ShareFileItemResult {

    @ApiModelProperty(value = "文件名", example = "a.mp4")
    private String name;

    @ApiModelProperty(value = "时间", example = "2020-01-01 15:22")
    private Date time;

    @ApiModelProperty(value = "大小", example = "1024")
    private Long size;

    @ApiModelProperty(value = "类型", example = "file")
    private FileTypeEnum type;

    @ApiModelProperty(value = "所在路径", example = "/home/")
    private String path;

    @ApiModelProperty(value = "下载地址", example = "http://www.example.com/a.mp4")
    private String url;

    @ApiModelProperty(value = "基索引id",example = "111")
    private String baseShareStorageKey;

    @ApiModelProperty(value = "基索引id",example = "111")
    private String baseSharePath;
    /**
     * 获取路径和名称的组合, 并移除重复的路径分隔符 /.
     *
     * @return  路径和名称的组合
     */
    @JsonIgnore
    public ShareFileItemResult(FileItemResult fileItemResult,String baseShareStorageKey,String baseSharePath){
        this.name=fileItemResult.getName();
        this.path=fileItemResult.getPath();
        this.size=fileItemResult.getSize();
        this.time=fileItemResult.getTime();
        this.type=fileItemResult.getType();
        this.url=fileItemResult.getUrl();
        this.baseShareStorageKey=baseShareStorageKey;
        this.baseSharePath=baseSharePath;
    }
    @JsonIgnore
    public ShareFileItemResult(){

    }
    @JsonIgnore
    public String getFullPath() {
        return StringUtils.concat(path, name);
    }
}
