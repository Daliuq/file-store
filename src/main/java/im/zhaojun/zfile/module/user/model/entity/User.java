package im.zhaojun.zfile.module.user.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "用户基本属性")
@TableName(value = "user")
public class User {
    @TableId(value = "id")
    @ApiModelProperty(value = "ID, 新增无需填写", example = "1")
    private Long id;
    @TableField(value = "name")
    @ApiModelProperty(value = "名称", example = "1")
    private String name;
    @TableField(value = "password")
    @ApiModelProperty(value = "密码", example = "1")
    private String password;
    @TableField(value = "role")
    @ApiModelProperty(value = "角色", example = "1")
    private int role;
    @TableField(value = "storage_capacity")
    @ApiModelProperty(value = "存储总容量", example = "1")
    private Long storageCapacity;
}
