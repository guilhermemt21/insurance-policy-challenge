package com.challenge.core.policy;

import com.challenge.core.policy.model.InsuranceCategory;
import com.challenge.core.policy.model.PolicyStatus;
import com.challenge.core.policy.model.RiskClassification;

import java.math.BigDecimal;

public class PolicyStatusClassifierService {

    public PolicyStatus classify(RiskClassification classification, InsuranceCategory category, BigDecimal insuredAmount) {
        LimitRule rule = getLimitRule(classification, category);

        if (insuredAmount == null || rule == null || rule.value() == null) {
            return PolicyStatus.REJECTED;
        }

        int comparison = insuredAmount.compareTo(rule.value());
        boolean isApproved = rule.inclusive() ? comparison <= 0 : comparison < 0;

        return isApproved ? PolicyStatus.VALIDATED : PolicyStatus.REJECTED;
    }

    private LimitRule getLimitRule(RiskClassification classification, InsuranceCategory category) {
        return switch (classification) {
            case REGULAR -> getRegularLimit(category);
            case HIGH_RISK -> getHighRiskLimit(category);
            case PREFERRED -> getPreferredLimit(category);
            case NO_INFORMATION -> getNoInformationLimit(category);
        };
    }

    private LimitRule getRegularLimit(InsuranceCategory category) {
        return switch (category) {
            case VIDA, RESIDENCIAL -> new LimitRule(new BigDecimal("500000.00"), true);
            case AUTO -> new LimitRule(new BigDecimal("350000.00"), true);
            default -> new LimitRule(new BigDecimal("255000.00"), true);
        };
    }

    private LimitRule getHighRiskLimit(InsuranceCategory category) {
        return switch (category) {
            case AUTO -> new LimitRule(new BigDecimal("250000.00"), true);
            case RESIDENCIAL -> new LimitRule(new BigDecimal("150000.00"), true);
            default -> new LimitRule(new BigDecimal("125000.00"), true);
        };
    }

    private LimitRule getPreferredLimit(InsuranceCategory category) {
        return switch (category) {
            case VIDA -> new LimitRule(new BigDecimal("800000.00"), false);
            case AUTO, RESIDENCIAL -> new LimitRule(new BigDecimal("450000.00"), false);
            default -> new LimitRule(new BigDecimal("375000.00"), true);
        };
    }

    private LimitRule getNoInformationLimit(InsuranceCategory category) {
        return switch (category) {
            case VIDA, RESIDENCIAL -> new LimitRule(new BigDecimal("200000.00"), true);
            case AUTO -> new LimitRule(new BigDecimal("75000.00"), true);
            default -> new LimitRule(new BigDecimal("55000.00"), true);
        };
    }

    public record LimitRule(BigDecimal value, boolean inclusive) {
    }
}
