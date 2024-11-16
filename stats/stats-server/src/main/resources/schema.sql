CREATE TABLE IF NOT EXISTS endpointhits (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY,
  app VARCHAR(255) NOT NULL,
  uri VARCHAR(1024) NOT NULL,
  ip VARCHAR(20),
  timestamp TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  CONSTRAINT pk_endpointhit PRIMARY KEY (id)
);
