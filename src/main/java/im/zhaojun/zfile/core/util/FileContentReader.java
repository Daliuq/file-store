package im.zhaojun.zfile.core.util;

import im.zhaojun.zfile.module.inode.addition_module.textClassify.model.enums.ReadAble;

import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;


public class FileContentReader {

    public static String readTextFile(String filename, InputStream inputStream) {
        try {
            Path filePath = Paths.get(filename);

            // Check if the file type is supported
            if (isSupportedFile(filePath)) {
                // Read and return file content
                String s = readContent(inputStream, filePath);
                inputStream.close();
                return s;

            } else {

                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static boolean isSupportedFile(Path filePath) {
        // Check if the file has a supported extension
        String fileExtension = StringUtils.getFileExtension(filePath.getFileName().toString());
        return Arrays.stream(ReadAble.values())
                .anyMatch(readAble -> readAble.getExtension().equalsIgnoreCase(fileExtension));
    }

    private static String readContent(InputStream inputStream, Path filePath) throws IOException {
        String fileExtension = StringUtils.getFileExtension(filePath.getFileName().toString());
        ReadAble readAble = getReadAble(fileExtension);
        switch (readAble) {
            case DOC:
                return readDocContent(inputStream);
            case DOCX:
                return readDocxContent(inputStream);
            default:
                return readTextContent(inputStream);
        }
    }

    private static ReadAble getReadAble(String fileExtension) {
        return Arrays.stream(ReadAble.values())
                .filter(readAble -> readAble.getExtension().equalsIgnoreCase(fileExtension))
                .findFirst()
                .orElse(null); // Handle unsupported types appropriately
    }

    private static String readDocContent(InputStream inputStream) throws IOException {
        try (HWPFDocument document = new HWPFDocument(inputStream)) {
            WordExtractor extractor = new WordExtractor(document);
            return extractor.getText();
        }
    }

    private static String readDocxContent(InputStream inputStream) throws IOException {
        try (XWPFDocument document = new XWPFDocument(inputStream)) {
            XWPFWordExtractor extractor = new XWPFWordExtractor(document);
            return extractor.getText();
        }
    }

    private static String readTextContent(InputStream inputStream) throws IOException {
        // Read file content from InputStream
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        }
        return content.toString();
    }

}
