package im.zhaojun.zfile.core.exception.user;

import im.zhaojun.zfile.core.exception.ZFileRuntimeException;

public class StorageCapacityOverflowException extends ZFileRuntimeException {
    public StorageCapacityOverflowException(String message) {
        super(message);
    }
}
