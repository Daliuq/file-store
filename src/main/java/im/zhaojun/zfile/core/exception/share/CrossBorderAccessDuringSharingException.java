package im.zhaojun.zfile.core.exception.share;

import im.zhaojun.zfile.core.exception.ZFileRuntimeException;

public class CrossBorderAccessDuringSharingException extends ZFileRuntimeException {
    public CrossBorderAccessDuringSharingException(String message) {
        super(message);
    }
}
