package com.challenge.pubsub;

import com.challenge.core.policy.model.PolicyStatus;

import java.util.UUID;

public interface EventPublisher {
    void publishPolicyStatusChanged(UUID policyId, PolicyStatus newStatus);
}