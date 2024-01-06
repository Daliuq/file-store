package im.zhaojun.zfile.module.storage.service.base;
import org.springframework.core.io.FileSystemResource;
public interface DownloadStreamableService {
    FileSystemResource getDownloadStreamResource(String pathAndName);
}
