package im.zhaojun.zfile.module.history.modle.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
@ApiModel(description = "文件备份历史")
@TableName(value = "file_history")
public class FileHistory {

    @ApiModelProperty(value = "主键",example = "1")
    @TableId(value = "id",type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "备份的存储源id",example = "10")
    @TableField(value = "bake_up_storage_id")
    private Integer bakeUpStorageId;

    @ApiModelProperty(value = "备份的文件(夹)id",example = "10")
    @TableField(value = "inode_id")
    private Long inodeId;

    public static String INODE_ID_COLUMN_NAME= "inode_id";

    @ApiModelProperty(value = "备份时间",example = "yyyy/MM/dd HH:mm:ss")
    @TableField(value = "bake_up_time")
    private Date bakeUpTime;

    @ApiModelProperty(value = "备份时间",example = "example")
    @TableField(value = "name")
    private String name;
}
