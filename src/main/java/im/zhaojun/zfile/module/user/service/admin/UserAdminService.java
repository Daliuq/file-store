package im.zhaojun.zfile.module.user.service.admin;

import java.util.List;
import im.zhaojun.zfile.module.user.mapper.UserMapper;
import im.zhaojun.zfile.module.user.model.entity.User;
import im.zhaojun.zfile.module.user.service.UserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
@Service
public class UserAdminService {
    @Resource
    UserService userService;

    @Resource
    UserMapper userMapper;

    public List<User> getUsers(){
         return  userMapper.selectList(null);
    }

    public Boolean updateUser(User user){
        userMapper.updateById(user);
        return true;
    }

    public Boolean deleteAnUser(Integer id){
        userMapper.deleteById(id);
        return true;
    }

    public Boolean deleteBatchUser(List<Integer> ids){
        userMapper.deleteBatchIds(ids);
        return true;
    }

    public void insertUser(User user) {
        userMapper.insert(user);
    }
}
