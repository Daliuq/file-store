package im.zhaojun.zfile.module.inode.service;

import im.zhaojun.zfile.module.inode.model.entity.InodeAdditionMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Slf4j
public class FileClassifyService {
    @Resource
    private MongoTemplate mongoTemplate;

    private String operateType;

    public void setOperateType(String operateType) {
        this.operateType = operateType;
    }

    /**
     *
     * @return List<String> 返回所有的TextClass类型
     */
    public List<String> getTextClasses(){
        // 使用distinct操作符获取fileType字段的所有不同值
        Criteria criteria = Criteria
                //保证操作的是文本文档
                .where(InodeAdditionMessage.FILE_TYPE_COLUMN_NAME)
                .is(operateType);
        return mongoTemplate.findDistinct(Query.query(criteria),
                InodeAdditionMessage.FILE_CLASSIFY_TYPE_COLUMN_NAME,
                InodeAdditionMessage.class, String.class);
    }

    /**
     *
     * @param fileClassifyType 文件分类类型
     * @param page 页，从1开始
     * @param pageSize 页大小
     * @return 所有符合的索引节点的额外信息，主要是拿到inode_id
     */
    public List<InodeAdditionMessage> getItemByTextClass(String fileClassifyType,Integer page,Integer pageSize){
        //  分页查询	page页码，pageSize每页展示几个
        Pageable pageable = PageRequest.of(page - 1, pageSize, Sort.by(
                Sort.Order.desc("date")));

        // 创建查询条件
        Criteria criteria = Criteria
                .where(InodeAdditionMessage.FILE_CLASSIFY_TYPE_COLUMN_NAME)
                .in(fileClassifyType)
                //保证操作的是文本文档
                .and(InodeAdditionMessage.FILE_TYPE_COLUMN_NAME)
                .is(operateType);

        Query query = new Query(criteria).with(pageable);

        return this.mongoTemplate.find(query, InodeAdditionMessage.class);
    }
//
//    /**
//     *
//     * @param inodeId 索引id
//     * @param newClass 新的类
//     * @return 成功？
//     */
//    public Boolean addTextClassList(Long inodeId,String newClass){
//        Criteria criteria = Criteria
//                .where(InodeAdditionMessage.ID_COLUMN_NAME)
//                .is(inodeId)
//                //保证操作的是文本文档
//                .and(InodeAdditionMessage.FILE_TYPE_COLUMN_NAME)
//                .is(operateType);
//        Query query =new Query(criteria);
//        Update update = new Update();
//        update.addToSet(InodeAdditionMessage.FILE_CLASSIFY_TYPE_COLUMN_NAME, newClass); // 使用 $set 更新列表字段
//        mongoTemplate.updateFirst(query,update,InodeAdditionMessage.class,InodeAdditionMessage.FILE_CLASSIFY_TYPE_COLUMN_NAME);
//        return true;
//    }
//
//    /**
//     *
//     * @param inodeId 索引id
//     * @param aimClass 删除目标类
//     * @return 成功？
//     */
//    public Boolean deleteTextClassList(Long inodeId,String aimClass){
//        Criteria criteria = Criteria
//                .where(InodeAdditionMessage.ID_COLUMN_NAME)
//                .is(inodeId)
//                // 保证操作的是文本文档
//                .and(InodeAdditionMessage.FILE_TYPE_COLUMN_NAME)
//                .is(operateType);
//        Query query = new Query(criteria);
//
//        Update update = new Update();
//        update.pull(InodeAdditionMessage.FILE_CLASSIFY_TYPE_COLUMN_NAME, aimClass); // 使用 $pull 删除列表中的特定元素
//
//        mongoTemplate.updateFirst(query, update, InodeAdditionMessage.class);
//        return true;
//    }

    /**
     *
     * @param inodeId  索引id
     * @return 类型列表
     */
    public List<String> findRemainTextClassList(Long inodeId) {
        Criteria criteria = Criteria
                .where(InodeAdditionMessage.ID_COLUMN_NAME)
                .is(inodeId);
        Query query = new Query(criteria);

        InodeAdditionMessage inodeAdditionMessage = mongoTemplate.
                findOne(query, InodeAdditionMessage.class);
        if (inodeAdditionMessage != null) {
            return inodeAdditionMessage.getFileClassifyType();
        }
        return null;
    }

    public Boolean textClassModify(Long inodeId,
                                   List<String> remainList,
                                   List<String> deleteList) {
        Criteria criteria = Criteria
                .where(InodeAdditionMessage.ID_COLUMN_NAME)
                .is(inodeId);
        Update update = new Update();
        Query query = new Query(criteria);
        update.set(InodeAdditionMessage.FILE_CLASSIFY_TYPE_COLUMN_NAME,remainList);
        update.set(InodeAdditionMessage.Drop_FILE_TYPE_COLUMN_NAME,deleteList);

        mongoTemplate.updateFirst(query, update, InodeAdditionMessage.class);
        return true;
    }

    public List<String> findTextClassDropList(Long inodeId) {
        Criteria criteria = Criteria
                .where(InodeAdditionMessage.ID_COLUMN_NAME)
                .is(inodeId);
        Query query = new Query(criteria);

        InodeAdditionMessage inodeAdditionMessage =
                mongoTemplate.findOne(query, InodeAdditionMessage.class);
        if (inodeAdditionMessage != null) {
            return inodeAdditionMessage.getDropFileClassifyType();
        }
        return null;
    }
}
