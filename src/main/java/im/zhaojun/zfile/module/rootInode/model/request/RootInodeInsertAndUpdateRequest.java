package im.zhaojun.zfile.module.rootInode.model.request;

import lombok.Data;

import java.text.ParseException;

@Data
public class RootInodeInsertAndUpdateRequest {
    private Long id;
    private Long userId;
    private String path;
    private String name;
    private Long storageCapacity;
    private String storageKey;
}
