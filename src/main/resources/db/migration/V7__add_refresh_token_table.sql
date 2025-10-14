CREATE TABLE refresh_tokens
(
    id          BIGINT AUTO_INCREMENT NOT NULL,
    token       VARCHAR(255) NOT NULL,
    expiry_date datetime     NOT NULL,
    user_id     BIGINT,
    CONSTRAINT pk_refresh_tokens PRIMARY KEY (id)
);

ALTER TABLE refresh_tokens
    ADD CONSTRAINT uc_refresh_tokens_token UNIQUE (token);
ALTER TABLE refresh_tokens
    ADD CONSTRAINT uc_refresh_tokens_user UNIQUE (user_id);

ALTER TABLE refresh_tokens
    ADD CONSTRAINT FK_REFRESH_TOKENS_ON_USER FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE;
