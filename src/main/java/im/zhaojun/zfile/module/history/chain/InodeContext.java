package im.zhaojun.zfile.module.history.chain;

import im.zhaojun.zfile.module.inode.model.entity.Inode;
import im.zhaojun.zfile.module.inode.model.request.InodeListRequest;
import org.apache.commons.chain.impl.ContextBase;

import java.util.List;

public class InodeContext extends ContextBase {

    /**
     * 文件索引节点列表请求
     */
    private InodeListRequest inodeListRequest;

    /**
     * 待查询名称
     */
    private List<Inode> inodes;
}
