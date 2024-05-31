package im.zhaojun.zfile.module.share.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mongodb.DuplicateKeyException;
import im.zhaojun.zfile.core.util.StringUtils;
import im.zhaojun.zfile.module.share.mapper.ShareLinkMapper;
import im.zhaojun.zfile.module.share.mapper.ShareMapper;
import im.zhaojun.zfile.module.share.model.entity.Share;
import im.zhaojun.zfile.module.share.model.entity.ShareLink;
import io.swagger.v3.oas.annotations.servers.Server;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.List;
import javax.annotation.Resource;
import java.sql.Timestamp;
import java.util.Date;

@Service
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Slf4j
public class ShareLinkService {
    @Resource
    ShareLinkMapper shareLinkMapper;

    @Resource
    ShareMapper shareMapper;
    public ShareLink generatorShareLink(Long inodeId, Long userId,Long expireTime) {
        ShareLink shareLink = new ShareLink();
        shareLink.setShareUserId(userId);
        shareLink.setInodeId(inodeId);
        shareLink.setCreateTime(new Timestamp(new Date().getTime()));
        shareLink.setSharePrivateKey(StringUtils.generateRandomString(ShareLink.PRIVATE_KEY_LENGTH));
        shareLink.setSharePublicKey(StringUtils.generateRandomString(ShareLink.PUBLIC_KEY_LENGTH));
        shareLink.setExpireTime(new Timestamp(expireTime));


        boolean inserted = false;

        while (!inserted) {
            try {
                shareLink.setSharePrivateKey(StringUtils.generateRandomString(ShareLink.PRIVATE_KEY_LENGTH));
                shareLink.setSharePublicKey(StringUtils.generateRandomString(ShareLink.PUBLIC_KEY_LENGTH));
                shareLinkMapper.insert(shareLink);
                inserted = true; // 插入成功，退出循环
            } catch (DuplicateKeyException e) {
                // 如果插入失败，捕获重复键异常并重新生成键
                ;
            }
        }
        return shareLink;
    }

    public List<ShareLink> selectByUser(long loginIdAsLong) {
        QueryWrapper<ShareLink> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq(ShareLink.SHARE_USER_ID_COLUMN_NAME,loginIdAsLong);
        return shareLinkMapper.selectList(queryWrapper);
    }

    public void deleteLink(String publicKey) {
        QueryWrapper<ShareLink> shareLinkQueryWrapperQueryWrapper=new QueryWrapper<>();
        QueryWrapper<Share> shareQueryWrapper=new QueryWrapper<>();
        shareLinkQueryWrapperQueryWrapper.eq(ShareLink.SHARE_PUBLIC_KEY_COLUMN_NAME,publicKey);
        ShareLink shareLink = shareLinkMapper.selectOne(shareLinkQueryWrapperQueryWrapper);
        shareQueryWrapper.eq(Share.ID_COLUMN_NAME,shareLink.getId());
        shareLinkMapper.delete(shareLinkQueryWrapperQueryWrapper);
        shareMapper.delete(shareQueryWrapper);
    }
}
