package im.zhaojun.zfile.api.baidu.image.model.api.result.base;

import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel(description = "百度API图像分类返回内部类")
public class ResultItem {
    private double score;
    private String root;
    private String keyword;
}
