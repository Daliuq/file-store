package im.zhaojun.zfile.module.deduplicate.util;

public class RedisKeyUtil {
    public static String getDeduplicateKey(Integer storageId,Long md5){
        return storageId.toString()+'-'+md5.toString();
    }
}
