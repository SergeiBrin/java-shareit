DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS items CASCADE;
DROP TABLE IF EXISTS bookings CASCADE;
DROP TABLE IF EXISTS comments CASCADE;
DROP TABLE IF EXISTS requests CASCADE;

CREATE TABLE IF NOT EXISTS users (
id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
name varchar(255) NOT NULL,
email varchar(255) NOT NULL,
CONSTRAINT un_user_email UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS requests (
id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
creator BIGINT,
description varchar(1000),
created timestamp,
CONSTRAINT fk_requests_creator FOREIGN KEY (creator) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS items (
id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
user_id BIGINT,
name varchar(255) NOT NULL,
description varchar(255),
available bool,
request BIGINT,
CONSTRAINT fk_items_user_id FOREIGN KEY (user_id) REFERENCES users(id),
CONSTRAINT fk_items_request FOREIGN KEY (request) REFERENCES requests(id)
);

CREATE TABLE IF NOT EXISTS bookings (
id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
start_time timestamp,
end_time timestamp,
status varchar(255),
item BIGINT,
booker BIGINT,
CONSTRAINT fk_bookings_item FOREIGN KEY (item) REFERENCES items(id),
CONSTRAINT fk_bookings_booker FOREIGN KEY (booker) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS comments (
id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
text varchar(1000),
item BIGINT,
author BIGINT,
created timestamp,
CONSTRAINT fk_comments_item FOREIGN KEY (item) REFERENCES items(id),
CONSTRAINT fk_comments_author FOREIGN KEY (author) REFERENCES users(id)
);

