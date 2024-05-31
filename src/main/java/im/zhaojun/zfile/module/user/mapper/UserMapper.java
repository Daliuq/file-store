package im.zhaojun.zfile.module.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import im.zhaojun.zfile.module.storage.model.entity.StorageSourceConfig;
import im.zhaojun.zfile.module.user.model.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {

}
