package im.zhaojun.zfile.module.share.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.bouncycastle.util.Times;

import java.sql.Timestamp;

@Data
@ApiModel(description = "分享链接基本属性")
@TableName(value = "share_link")
public class ShareLink {
    @TableId(value = "id")
    @ApiModelProperty(value = "ID, 新增无需填写", example = "1")
    private Long id;
    public static String ID_COLUMN_NAME="id";
    @TableField(value = "share_user_id")
    @ApiModelProperty(value = "分享者id", example = "1")
    private Long shareUserId;
    public static String SHARE_USER_ID_COLUMN_NAME="share_user_id";
    @TableField(value = "inode_id")
    @ApiModelProperty(value = " 分享对象索引id", example = "1")
    private Long inodeId;

    @TableField(value = "share_public_key")
    @ApiModelProperty(value = "公钥", example = "1")
    private String sharePublicKey;
    public static Integer PUBLIC_KEY_LENGTH=30;
    public static String SHARE_PUBLIC_KEY_COLUMN_NAME="share_public_key";
    @TableField(value = "share_private_key")
    @ApiModelProperty(value = "私钥", example = "1")
    private String sharePrivateKey;

    public static String SHARE_PRIVATE_KEY_COLUMN_NAME="share_private_key";
    public static Integer PRIVATE_KEY_LENGTH=10;
    @TableField(value = "create_time")
    @ApiModelProperty(value = "创建时间", example = "1")
    private Timestamp createTime;

    @TableField(value = "expire_time")
    @ApiModelProperty(value = "到期时间", example = "1")
    private Timestamp expireTime;
}
