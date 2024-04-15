package im.zhaojun.zfile.core.util;

import org.apache.commons.imaging.ImageInfo;
import org.apache.commons.imaging.Imaging;

import java.io.IOException;
import java.io.InputStream;

public class ImageUtils {

    /**
     * 判断输入流是否为图片
     *
     *
     * @return 如果输入流表示的是有效的图片，则返回 true；否则返回 false
     */
    public static boolean isImagePathWithSupportedExtension(String path) {
        String lowercasePath = path.toLowerCase();
        return lowercasePath.endsWith(".png") ||
                lowercasePath.endsWith(".jpg") ||
                lowercasePath.endsWith(".jpeg") ||
                lowercasePath.endsWith(".bmp");
    }
}
