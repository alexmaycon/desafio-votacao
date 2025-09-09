--liquibase formatted sql

--changeset TASK-20:1
ALTER TABLE voting_session ALTER COLUMN dt_start SET NULL;
--rollback ALTER TABLE voting_session ALTER COLUMN dt_start SET NOT NULL;

--changeset TASK-20:2
ALTER TABLE voting_session ALTER COLUMN dt_end SET NULL;
--rollback ALTER TABLE voting_session ALTER COLUMN dt_end SET NOT NULL;
