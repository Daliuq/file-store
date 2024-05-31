package im.zhaojun.zfile.module.inode.module.classify.model.request;

import im.zhaojun.zfile.module.inode.module.classify.model.FileTypeEnum;
import lombok.Data;

@Data
public class TextClassifyInodeRequest {
    private String path;
    private String name;
    private Integer storageId;
    private FileTypeEnum type;
    private String storageKey;
}
