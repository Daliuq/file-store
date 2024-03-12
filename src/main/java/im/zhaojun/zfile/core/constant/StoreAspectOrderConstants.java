package im.zhaojun.zfile.core.constant;

import org.springframework.context.annotation.Configuration;

@Configuration
public class StoreAspectOrderConstants {
    /**
     * 校验密码
     */
    public static final int CHECK_PASSWORD_ASPECT_ORDER = 1;
    /**
     * 校验文件
     */
    public static final int FILE_OPERATOR_CHECK_ASPECT_ORDER = 2;
    /**
     * 文件操作异常//至此文件层结束
     */
    public static final int FILE_OPERATOR_EXCEPTION_ASPECT_ORDER = 3;
    /**
     * 索引节点操作
     */
    public static final int INODE_TRACK_ASPECT_ORDER = 5;

    public static final int TEST_CLASSIFY_ASPECT_ORDER = 6;
    /**
     * 历史服务操作
     */
    public static final int HISTORY_SERVICE_ASPECT_ORDER =4;
}
