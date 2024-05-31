package im.zhaojun.zfile.module.inode.module.classify.imageClassify.model.messagequeue.send;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value="文本分类交换机发送类")
public class ClassifyMessageSend {
    @ApiModelProperty(value = "文本分类的内容", example = "asdsfadfsfasf")
    public byte[] image;

    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty(value = "对应索引节点ID, 全局唯一，出于安全性考虑，需要根据全局唯一id生成器生成", example = "1")
    private Long id;
}
