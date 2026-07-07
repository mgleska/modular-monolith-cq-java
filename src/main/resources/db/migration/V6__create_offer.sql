CREATE TABLE ofr_offer
(
    id           INT AUTO_INCREMENT NOT NULL,
    version      INT    DEFAULT 0   NOT NULL,
    store_id     INT                NOT NULL,
    external_id  VARCHAR(50)        NOT NULL,
    product_ean  VARCHAR(48)        NOT NULL,
    product_name VARCHAR(200)       NULL,
    price        INT                NOT NULL,
    lowest_price INT                NULL,
    `visible`    BIT(1) DEFAULT 1   NOT NULL,
    product_id   INT                NULL,
    created_at   DATETIME           NULL,
    updated_at   DATETIME           NULL,
    PRIMARY KEY (id)
);

ALTER TABLE ofr_offer
    ADD CONSTRAINT uc_279269de03b21d962f15d4657 UNIQUE (store_id, product_ean);