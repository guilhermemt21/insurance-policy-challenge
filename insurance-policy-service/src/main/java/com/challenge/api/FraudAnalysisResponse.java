package com.challenge.api;


import com.challenge.core.policy.model.RiskClassification;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class FraudAnalysisResponse {
    private UUID orderId;
    private UUID customerId;
    @JsonDeserialize(using = OffsetDateTimeDeserializer.class)
    private OffsetDateTime analyzedAt;
    private RiskClassification classification;
    private List<FraudOccurrence> occurrences;

}
