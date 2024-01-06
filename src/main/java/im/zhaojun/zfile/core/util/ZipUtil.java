package im.zhaojun.zfile.core.util;


/**
 * 流zip工具类
 */

import lombok.SneakyThrows;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipUtil {

    /**
     * 解压流
     *
     * @param inputStream
     * @return
     */
    @SneakyThrows
    public static ZipArchiveInputStream unzipStream(InputStream inputStream) {
        if (inputStream == null) {
            return null;
        }
        //1.Jdk原生Zip流，会因为文件或文件夹命名所用 字符集编码不匹配，报MALFORMED错（畸形的）
        //ZipInputStream zipInputStream = new ZipInputStream(inputStream);

        //2.Apach-commons-compress的Zip流，兼容性更好
        return new ZipArchiveInputStream(inputStream);
    }

        /**
         * 压缩流
         *
         * @param zipInputStream
         * @param targetDirectory 写入地址，绝对路径
         * @return
         */
    public static void writeStreamToFile(ZipArchiveInputStream zipInputStream, String targetDirectory) throws IOException {
        ZipArchiveEntry entry;
        while ((entry = zipInputStream.getNextZipEntry()) != null) {
            if (!entry.isDirectory()) {
                // 如果不是目录，则创建对应文件并写入数据
                String entryFileName = entry.getName();
                File entryFile = new File(targetDirectory, entryFileName);

                // 确保父目录存在
                File parent = entryFile.getParentFile();
                if (!parent.exists()) {
                    parent.mkdirs();
                }

                FileOutputStream fileOutputStream = new FileOutputStream(entryFile);
                byte[] buffer = new byte[1024];
                int length;
                while ((length = zipInputStream.read(buffer)) != -1) {
                    fileOutputStream.write(buffer, 0, length);
                }
                fileOutputStream.close();
            } else {
                // 如果是目录，则创建对应目录
                String entryDirName = entry.getName();
                File entryDir = new File(targetDirectory, entryDirName);
                entryDir.mkdirs();
            }
        }
        zipInputStream.close();
    }

    /**
     * 压缩流
     *
     * @param txtName
     * @param inputStream
     * @return
     */
    public static ByteArrayOutputStream zipStream(String txtName, ByteArrayInputStream inputStream) throws IOException {
        //Fail-Fast
        if (inputStream == null) {
            return null;
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ZipOutputStream zipOut = new ZipOutputStream(outputStream);
        zipOut.putNextEntry(new ZipEntry(txtName));

        int n;
        byte[] buffer = new byte[1024];
        while ((n = inputStream.read(buffer)) != -1) {
            zipOut.write(buffer, 0, n);
        }

        zipOut.close();
        inputStream.close();
        outputStream.close();
        return outputStream;
    }

}
