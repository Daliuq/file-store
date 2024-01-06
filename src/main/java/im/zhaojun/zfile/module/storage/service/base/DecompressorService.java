package im.zhaojun.zfile.module.storage.service.base;

import java.io.InputStream;

/***
 * 标记接口。说明能解压
 * @author liuquan
 */
public interface DecompressorService extends DownloadStreamableService,UploadStreamableService{
    String decompress(String pathAndName, InputStream inputStream);
}
