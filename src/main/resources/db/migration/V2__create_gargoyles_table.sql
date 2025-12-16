CREATE TABLE gargoyles (
  id bigserial PRIMARY KEY,
  name varchar(255) NOT NULL UNIQUE,
  age INT,
  type VARCHAR(10) NOT NULL CHECK (type IN ('BAD', 'GOOD', 'CHILD')),
  status VARCHAR(10) NOT NULL CHECK (status IN ('ACTIVE', 'RETIRED')),
  hunger INT,
  happiness INT,
  health INT,
  experience INT,
  strength INT,
  speed INT,
  intelligence INT,
  last_fed FLOAT,
  last_played FLOAT,
  left_at FLOAT,
  user_id BIGINT NOT NULL,
  CONSTRAINT fk_gargoyles_user
        FOREIGN KEY (user_id)
        REFERENCES users (id)
        ON DELETE CASCADE
);