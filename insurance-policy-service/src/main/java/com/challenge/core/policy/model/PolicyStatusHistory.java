package com.challenge.core.policy.model;

import java.time.OffsetDateTime;
import java.util.UUID;

public record PolicyStatusHistory(UUID policyId, PolicyStatus status, OffsetDateTime timestamp) {
}
