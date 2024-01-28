package im.zhaojun.zfile.module.inode.chain.command;

import im.zhaojun.zfile.core.util.FileComparator;
import im.zhaojun.zfile.module.inode.chain.InodeContext;
import im.zhaojun.zfile.module.inode.model.request.InodeListRequest;
import im.zhaojun.zfile.module.inode.model.result.InodeItemResult;
import im.zhaojun.zfile.module.storage.chain.FileContext;
import im.zhaojun.zfile.module.storage.model.request.base.FileListRequest;
import im.zhaojun.zfile.module.storage.model.result.FileItemResult;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class InodeSortCommand implements Command {

    @Override
    public boolean execute(Context context) throws Exception {
        InodeContext inodeContext = (InodeContext) context;

        List<InodeItemResult> inodeItemResultList = inodeContext.getInodeItemResultList();
        InodeListRequest inodeListRequest = inodeContext.getInodeListRequest();

        if (inodeListRequest.getOrderBy() == null) {
            return false;
        }

        // 创建副本, 防止排序和过滤对原数据产生影响
        List<InodeItemResult> copyList = new ArrayList<>(inodeItemResultList);
        //ToDo 比较器
        return false;
    }
}
