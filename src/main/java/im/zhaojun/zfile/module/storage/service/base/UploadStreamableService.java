package im.zhaojun.zfile.module.storage.service.base;

import java.io.InputStream;

public interface UploadStreamableService {
    void uploadStream(String unloadFilePathAndName,InputStream inputStream);
}
