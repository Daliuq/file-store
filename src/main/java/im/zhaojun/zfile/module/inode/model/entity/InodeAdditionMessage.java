package im.zhaojun.zfile.module.inode.model.entity;



import im.zhaojun.zfile.module.inode.model.enums.FileTypeEnum;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

@Data
@Document(collection="inodeadditionmessage")
public class InodeAdditionMessage {

    @MongoId
    private Long id;
    private FileTypeEnum fileType;
    public static String FILE_TYPE_COLUMN_NAME="fileType";
    private String fileClassifyType;
    public static String FILE_CLASSIFY_TYPE_COLUMN_NAME="fileClassifyType";
}
