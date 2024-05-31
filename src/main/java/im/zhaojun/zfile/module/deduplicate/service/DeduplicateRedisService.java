package im.zhaojun.zfile.module.deduplicate.service;

import im.zhaojun.zfile.core.util.redis.RedisUtil;
import im.zhaojun.zfile.module.deduplicate.modle.FileDeduplicate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class DeduplicateRedisService {
    @Resource
    RedisUtil redisUtil;
    public Boolean SetKey(String key, FileDeduplicate value){
          return redisUtil.set(key,value);
    }
    public FileDeduplicate getByKey(String key){
        return (FileDeduplicate) redisUtil.get(key);
    }
}
