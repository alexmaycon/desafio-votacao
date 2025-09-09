package com.voting.system.api.exception;

public class VotingSessionException extends BusinessException {

    public VotingSessionException(String message) {
        super(message, "VOTING_SESSION_ERROR");
    }

    public VotingSessionException(String message, Throwable cause) {
        super(message, "VOTING_SESSION_ERROR", cause);
    }
}
