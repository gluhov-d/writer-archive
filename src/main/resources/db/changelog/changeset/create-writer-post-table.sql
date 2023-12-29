-- changeSet gluhov:5
CREATE TABLE Writer_Post (
                            writer_id BIGINT,
                            post_id BIGINT,
                            PRIMARY KEY (writer_id, post_id),
                            FOREIGN KEY (writer_id) REFERENCES Writer(id),
                            FOREIGN KEY (post_id) REFERENCES Post(id)
);