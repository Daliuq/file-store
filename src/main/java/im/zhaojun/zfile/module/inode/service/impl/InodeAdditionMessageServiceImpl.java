package im.zhaojun.zfile.module.inode.service.impl;

import cn.hutool.core.util.ObjectUtil;
import im.zhaojun.zfile.module.inode.mapper.InodeAdditionMessageMapper;
import im.zhaojun.zfile.module.inode.model.entity.Inode;
import im.zhaojun.zfile.module.inode.model.entity.InodeAdditionMessage;
import im.zhaojun.zfile.module.inode.service.InodeAdditionMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import java.util.ArrayList;
import java.util.List;

@Service
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Slf4j
public class InodeAdditionMessageServiceImpl implements InodeAdditionMessageService {

    @Resource
    InodeAdditionMessageMapper inodeAdditionMessageMapper;

    @Resource
    private MongoTemplate mongoTemplate;

    public boolean save(InodeAdditionMessage inodeAdditionMessage){
        InodeAdditionMessage save = mongoTemplate.save(inodeAdditionMessage);
        return ObjectUtil.isNull(save);
    }

    public boolean delete(Long inodeId) {
        try {
            mongoTemplate.remove(Query.query(
                    Criteria.where(InodeAdditionMessage.ID_COLUMN_NAME)
                            .is(inodeId)), InodeAdditionMessage.class);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
