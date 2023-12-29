-- changeSet gluhov:8
INSERT INTO Post (writer_id, content, status) VALUES
                                                  (1, 'This is the first post content', 'ACTIVE'),
                                                  (1, 'This is the second post content', 'UNDER_REVIEW'),
                                                  (2, 'Another post by a different writer', 'DELETED');
