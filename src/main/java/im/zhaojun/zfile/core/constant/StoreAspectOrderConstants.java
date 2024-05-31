package im.zhaojun.zfile.core.constant;

import org.springframework.context.annotation.Configuration;

@Configuration
public class StoreAspectOrderConstants {
    /**
     * 校验密码以下7个为文件操作
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
     * 历史服务操作
     */
    public static final int HISTORY_SERVICE_ASPECT_ORDER =4;
    /**
     * 分类操作
     */
    public static final int CLASSIFY_ASPECT_ORDER = 5;
    /**
     * 分类操作2
     */
    public static final int CLASSIFY_ASPECT_ORDER_2 = 6;
    /**
     * 索引节点操作
     */
    public static final int INODE_TRACK_ASPECT_ORDER = 7;


    /**
     * 以下1个为删除节点操作
     */
    public static final int INODE_DELETE_ASPECT_ORDER = 1;

    /**
     * 以下1个为在获取上传链接前操作
     */
    public static final int STORAGE_SOURCE_STORAGE_CAPACITY_CHECK_ASPECT_ORDER = 1;
    public static final int USER_STORAGE_CAPACITY_CHECK_ASPECT_ORDER = 2;

}
