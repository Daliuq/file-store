package im.zhaojun.zfile.module.share.model.request;

import io.swagger.annotations.ApiModel;
import io.swagger.models.auth.In;
import lombok.Data;

@Data
@ApiModel(value="获取用户基本信息请求类")
public class GetShareFileItemListRequest {
    private Long shareUserId;

    private String baseSharePath;

    private String baseShareStorageKey;

    private String fullPath;

}
