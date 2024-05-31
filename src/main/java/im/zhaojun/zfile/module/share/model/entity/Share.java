package im.zhaojun.zfile.module.share.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.sql.Timestamp;

@Data
@ApiModel(description = "分享基本属性")
@TableName(value = "share")
public class Share {
    @TableId(value = "id")
    @ApiModelProperty(value = "ID, 新增无需填写", example = "1")
    private Long id;
    public static String ID_COLUMN_NAME = "id";
    @TableField(value = "share_user_id")
    @ApiModelProperty(value = "分享者id", example = "1")
    private Long shareUserId;

    public static String SHARE_USER_ID_COLUMN_NAME = "share_user_id";

    @TableField(value = "recipient_user_id")
    @ApiModelProperty(value = "接收者id", example = "1")
    private Long recipientUserId;

    public static String RECIPIENT_USER_ID_COLUMN_NAME = "recipient_user_id";

    @TableField(value = "inode_id")
    @ApiModelProperty(value = " 分享对象索引id", example = "1")
    private Long inodeId;

    @TableField(value = "share_link_id")
    @ApiModelProperty(value = " 分享链接id", example = "1")
    private Long shareLinkId;

    @TableField(value = "receive_time")
    @ApiModelProperty(value = " 接收时间", example = "")
    private Timestamp receiveTime;

    @TableField(value = "expire_time")
    @ApiModelProperty(value = "过期时间", example = "")
    private Timestamp expireTime;

    @TableField(value = "forbid")
    @TableLogic
    @ApiModelProperty(value = "逻辑删除字段", example = "false")
    private Boolean forbid;
}
