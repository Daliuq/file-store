CREATE TABLE file_history (
                       id BIGINT PRIMARY KEY AUTO_INCREMENT,
                       bake_up_storage_id INT NOT NULL comment '存储源id',
                       inode_id BIGINT NOT NULL comment '文件(夹)索引节点id',
                       bake_up_time TIMESTAMP comment '备份时间'
);
