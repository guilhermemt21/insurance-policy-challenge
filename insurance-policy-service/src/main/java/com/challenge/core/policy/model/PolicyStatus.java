package com.challenge.core.policy.model;

public enum PolicyStatus {
    RECEIVED(false),
    VALIDATED(false),
    PENDING(false),
    APPROVED(true),
    REJECTED(true),
    CANCELLED(true);

    private final boolean isFinal;

    PolicyStatus(boolean isFinal) {
        this.isFinal = isFinal;
    }

    public boolean isFinalState() {
        return this.isFinal;
    }
}
