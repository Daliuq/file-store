package im.zhaojun.zfile.module.share.model.result;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.Date;

@Data
@ApiModel(value="分享文件文件列表信息结果类")
public class ShareLinkItemResult {
    private String publicKey;
    private String privateKey;
    private Date createTime;
    private Date expireTime;
    private String pathAndName;
}
