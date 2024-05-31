package im.zhaojun.zfile.module.inode.module.classify.imageClassify.model.messagequeue.receive.base;

import lombok.Data;

@Data
public class ResultItem {
    String key;
    private double score;
}
