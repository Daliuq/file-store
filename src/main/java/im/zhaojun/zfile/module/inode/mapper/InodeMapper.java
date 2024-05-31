package im.zhaojun.zfile.module.inode.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import im.zhaojun.zfile.module.inode.model.entity.Inode;
import org.apache.ibatis.annotations.Mapper;

/**
 * 文件追踪设置 Mapper 接口
 *
 * @author zhaojun
 */
@Mapper
public interface InodeMapper extends BaseMapper<Inode> {

    //增

    /**
     * 根据存储源 ID , 并按照存储源 id 排序
     *
     * @param   storageId
     *          存储源 ID
     *
     * @return  存储源拓展配置列表
     */


}
