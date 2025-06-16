package com.challenge.core.policy.model;

import java.math.BigDecimal;
import java.util.UUID;

public record CoverageAmountResult(UUID policyId, String name, BigDecimal coverageAmount) {
}
