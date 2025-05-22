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
values ('Серьги', 'E0001','earrings-1.jpg','Лучшие в мире серьги', 5000, 5),
       ('Серьги','E0002','earrings-2.jpeg','Лучшие в мире серьги', 4500, 5),
       ('Ожерелье', 'N0001','necklace.jpg','Лучшие в мире серьги', 7000, 3),
       ('Кольцо', 'R0001','ring.jpg','Лучшие в мире серьги', 2500, 2),
       ('Кольцо', 'R0002','ring-2.jpg','Лучшие в мире серьги', 1000, 5),
       ('Кольцо', 'R0003','ring-airplane.jpg','Лучшие в мире серьги', 2000, 7),
       ('Серьги',  'E0003','earrings-3.jpg','Лучшие в мире серьги', 6000, 4);
