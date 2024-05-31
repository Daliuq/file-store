package im.zhaojun.zfile.core.exception;

public class StorageSpaceInsufficientException extends RuntimeException{
    //当存储源空间不够新建用户时报错
    public StorageSpaceInsufficientException(String message) {
        super(message);
    }
}
