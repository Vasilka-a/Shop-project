create table cart
(
    id            BIGSERIAL PRIMARY KEY,
    product_name  varchar(256) not null,
    product_code  varchar(10)  not null,
    product_image varchar,
    price         money
);

create table users
(
    id    BIGSERIAL PRIMARY KEY,
    email varchar unique not null
);
