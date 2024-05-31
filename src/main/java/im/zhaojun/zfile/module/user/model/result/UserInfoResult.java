package im.zhaojun.zfile.module.user.model.result;

import im.zhaojun.zfile.module.user.model.entity.User;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.List;
@Data
@ApiModel(description = "User 返回用户信息响应类")
public class UserInfoResult {
    List<User> userItemResult;
}
