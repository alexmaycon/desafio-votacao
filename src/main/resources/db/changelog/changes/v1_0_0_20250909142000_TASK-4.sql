--liquibase formatted sql

--changeset TASK-4:1
CREATE TABLE voting_session (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_agenda BIGINT NOT NULL,
    dt_start TIMESTAMP NOT NULL,
    dt_end TIMESTAMP NOT NULL,
    vl_duration_minutes INTEGER NOT NULL DEFAULT 1,
    ck_status VARCHAR(20) NOT NULL DEFAULT 'OPEN',
    dt_created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_agenda) REFERENCES agenda(id)
);
--rollback DROP TABLE voting_session;
