--liquibase formatted sql

--changeset TASK-3:1
CREATE TABLE associate (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tx_cpf VARCHAR(11) NOT NULL UNIQUE,
    tx_name VARCHAR(255) NOT NULL,
    dt_created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ck_active BOOLEAN NOT NULL DEFAULT TRUE
);
--rollback DROP TABLE associate;
