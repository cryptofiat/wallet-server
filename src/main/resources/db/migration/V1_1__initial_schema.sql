CREATE TABLE payment_request (
  id BIGINT(20) NOT NULL AUTO_INCREMENT,
  requestor_address VARCHAR(50) NOT NULL,
  adressee_address VARCHAR(50) NULLABLE,
  PRIMARY KEY (id)
);
