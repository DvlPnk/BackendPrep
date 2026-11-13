CREATE TABLE stock_deductions (
    order_id BIGINT NOT NULL PRIMARY KEY,
    product_id BIGINT NOT NULL,
    quantity INT NOT NULL CHECK (quantity > 0)
);