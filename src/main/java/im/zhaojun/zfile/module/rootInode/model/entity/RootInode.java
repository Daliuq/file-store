package im.zhaojun.zfile.module.rootInode.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import im.zhaojun.zfile.module.rootInode.model.request.RootInodeInsertAndUpdateRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "根节点实体类")
@TableName(value = "root_inode")
public class RootInode {
    @TableId(value = "id",type = IdType.AUTO)
    @ApiModelProperty(value = "ID, 新增无需填写", example = "1")
    private Long id;

    public static String ID_COLUMN_NAME="id";

    @TableField(value = "inode_id")
    @ApiModelProperty(value = "索引 id", example = "1")
    private Long inodeId;

    public static String INODE_ID_COLUMN_NAME="inode_id";

    @TableField(value = "user_id")
    @ApiModelProperty(value = "用户 id", example = "1")
    private Long userId;

    public static String USER_ID_COLUMN_NAME="user_id";

    @TableField(value = "type")
    @ApiModelProperty(value = "根类型", example = "1")
    private Integer Type;

    public static String TYPE_COLUMN_NAME = "type";

    @TableField(value = "name")
    @ApiModelProperty(value = "根名称", example = "1")
    private String name;

    public static String name_COLUMN_NAME = "name";

    @TableField(value = "storage_capacity")
    @ApiModelProperty(value = "空间大小", example = "1")
    private Long storageCapacity;

    public static String STORAGE_CAPACITY_COLUMN_NAME = "storage_capacity";

}
