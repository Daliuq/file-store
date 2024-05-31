package im.zhaojun.zfile.module.rootInode.model.request;

import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel(description = "User 信息请求类")
public class RootInodeRequest {
    Long userId;
    Long rootInodeId;
}
