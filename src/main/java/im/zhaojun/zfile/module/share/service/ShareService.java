package im.zhaojun.zfile.module.share.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import im.zhaojun.zfile.module.share.mapper.ShareLinkMapper;
import im.zhaojun.zfile.module.share.mapper.ShareMapper;
import im.zhaojun.zfile.module.share.model.entity.Share;
import im.zhaojun.zfile.module.share.model.entity.ShareLink;
import im.zhaojun.zfile.module.user.mapper.UserMapper;
import im.zhaojun.zfile.module.user.model.entity.User;
import io.swagger.v3.oas.annotations.servers.Server;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Mapper;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.swing.plaf.basic.BasicEditorPaneUI;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Slf4j
public class ShareService {
    @Resource
    ShareMapper shareMapper;
    @Resource
    ShareLinkMapper shareLinkMapper;
    @Resource
    UserMapper userMapper;
    public List<User> getShareUsers(Long userId) {
        ArrayList<User> users=new ArrayList<>();
        QueryWrapper<Share> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq(Share.RECIPIENT_USER_ID_COLUMN_NAME,userId);

        List<Share> shares = shareMapper.selectList(queryWrapper);
        System.out.println(shares);
        for(Share share:shares){
            Long shareUserId = share.getShareUserId();
            User user = userMapper.selectById(shareUserId);
            users.add(user);
        }
        return users;
    }

    public List<Long> getBaseInodesInOneUser(Long shareUserId, long loginIdAsLong) {
        QueryWrapper<Share> shareQueryWrapper=new QueryWrapper<>();
        shareQueryWrapper.eq(Share.RECIPIENT_USER_ID_COLUMN_NAME,loginIdAsLong);
        shareQueryWrapper.eq(Share.SHARE_USER_ID_COLUMN_NAME,shareUserId);
        List<Share> shares = shareMapper.selectList(shareQueryWrapper);
        List<Long> ids=new ArrayList<>();
        for(Share share:shares){
            ids.add(share.getInodeId());
        }
        return ids;

    }

    public void generatorShare(ShareLink shareLink, long userId) {
        System.out.println(shareLink);
        QueryWrapper<ShareLink> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(ShareLink.SHARE_PUBLIC_KEY_COLUMN_NAME,shareLink.getSharePublicKey());
        queryWrapper.eq(ShareLink.SHARE_PRIVATE_KEY_COLUMN_NAME,shareLink.getSharePrivateKey());
        queryWrapper.eq(ShareLink.SHARE_USER_ID_COLUMN_NAME,shareLink.getShareUserId());

        shareLink = shareLinkMapper.selectOne(queryWrapper);


        Share share = new Share();

        if(shareLink.getExpireTime().after(new Timestamp(new Date().getTime()))){
            share.setInodeId(shareLink.getInodeId());
            share.setShareLinkId(shareLink.getId());
            share.setRecipientUserId(userId);
            share.setReceiveTime(new Timestamp(new Date().getTime()));
            share.setShareUserId(shareLink.getShareUserId());
            shareMapper.insert(share);
        }else{
            throw new RuntimeException("分享链接过期");
        }


    }

    public List<Share> getShares(long loginIdAsLong) {
        QueryWrapper<Share> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq(Share.SHARE_USER_ID_COLUMN_NAME,loginIdAsLong);
        return shareMapper.selectList(queryWrapper);
    }

    public void deleteById(Long id) {
        shareMapper.deleteById(id);
    }
}
