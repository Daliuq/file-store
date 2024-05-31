package im.zhaojun.zfile.module.storage.model.param;

import im.zhaojun.zfile.module.storage.annotation.StorageParamItem;
import im.zhaojun.zfile.module.storage.model.enums.StorageParamTypeEnum;

/**
 * 基本实现类，其他所有都继承他
 */
public class BaseParam implements IStorageParam{
    @StorageParamItem(name = "存储源容量",
            order = 1000,
            type = StorageParamTypeEnum.INPUT,
            defaultValue = "209715200",description = "存储源容量,单位B(1KB=1024B,1MB=1024KB,1GB=1024MB)",
            ignoreInput = false)
    private Long size;
}
