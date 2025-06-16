package com.challenge.core.policy;


import com.challenge.core.policy.model.InsuranceCategory;
import com.challenge.core.policy.model.PaymentMethod;
import com.challenge.core.policy.model.SalesChannel;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
public class PolicyRequestDTO {
    private UUID productId;
    private InsuranceCategory category;
    private SalesChannel salesChannel;
    private PaymentMethod paymentMethod;
    private BigDecimal totalMonthlyPremiumAmount;
    private BigDecimal insuredAmount;
    private Map<String, BigDecimal> coverages;
    private List<String> assistances;

    public String getProductId(){
        return productId.toString();
    }
}