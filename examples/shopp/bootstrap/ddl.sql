create schema shopp;

create table shopp.product (
    product_id bigint not null primary key,
    product_name varchar(128) not null,
    KEY (product_name)
);

create table shopp.customer (
    customer_id bigint not null primary key,
    customer_name varchar(128) not null
);

create table shopp.checkout (
    checkout_id bigint not null primary key,
    customer_id bigint not null,
    checkout_time datetime not null,
    KEY (customer_id)
);

create table shopp.checkout_entry (
    checkout_entry_id bigint not null auto_increment primary key,
    checkout_id bigint not null,
    product_id bigint not null,
    product_price decimal(10, 2) not null,
    product_quantity int,
    KEY (checkout_id)
);