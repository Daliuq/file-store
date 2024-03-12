package im.zhaojun.zfile.module.inode.mapper;

import im.zhaojun.zfile.module.inode.model.entity.InodeAdditionMessage;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface InodeAdditionMessageMapper extends MongoRepository<InodeAdditionMessage,String> {

}
