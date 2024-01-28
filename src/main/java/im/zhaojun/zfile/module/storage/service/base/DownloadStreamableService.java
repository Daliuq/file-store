package im.zhaojun.zfile.module.storage.service.base;

import java.io.IOException;
import java.io.InputStream;

public interface DownloadStreamableService {

    InputStream getDownloadStream(String pathAndName) throws IOException;
}
