CREATE TABLE Writer (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        firstName VARCHAR(255) NOT NULL ,
                        lastName VARCHAR(255)
);

CREATE TABLE Label (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       name VARCHAR(255) NOT NULL
);

CREATE TABLE Post (
                      id BIGINT AUTO_INCREMENT PRIMARY KEY,
                      content TEXT NOT NULL ,
                      created TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                      updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                      status ENUM('ACTIVE', 'UNDER_REVIEW', 'DELETED')
);

CREATE TABLE Post_Label (
                            post_id BIGINT,
                            label_id BIGINT,
                            PRIMARY KEY (post_id, label_id),
                            FOREIGN KEY (post_id) REFERENCES Post(id) ON DELETE CASCADE ,
                            FOREIGN KEY (label_id) REFERENCES Label(id) ON DELETE CASCADE
);



CREATE TABLE Writer_Post (
                             writer_id BIGINT,
                             post_id BIGINT,
                             PRIMARY KEY (writer_id, post_id),
                             FOREIGN KEY (writer_id) REFERENCES Writer(id) ON DELETE CASCADE,
                             FOREIGN KEY (post_id) REFERENCES Post(id) ON DELETE CASCADE
);