CREATE TABLE root_inode (
                             id Bigint AUTO_INCREMENT PRIMARY KEY COMMENT 'ID',
                             inode_id BigInt NOT NULL COMMENT '根索引id',
                             user_id BIGINT NOT NULL COMMENT '用户id'
);

INSERT INTO root_inode (user_id, inode_id)
SELECT id, root_inode FROM user WHERE root_inode IS NOT NULL;

ALTER TABLE user DROP COLUMN root_inode;
