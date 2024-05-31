ALTER TABLE share
    ADD COLUMN forbid bool default false;
ALTER TABLE share
    ADD CONSTRAINT unique_share UNIQUE (recipient_user_id,share_link_id);
