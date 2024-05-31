CREATE TABLE user (
                              id BIGINT PRIMARY KEY COMMENT '即为学号',
                              name VARCHAR(40),
                              password VArChar (40),
                              role INTEGER,
                              root_inode BIGINT,
                              storage_capacity BIGINT
);
