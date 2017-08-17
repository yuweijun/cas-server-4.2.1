## create table in database test

CREATE TABLE `users` (
  id int(11) NOT NULL AUTO_INCREMENT,
  username varchar(50) NOT NULL,
  password varchar(50) NOT NULL,
  salt varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`),
  INDEX index_username(username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

insert users (username, password, salt) values ('casuser', '2221743b6930692378a076f2411b6762', 'test-salt');

## add row as below

user/password is : casuser/Mellon

+----------+----------------------------------+-----------+
| username | password                         | salt      |
+----------+----------------------------------+-----------+
| casuser  | 2221743b6930692378a076f2411b6762 | test-salt |
+----------+----------------------------------+-----------+

