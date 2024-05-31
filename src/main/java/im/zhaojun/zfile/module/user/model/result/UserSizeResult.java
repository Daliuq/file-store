package im.zhaojun.zfile.module.user.model.result;

import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel(description = "User 返回用户存放根存储信息响应类")
public class UserSizeResult {
    Long totalSize;
    Long usedSize;
}
