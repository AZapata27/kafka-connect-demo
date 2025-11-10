create table compras_new
(
    id       int,
    user_id  int,
    value    int,
    order_id int
);


create table compras
(
    id       serial,
    user_id  int,
    value    int,
    order_id int
);