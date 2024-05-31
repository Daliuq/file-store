ALTER TABLE inode
    ADD COLUMN `link_his_id` BIGINT COMMENT '链接的his_id,如果非null话，就是link节点';
