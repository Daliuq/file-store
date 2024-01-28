package im.zhaojun.zfile.module.inode.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import im.zhaojun.zfile.core.model.entity.OwnIdInterface;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author liuquan
 */
@Data
@ApiModel(description = "文件索引节点")
@TableName(value = "inode")
public class Inode implements Serializable, OwnIdInterface {
    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty(value = "ID, 全局唯一，出于安全性考虑，需要根据全局唯一id生成器生成", example = "1")
    private Long id;
    public static String ID_COLUMN_NAME="id";
    @TableField(value = "storage_id")
    @ApiModelProperty(value = "存储源 id", example = "1")
    private Integer storageId;
    public static String STORAGE_ID_COLUMN_NAME="storage_id";

    @TableField(value = "`name`")
    @ApiModelProperty(value = "文件名称 name", example = "bucketName")
    private String name;
    public static String NAME_COLUMN_NAME="name";

    @TableField(value = "is_directory")
    @ApiModelProperty(value = "是否是目录（文件夹）", example = "false")
    private Boolean isDirectory;

    @TableField(value = "is_historical_version_service")
    @ApiModelProperty(value = "是否开启历史记录服务", example = "false")
    private Boolean isHistoricalVersionService;

    @TableField(value = "`is_delete`")
    @ApiModelProperty(value = "是否逻辑删除", example = "false")
    private Boolean isDelete;

    public static String IS_DELETE_COLUMN_NAME="is_delete";
    public static String IS_HISTORICAL_VERSION_SERVICE_COLUMN_NAME="is_delete";
}
