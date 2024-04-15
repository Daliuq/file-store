package im.zhaojun.zfile.module.inode.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
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

    /**
     * 新建
     * @param inodeLink
     * @return
     */
    public InodeLink insert(InodeLink inodeLink){
        inodeLinkMapper.insert(inodeLink);
        return inodeLink;
    }

    public Long findParentId(Long id) {
        QueryWrapper<InodeLink> wrapper = new QueryWrapper<>();
        wrapper.eq(InodeLink.CHILD_ID_COLUMN_NAME, id);
        InodeLink inodeLink = inodeLinkMapper.selectOne(wrapper);
        return inodeLink != null ? inodeLink.getParentId() : null;
    }
}
