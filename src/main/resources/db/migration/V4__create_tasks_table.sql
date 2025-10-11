CREATE TABLE tasks
(
    id            BIGINT AUTO_INCREMENT NOT NULL,
    title         VARCHAR(255) NOT NULL,
    description   VARCHAR(255) NOT NULL,
    status        VARCHAR(255) NOT NULL,
    priority      VARCHAR(255) NOT NULL,
    due_date_time datetime NULL,
    created_at    datetime     NOT NULL,
    updated_at    datetime NULL,
    project_id    BIGINT       NOT NULL,
    assignee_id   BIGINT NULL,
    CONSTRAINT pk_tasks PRIMARY KEY (id)
);

ALTER TABLE tasks
    ADD CONSTRAINT FK_TASKS_ON_ASSIGNEE FOREIGN KEY (assignee_id) REFERENCES users (id);

ALTER TABLE tasks
    ADD CONSTRAINT FK_TASKS_ON_PROJECT FOREIGN KEY (project_id) REFERENCES projects (id);