-- drop table if exists
--     users,
--     request,
--     items,
--     booking,
--     comments;

CREATE TABLE IF NOT EXISTS users
(
    id    BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name  VARCHAR(255)                            NOT NULL,
    email VARCHAR(45)                             NOT NULL,
    CONSTRAINT pk_user PRIMARY KEY (id),
    CONSTRAINT UQ_USER_EMAIL UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS request
(
    id          BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    description VARCHAR(200)                            NOT NULL,
    created     TIMESTAMP WITHOUT TIME ZONE             NOT NULL,
    creator_id  BIGINT                                  NOT NULL,
    CONSTRAINT pk_request PRIMARY KEY (id),
    CONSTRAINT fk_creator_id FOREIGN KEY (creator_id) REFERENCES users (id)
);

CREATE TABLE IF NOT EXISTS items
(
    id          BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    owner_id    BIGINT                                  NOT NULL,
    request_id  BIGINT,
    name        VARCHAR(255)                            NOT NULL,
    description VARCHAR(200)                            NOT NULL,
    available   BOOLEAN                                 NOT NULL,
    CONSTRAINT pk_item PRIMARY KEY (id),
    CONSTRAINT fk_owner_id FOREIGN KEY (owner_id) REFERENCES users (id),
    CONSTRAINT fk_request_id FOREIGN KEY (request_id) REFERENCES request (id)
);

CREATE TABLE IF NOT EXISTS booking
(
    id         BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    item_id    BIGINT                                  NOT NULL,
    booker_id  BIGINT                                  NOT NULL,
    start_rent TIMESTAMP WITHOUT TIME ZONE             NOT NULL,
    end_rent   TIMESTAMP WITHOUT TIME ZONE             NOT NULL,
    status     VARCHAR(255)                            NOT NULL,
    state      VARCHAR(255),
    CONSTRAINT pk_booking PRIMARY KEY (id),
    CONSTRAINT fk_item_id FOREIGN KEY (item_id) REFERENCES items (id),
    CONSTRAINT fk_booker_id FOREIGN KEY (booker_id) REFERENCES users (id)
);

CREATE TABLE IF NOT EXISTS comments
(
    id      BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    item_id BIGINT                                  NOT NULL,
    user_id BIGINT                                  NOT NULL,
    text    VARCHAR(200)                            NOT NULL,
    created timestamp                               NOT NULL,
    CONSTRAINT pk_comment PRIMARY KEY (id),
    CONSTRAINT fk_item_id_comments FOREIGN KEY (item_id) REFERENCES items (id),
    CONSTRAINT fk_user_id_comments FOREIGN KEY (user_id) REFERENCES users (id)
);