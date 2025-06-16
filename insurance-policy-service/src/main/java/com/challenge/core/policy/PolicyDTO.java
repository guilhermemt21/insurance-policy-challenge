package com.challenge.core.policy;

import com.challenge.core.policy.model.*;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
public class PolicyDTO {
    private UUID id;
    private UUID customerId;
    private UUID productId;
    private InsuranceCategory category;
    private SalesChannel salesChannel;
    private PaymentMethod paymentMethod;
    private PolicyStatus status;
    private OffsetDateTime createdAt;
    private OffsetDateTime finishedAt;
    private BigDecimal totalMonthlyPremiumAmount;
    private BigDecimal insuredAmount;
    private Map<String, BigDecimal> coverages;
    private List<String> assistances;
    private List<PolicyStatusHistory> history;

    public void fill(List<CoverageAmountResult> coverageResults, List<AssistanceResult> assistances, List<PolicyStatusHistory> history) {
        for (CoverageAmountResult coverage : coverageResults) {
            getCoverages().put(coverage.name(), coverage.coverageAmount());
        }

        for (AssistanceResult assistance : assistances) {
            getAssistances().add(assistance.description());
        }

        getHistory().addAll(history);
    }
}
