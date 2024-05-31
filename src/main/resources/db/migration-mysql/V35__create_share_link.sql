CREATE TABLE share_link (
                id BIGINT PRIMARY KEY ,
                share_user_id BIGINT comment '所有者id',
                inode BIGINT comment '分享的索引',
                share_public_key varchar(30) comment '分享公钥',
                share_private_key varchar(10) comment '分享私钥',
                create_time datetime comment '创建时间',
                expire_time datetime comment '到期时间'
);
