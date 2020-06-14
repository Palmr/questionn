insert into shopp.product (product_id, product_name) values
    ( 1, 'Heinz Baked Beans' ),
    ( 2, 'Fairy Liquid' ),
    ( 3, 'White Bread' ),
    ( 4, 'Brown Bread' ),
    ( 5, 'Butter' ),
    ( 6, 'Extra Mature Cheddar' ),
    ( 7, 'Lea and Perrins' ),
    ( 8, 'Black peppercorns' );

insert into shopp.customer (customer_id, customer_name) values
    ( 1, 'Alice Brown'),
    ( 2, 'Chris Dickinson'),
    ( 3, 'Evelyn Fotheringay'),
    ( 4, 'Gareth Hughes'),
    ( 5, 'Isobel Jukes'),
    ( 6, 'Karl Legendre');

insert into shopp.checkout (checkout_id, customer_id, checkout_time) values
    ( 1, 2, '2020-06-13 14:55:06'),
    ( 2, 2, '2020-06-13 15:42:11'),
    ( 3, 4, '2020-06-14 09:15:33'),
    ( 4, 6, '2020-06-15 11:19:41');

insert into shopp.checkout_entry (checkout_id, product_id, product_price, product_quantity) values
    ( 1, 4, 1.23, 1 ),
    ( 1, 6, 2.60, 1 ),
    ( 2, 7, 2.05, 1 ),
    ( 3, 2, 1.50, 1 ),
    ( 4, 4, 1.23, 2 ),
    ( 4, 5, 1.02, 1 ),
    ( 4, 1, 0.62, 3 );
