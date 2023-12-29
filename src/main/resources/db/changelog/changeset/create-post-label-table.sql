-- changeSet gluhov:4
CREATE TABLE Post_Label (
                            post_id BIGINT,
                            label_id BIGINT,
                            PRIMARY KEY (post_id, label_id),
                            FOREIGN KEY (post_id) REFERENCES Post(id) ON DELETE CASCADE,
                            FOREIGN KEY (label_id) REFERENCES Label(id) ON DELETE CASCADE
);