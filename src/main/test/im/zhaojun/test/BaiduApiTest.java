package im.zhaojun.test;

import im.zhaojun.zfile.ZfileApplication;
import im.zhaojun.zfile.api.baidu.image.UniversalObjectAndSceneRecognition;
import im.zhaojun.zfile.api.baidu.image.model.api.result.JsonResult;
import im.zhaojun.zfile.core.util.InputStreamToBase64Converter;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import javax.annotation.Resource;
import java.io.*;
import java.nio.file.Files;

@SpringBootTest(classes = ZfileApplication.class)
@DirtiesContext
public class BaiduApiTest {
    @Resource
    UniversalObjectAndSceneRecognition universalObjectAndSceneRecognition;


    @Test
    public void uOASRTest() throws IOException {
        String imagePath = "C:\\Users\\86185\\Pictures\\IMG_2122.jpg"; // 请将路径替换为实际的图片路径

        // 2. 创建File对象
        File imageFile = new File(imagePath);
        try (InputStream inputStream = Files.newInputStream(imageFile.toPath())) {
            String convert = InputStreamToBase64Converter.convert(inputStream);
            JsonResult classify = universalObjectAndSceneRecognition.classify(convert);
            System.out.println(classify);
        }

    }
}
