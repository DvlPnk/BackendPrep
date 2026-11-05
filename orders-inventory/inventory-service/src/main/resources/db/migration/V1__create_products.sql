CREATE TABLE products (
    product_id BIGINT NOT NULL AUTO_INCREMENT,
    product_name VARCHAR(150) NOT NULL,
    price DECIMAL(12,2) NOT NULL,
    stock INT NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,

    PRIMARY KEY (product_id),
    CONSTRAINT chk_products_price CHECK (price >= 0),
    CONSTRAINT chk_products_stock CHECK (stock >= 0)
);