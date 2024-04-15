package im.zhaojun.zfile.module.inode.addition_module.textClassify.model.result;

import im.zhaojun.zfile.module.inode.addition_module.textClassify.model.request.base.TextClassModifyItem;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(description = "请求文本分类响应")
public class TextClassModifyResult {
    List<TextClassModifyItem> totalList;
    List<String> deleteList;

}
