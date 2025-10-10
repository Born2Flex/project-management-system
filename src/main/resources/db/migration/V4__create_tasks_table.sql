CREATE TABLE tasks (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    status VARCHAR(50) NOT NULL,
    priority VARCHAR(50) NOT NULL,
    due_date_time TIMESTAMP,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    project_id BIGINT NOT NULL,
    assignee_id BIGINT NOT NULL,
    FOREIGN KEY (project_id) REFERENCES project_entity (id),
    FOREIGN KEY (assignee_id) REFERENCES user_entity (id)
);