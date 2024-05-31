package im.zhaojun.zfile.module.user.service;

import im.zhaojun.zfile.module.user.mapper.UserMapper;
import im.zhaojun.zfile.module.user.model.entity.User;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
@Service
public class LoginService {
    @Resource
    private UserMapper userMapper;


    public String getPassword(Long id) {
        User user = userMapper.selectById(id);
        return user.getPassword();
    }
}
