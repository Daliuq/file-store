package im.zhaojun.zfile.module.inode.chain;

import im.zhaojun.zfile.module.inode.model.request.InodeListRequest;
import im.zhaojun.zfile.module.inode.model.result.InodeItemResult;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.chain.impl.ContextBase;
import org.springframework.context.annotation.DependsOn;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@Builder
public class InodeContext extends ContextBase {

    /**
     * 文件索引节点列表请求
     */
    private InodeListRequest inodeListRequest;

    /**
     * 查询出来的索引列表
     */
    private List<InodeItemResult> inodeItemResultList;
}
