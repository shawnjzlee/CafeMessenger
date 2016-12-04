COPY MENU
FROM '/extra/jhsie007/CafePOS/data/menu.csv'
WITH DELIMITER ';';

COPY USERS
FROM '/extra/jhsie007/CafePOS/data/users.csv'
WITH DELIMITER ';';

COPY ORDERS
FROM '/extra/jhsie007/CafePOS/data/orders.csv'
WITH DELIMITER ';';
ALTER SEQUENCE orders_orderid_seq RESTART 87257;

COPY ITEMSTATUS
FROM '/extra/jhsie007/CafePOS/data/itemStatus.csv'
WITH DELIMITER ';';

