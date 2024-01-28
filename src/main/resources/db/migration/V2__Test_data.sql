INSERT INTO Writer (firstName, lastName) VALUES
                                             ('John', 'Doe'),
                                             ('Jane', 'Smith'),
                                             ('Emily', 'Johnson');

INSERT INTO Label (name) VALUES
                             ('Technology'),
                             ('Health'),
                             ('Lifestyle');

INSERT INTO Post (writer_id, content, status) VALUES
                                                  (1, 'This is the first post content', 'ACTIVE'),
                                                  (1, 'This is the second post content', 'UNDER_REVIEW'),
                                                  (2, 'Another post by a different writer', 'DELETED');

INSERT INTO Post_Label (post_id, label_id) VALUES
                                               (1, 1),
                                               (1, 3),
                                               (2, 2),
                                               (3, 3);

INSERT INTO Writer_Post (writer_id, post_id) VALUES
                                                 (1, 1),
                                                 (1, 3),
                                                 (2, 2),
                                                 (3, 3);