package im.zhaojun.zfile.core.util;

import java.nio.file.Path;
import java.nio.file.Paths;

public class FilePathUtils {

    public static String convertPathToBackend(String frontendPath) {
        // 寻找最后一个 "/"
        int lastSlashIndex = frontendPath.lastIndexOf('/');

        if (lastSlashIndex != -1) {
            // 将最后一个 "/" 改成 "."
            frontendPath = frontendPath.substring(0, lastSlashIndex)
                    + "." + frontendPath.substring(lastSlashIndex + 1);
        }

        return frontendPath;
    }

    public static void main(String[] args) {
        // 示例用法
        String frontendPath = "/example/path/txt";
        String backendPath = convertPathToBackend(frontendPath);
        System.out.println(backendPath);
    }
}
