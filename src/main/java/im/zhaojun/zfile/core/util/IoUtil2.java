package im.zhaojun.zfile.core.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class IoUtil2 {
    public static InputStream copyInputStream(InputStream originalInputStream) throws IOException {
        // 使用 ByteArrayOutputStream 读取原始 InputStream 中的数据
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        while ((length = originalInputStream.read(buffer)) != -1) {
            byteArrayOutputStream.write(buffer, 0, length);
        }

        // 将数据写入字节数组后，创建一个新的 ByteArrayInputStream
        return new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
    }
}
