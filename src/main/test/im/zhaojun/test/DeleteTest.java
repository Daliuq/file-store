package im.zhaojun.test;

import im.zhaojun.zfile.ZfileApplication;
import im.zhaojun.zfile.module.share.mapper.ShareMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import javax.annotation.Resource;

@SpringBootTest(classes = ZfileApplication.class)
@DirtiesContext
public class DeleteTest {
    @Resource
    ShareMapper shareMapper;
    @Test
    public void uOASRTest(){
        shareMapper.deleteById(1793530887230869505L);
    }
}
