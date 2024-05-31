package im.zhaojun.zfile.module.user.model.request;

import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel(description = "User 索引下列表信息请求类")
public class UserInodeListReq {
    Long inode_id;
}
