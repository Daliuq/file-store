package im.zhaojun.zfile.module.inode.chain;

import im.zhaojun.zfile.module.inode.chain.command.InodeSortCommand;
import org.apache.commons.chain.impl.ChainBase;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

@Component
public class InodeChain extends ChainBase {

    @Resource
    InodeSortCommand inodeSortCommand;
    @PostConstruct
    public void init() {

        this.addCommand(inodeSortCommand);

    }
}
