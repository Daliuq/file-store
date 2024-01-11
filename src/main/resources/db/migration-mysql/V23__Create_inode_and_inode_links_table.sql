CREATE TABLE inode (
                      id BIGINT PRIMARY KEY AUTO_INCREMENT,
                      storage_id INT NOT NULL comment '存储源id',
                      `name` VARCHAR(100) NOT NULL comment '文件名称或文件夹名称',
                      is_directory bool NOT NULL comment '是不是文件夹',
                      is_historical_version_service BOOL NOT NULL DEFAULT false,
                      is_delete bool default false comment '是否逻辑删除'
);
CREATE TABLE inode_link (
                      parent_id BIGINT,
                      child_id BIGINT,
                      PRIMARY KEY (parent_id, child_id)
);
