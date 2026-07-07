CREATE TABLE str_store
(
    id          INT AUTO_INCREMENT NOT NULL,
    external_id VARCHAR(10)        NOT NULL,
    name        VARCHAR(250)       NOT NULL,
    address     VARCHAR(250)       NOT NULL,
    created_at  DATETIME           NULL,
    updated_at  DATETIME           NULL,
    is_deleted  BIT(1)             NOT NULL,
    PRIMARY KEY (id)
);

CREATE INDEX deleted ON str_store (is_deleted);