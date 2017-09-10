CREATE SCHEMA `wallet_server_db` DEFAULT CHARACTER SET utf8;

-- Might fail if flyway user has already been created for that node
CREATE USER 'flyway'@'%' IDENTIFIED BY 'q1w2e3r4';
GRANT ALL PRIVILEGES ON wallet_server_db.* TO flyway;

CREATE USER 'wallet-service'@'%' IDENTIFIED BY 'q1w2e3r4';
GRANT ALL PRIVILEGES ON wallet_server_db.* TO 'wallet-service';

USE wallet_server_db;