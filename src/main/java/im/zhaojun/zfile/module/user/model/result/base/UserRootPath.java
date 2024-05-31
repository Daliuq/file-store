package im.zhaojun.zfile.module.user.model.result.base;

import lombok.Data;

@Data
public class UserRootPath {
    private String path;
    private String storageKey;
    private Integer type;
    private String name;
}
