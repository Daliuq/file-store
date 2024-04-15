package im.zhaojun.zfile.module.inode.model.entity;



import im.zhaojun.zfile.module.inode.model.enums.FileTypeEnum;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.util.List;

@Data
@Document(collection="inodeadditionmessage")
public class InodeAdditionMessage {

    @MongoId
    @Id
    private Long id;
    public static String ID_COLUMN_NAME="id";
    private FileTypeEnum fileType;
    public static String FILE_TYPE_COLUMN_NAME="fileType";
    public static String FILE_CLASSIFY_TYPE_COLUMN_NAME="fileClassifyType";
    private List<String> fileClassifyType; // 将字段类型改为 List<String>
    public static String Drop_FILE_TYPE_COLUMN_NAME ="dropFileClassifyType";
    private List<String> dropFileClassifyType; // 将字段类型改为 List<String>

}
