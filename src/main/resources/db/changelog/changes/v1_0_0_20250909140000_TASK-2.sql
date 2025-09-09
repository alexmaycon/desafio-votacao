--liquibase formatted sql

--changeset TASK-2:1
CREATE TABLE agenda (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tx_title VARCHAR(255) NOT NULL,
    tx_description VARCHAR(1000),
    dt_created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    dt_updated TIMESTAMP DEFAULT NULL,
    ck_active BOOLEAN NOT NULL DEFAULT TRUE
);
--rollback DROP TABLE agenda;
