package im.zhaojun.zfile.module.user.model.request;

import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel(description = "User InodeChildren")
public class UserInodeChildrenReq {
    private String path;
    private Integer storageId;
}
