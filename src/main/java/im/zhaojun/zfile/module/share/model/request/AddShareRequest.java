package im.zhaojun.zfile.module.share.model.request;

import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel(value="获取用户基本信息请求类")
public class AddShareRequest {
    private Long shareUserId;
    private String sharePublicKey;
    private String sharePrivateKey;
}
