CREATE TABLE prd_product
(
    id        INT AUTO_INCREMENT NOT NULL,
    ean       VARCHAR(48)        NOT NULL,
    name      VARCHAR(200)       NOT NULL,
    image_url VARCHAR(500)       NULL,
    created_at DATETIME           NULL,
    updated_at DATETIME           NULL,
    PRIMARY KEY (id)
);

CREATE INDEX idx_31a87aa12669115dfc1554c57 ON prd_product (ean);

CREATE TABLE prd_product_quantity
(
    id         INT AUTO_INCREMENT NOT NULL,
    store_id   INT                NOT NULL,
    product_id INT                NOT NULL,
    quantity   INT                NOT NULL,
    created_at DATETIME           NULL,
    updated_at DATETIME           NULL,
    PRIMARY KEY (id)
);

ALTER TABLE prd_product_quantity
    ADD CONSTRAINT uc_2ee8a4a85deaafba96315422f UNIQUE (store_id, product_id);
