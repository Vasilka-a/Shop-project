create table products
(
    id           BIGSERIAL PRIMARY KEY,
    product_name     varchar(256) not null,
    product_code varchar(10) not null,
    product_image varchar,
    description varchar,
    price money,
    quantity integer
);

insert into products (product_name, product_code, product_image, description,price, quantity)
values ('Серьги', 'E0001','earrings-1.jpg','Описание товара', 5000, 5),
       ('Серьги','E0002','earrings-2.jpeg','Описание товара', 4500, 5),
       ('Ожерелье', 'N0001','necklace.jpg','Описание товара', 7000, 3),
       ('Кольцо', 'R0001','ring.jpg','Описание товара', 2500, 2),
       ('Кольцо', 'R0002','ring-2.jpg','Описание товара', 1000, 5),
       ('Кольцо', 'R0003','ring-airplane.jpg','Описание товара', 2000, 7),
       ('Серьги',  'E0003','earrings-3.jpg','Описание товара', 6000, 4);
