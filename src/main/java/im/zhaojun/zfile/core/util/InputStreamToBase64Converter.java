package im.zhaojun.zfile.core.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Base64;

public class InputStreamToBase64Converter {

    public static String convert(InputStream inputStream) throws IOException {
        // 1. 读取InputStream中的所有字节数据
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }
        byte[] bytes = outputStream.toByteArray();
        inputStream.close();
        outputStream.close();
        // 2. 使用Base64编码算法将字节数组转换为Base64编码的字符串
        return Base64.getEncoder().encodeToString(bytes);
    }
}
