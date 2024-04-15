package im.zhaojun.test;

import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class TTT {
    public static void main(String[] args) {
        String base64String = "KdqIlfdbiGvJ209wizD3b/HiJWyjF";
        byte[] imageData = java.util.Base64.getDecoder().decode(base64String);

        try (InputStream inputStream = new ByteArrayInputStream(imageData)) {
            String format = ImageIO.getImageReadersByMIMEType("image/jpeg").next().getFormatName();
            System.out.println("Detected image format: " + format);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
