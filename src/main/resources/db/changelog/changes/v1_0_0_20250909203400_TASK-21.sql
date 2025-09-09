--liquibase formatted sql

--changeset TASK-21:1
ALTER TABLE vote ADD COLUMN dt_vote_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP;
--rollback ALTER TABLE vote DROP COLUMN dt_vote_time;
