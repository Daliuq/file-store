package im.zhaojun.zfile.module.user.model.result;

import im.zhaojun.zfile.module.user.model.result.base.UserRootPath;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import java.util.List;
@Data
@ApiModel(description = "User 返回用户存放根存储信息响应类")
public class RootPathResult {
    List<UserRootPath> rootPaths;
    Integer role;
}
