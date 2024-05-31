ALTER TABLE share_link
ADD CONSTRAINT unique_share_public_key UNIQUE (share_public_key);
