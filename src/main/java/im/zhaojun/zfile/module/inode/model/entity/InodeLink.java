package im.zhaojun.zfile.module.inode.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel(description = "文件索引节点链接")
@TableName("inode_link")
public class InodeLink implements Serializable {
    @ApiModelProperty(value = "父id", example = "1")
    @TableField("parent_id") // 数据库表中的字段名
    private Long parentId;

    public static String PARENT_ID_COLUMN_NAME="parent_id";


    @ApiModelProperty(value = "子id", example = "2")
    @TableField("child_id") // 数据库表中的字段名
    private Long childId;

    public static String CHILD_ID_COLUMN_NAME="child_id";

    public InodeLink(Long parentId, Long childId) {
        this.parentId=parentId;
        this.childId=childId;
    }
}
