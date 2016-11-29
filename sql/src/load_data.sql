COPY MENU
FROM '/home/csmajs/jhsie007/Desktop/CafePOS/data/menu.csv'
WITH DELIMITER ';';

COPY USERS
FROM '/home/csmajs/jhsie007/Desktop/CafePOS/data/users.csv'
WITH DELIMITER ';';

COPY ORDERS
FROM '/home/csmajs/jhsie007/Desktop/CafePOS/data/orders.csv'
WITH DELIMITER ';';
ALTER SEQUENCE orders_orderid_seq RESTART 87257;

COPY ITEMSTATUS
FROM '/home/csmajs/jhsie007/Desktop/CafePOS/data/itemStatus.csv'
WITH DELIMITER ';';

