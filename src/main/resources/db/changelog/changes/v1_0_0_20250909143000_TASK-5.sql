--liquibase formatted sql

--changeset TASK-5:1
CREATE TABLE vote (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_voting_session BIGINT NOT NULL,
    id_associate BIGINT NOT NULL,
    ck_vote_value VARCHAR(10) NOT NULL,
    dt_created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_voting_session) REFERENCES voting_session(id),
    FOREIGN KEY (id_associate) REFERENCES associate(id),
    CONSTRAINT unique_vote_per_session UNIQUE (id_voting_session, id_associate)
);
--rollback DROP TABLE vote;
