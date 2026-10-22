CREATE TABLE t_training_record
(
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    description VARCHAR(255) NOT NULL,
    created_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;