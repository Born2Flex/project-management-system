CREATE TABLE project_developers
(
    project_id BIGINT NOT NULL,
    user_id    BIGINT NOT NULL,
    PRIMARY KEY (project_id, user_id),
    CONSTRAINT fk_project_developers_project
        FOREIGN KEY (project_id)
            REFERENCES projects (id)
            ON DELETE CASCADE,
    CONSTRAINT fk_project_developers_user
        FOREIGN KEY (user_id)
            REFERENCES users (id)
            ON DELETE CASCADE
);
