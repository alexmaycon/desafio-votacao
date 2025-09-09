package com.voting.system.api.exception;

public class VoteException extends BusinessException {

    public VoteException(String message) {
        super(message, "VOTE_ERROR");
    }

    public VoteException(String message, Throwable cause) {
        super(message, "VOTE_ERROR", cause);
    }
}
