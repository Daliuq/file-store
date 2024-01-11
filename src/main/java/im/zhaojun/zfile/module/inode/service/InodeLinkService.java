package im.zhaojun.zfile.module.inode.service;

import im.zhaojun.zfile.module.inode.mapper.InodeLinkMapper;
import im.zhaojun.zfile.module.inode.model.entity.InodeLink;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Slf4j
public class InodeLinkService {

    @Resource
    InodeLinkMapper inodeLinkMapper;

    public InodeLink save(InodeLink inodeLink){
        inodeLinkMapper.insert(inodeLink);
        return inodeLink;
    }
}
