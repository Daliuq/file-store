package im.zhaojun.zfile.module.inode.module.classify.model.request;

import im.zhaojun.zfile.module.inode.module.classify.model.FileTypeEnum;
import im.zhaojun.zfile.module.inode.module.classify.model.request.base.TextClassModifyItem;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.List;
@Data
@ApiModel(description = "保存文本分类请求")
public class TextClassModifyRequest {
   private List<TextClassModifyItem> totalList;
   private List<String> deleteList;
   private TextClassifyInodeRequest textClassifyInodeRequest;
   private FileTypeEnum type;
}
