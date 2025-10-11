CREATE TABLE projects
(
    id          BIGINT AUTO_INCREMENT NOT NULL,
    name        VARCHAR(255) NOT NULL,
    description VARCHAR(255) NOT NULL,
    status      VARCHAR(255) NOT NULL,
    CONSTRAINT pk_projects PRIMARY KEY (id)
);

ALTER TABLE projects
    ADD CONSTRAINT uc_projects_name UNIQUE (name);