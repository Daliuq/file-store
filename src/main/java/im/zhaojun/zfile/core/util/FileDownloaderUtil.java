package im.zhaojun.zfile.core.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class FileDownloaderUtil{

    public static InputStream downloadFile(String fileUrl) throws IOException {
        URL url = new URL(fileUrl);
        URLConnection connection = url.openConnection();
        connection.connect();
        return connection.getInputStream();
    }
}
