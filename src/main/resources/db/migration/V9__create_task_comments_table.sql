CREATE TABLE task_comments
(
    id            BIGINT AUTO_INCREMENT NOT NULL,
    text          VARCHAR(255) NOT NULL,
    created_at    datetime     NOT NULL,
    updated_at    datetime     NULL,
    task_id       BIGINT       NOT NULL,
    author_id     BIGINT       NOT NULL,
    CONSTRAINT pk_task_comments PRIMARY KEY (id)
);

ALTER TABLE task_comments
    ADD CONSTRAINT FK_TASK_COMMENTS_ON_AUTHOR FOREIGN KEY (author_id) REFERENCES users (id);

ALTER TABLE task_comments
    ADD CONSTRAINT FK_TASK_COMMENTS_ON_TASK FOREIGN KEY (task_id) REFERENCES tasks (id);