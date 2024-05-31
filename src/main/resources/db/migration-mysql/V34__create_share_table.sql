CREATE TABLE share (
                      id BIGINT PRIMARY KEY,
                      share_user_id BIGINT comment '分享者id',
                      recipient_user_id BIGINT comment '接收者id',
                      inode_id BIGINT comment '分享索引id',
                      receive_time DATETIME comment '接收者接收时间',
                      expire_time datetime comment '过期时间(预留)'
);
