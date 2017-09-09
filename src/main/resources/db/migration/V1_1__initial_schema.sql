CREATE TABLE payment_request (
  id BIGINT(20) NOT NULL AUTO_INCREMENT,
  requestor_address VARCHAR(50) NOT NULL,
  receiver_address VARCHAR(50) NOT NULL,
  euro2_payment_uri VARCHAR(250) NOT NULL,
  payer_address VARCHAR(50) NULLABLE,
  PRIMARY KEY (id)
);
