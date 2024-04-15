package im.zhaojun.zfile.api.baidu.image.model.api.result;

import com.baomidou.mybatisplus.annotation.TableName;
import im.zhaojun.zfile.api.baidu.image.model.api.result.base.ResultItem;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
@Data
@ApiModel(description = "百度API图像分类返回")
public class JsonResult implements Serializable {
    private List<ResultItem> result;
    private Integer result_num;
    private String log_id;
}
