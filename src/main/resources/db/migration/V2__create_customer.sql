CREATE TABLE cst_customer
(
    id             INT AUTO_INCREMENT                          NOT NULL,
    selected_store INT DEFAULT 0                               NOT NULL,
    status         ENUM ('ACTIVE', 'INACTIVE', 'DEACTIVATING') NOT NULL,
    name           VARCHAR(250)                                NOT NULL,
    PRIMARY KEY (id)
);