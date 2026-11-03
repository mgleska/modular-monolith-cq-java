CREATE TABLE mlr_dispatch
(
    id         BIGINT AUTO_INCREMENT             NOT NULL,
    status     ENUM ('QUEUE', 'SENDING', 'SENT') NOT NULL,
    source     VARCHAR(50)                       NOT NULL,
    source_id  BIGINT                            NOT NULL,
    recipient  VARCHAR(500)                      NOT NULL,
    message    MEDIUMTEXT                        NOT NULL,
    created_at DATETIME(6)                       NULL,
    updated_at DATETIME(6)                       NULL,
    CONSTRAINT PRIMARY KEY (id)
);