package im.zhaojun.zfile.module.storage.service.base;

import org.apache.catalina.webresources.FileResource;
import org.springframework.core.io.FileSystemResource;

import java.io.InputStream;

/***
 * 标记接口，说明服务是本地服务
 *
 * @author liuquan
 */
public interface LocalService {
    /**本地服务
     *
     *
     * @return 成功？
     * @author liuquan
     */
    void uploadFileStreamToLocal(String unloadFilePathAndName, InputStream inputStream);

    FileSystemResource getLocalFileResource(String localFilePathAndName);
}
