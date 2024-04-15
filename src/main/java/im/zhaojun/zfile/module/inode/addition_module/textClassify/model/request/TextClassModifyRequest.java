package im.zhaojun.zfile.module.inode.addition_module.textClassify.model.request;

import im.zhaojun.zfile.module.inode.addition_module.textClassify.model.request.base.TextClassModifyItem;
import im.zhaojun.zfile.module.inode.addition_module.utile.FileTypeEnum;
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
