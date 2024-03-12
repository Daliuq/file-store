package im.zhaojun.zfile.module.inode.addition_module.textClassify.serivce;

import im.zhaojun.zfile.module.inode.model.entity.InodeAdditionMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Slf4j
public class TextClassifyService {
    @Resource
    private MongoTemplate mongoTemplate;

    public List<String> getTextClass(){
        List<String> distinct = mongoTemplate.findDistinct(
                InodeAdditionMessage.FILE_CLASSIFY_TYPE_COLUMN_NAME,
                InodeAdditionMessage.class, String.class);
        // 使用distinct操作符获取fileType字段的所有不同值
        return distinct;
    }
}
