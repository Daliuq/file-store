package im.zhaojun.zfile.module.inode.addition_module.textClassify.model.request;

import im.zhaojun.zfile.module.inode.addition_module.utile.FileTypeEnum;
import lombok.Data;

@Data
public class TextClassifyInodeRequest {
    private String path;
    private String name;
    private Integer storageId;
    private FileTypeEnum type;
}
