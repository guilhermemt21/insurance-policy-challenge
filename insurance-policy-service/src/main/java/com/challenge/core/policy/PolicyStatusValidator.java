package com.challenge.core.policy;


import com.challenge.core.policy.model.PolicyStatus;


public class PolicyStatusValidator {

    public void validateStatusTransition(PolicyDTO policy, PolicyStatus desiredStatus) {
        PolicyStatus currentStatus = policy.getStatus();

        if (currentStatus == null) {
            throw new IllegalArgumentException("Current policy status is undefined");
        }

        if (currentStatus.isFinalState()) {
            throw new IllegalStateException("Cannot change status from a finalized state: " + currentStatus);
        }

        switch (currentStatus) {
            case RECEIVED -> {
                if (desiredStatus != PolicyStatus.VALIDATED && desiredStatus != PolicyStatus.CANCELLED && desiredStatus != PolicyStatus.REJECTED) {
                    throw new IllegalStateException("Invalid transition from RECEIVED to " + desiredStatus);
                }
            }
            case VALIDATED -> {
                if (desiredStatus != PolicyStatus.PENDING && desiredStatus != PolicyStatus.CANCELLED) {
                    throw new IllegalStateException("Invalid transition from VALIDATED to " + desiredStatus);
                }
            }
            case PENDING -> {
                if (!(desiredStatus == PolicyStatus.APPROVED ||
                        desiredStatus == PolicyStatus.REJECTED ||
                        desiredStatus == PolicyStatus.CANCELLED ||
                        desiredStatus == PolicyStatus.PENDING)) {
                    throw new IllegalStateException("Invalid transition from PENDING to " + desiredStatus);
                }
            }
            default -> throw new IllegalStateException("Unhandled current status: " + currentStatus);
        }
    }
}
