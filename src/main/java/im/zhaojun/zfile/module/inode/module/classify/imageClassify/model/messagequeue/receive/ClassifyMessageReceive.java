package im.zhaojun.zfile.module.inode.module.classify.imageClassify.model.messagequeue.receive;

import im.zhaojun.zfile.module.inode.module.classify.imageClassify.model.messagequeue.receive.base.ResultItem;
import lombok.Data;
import java.util.List;

@Data
public class ClassifyMessageReceive {
        private Long inodeId;
        private List<ResultItem> result;
}
