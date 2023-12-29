-- changeSet gluhov:2
CREATE TABLE Post (
                      id BIGINT AUTO_INCREMENT PRIMARY KEY,
                      writer_id BIGINT,
                      content TEXT,
                      created TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                      updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                      status ENUM('ACTIVE', 'UNDER_REVIEW', 'DELETED'),
                      FOREIGN KEY (writer_id) REFERENCES Writer(id)
);