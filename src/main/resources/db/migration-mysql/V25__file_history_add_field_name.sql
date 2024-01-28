-- V25__file_history_add_field_name.sql

-- 添加一个名为 'name' 的字段到 'file_history' 表
ALTER TABLE file_history
    ADD COLUMN name VARCHAR(255) COMMENT '文件(夹)名称';
