package com.challenge.core.policy;

import com.challenge.TestDatabaseExtension;
import com.challenge.core.policy.model.AssistanceResult;
import com.challenge.core.policy.model.CoverageAmountResult;
import com.challenge.core.policy.model.PolicyStatus;
import com.challenge.core.policy.model.PolicyStatusHistory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static com.challenge.core.policy.model.InsuranceCategory.*;
import static com.challenge.core.policy.model.PaymentMethod.DEBIT_CARD;
import static com.challenge.core.policy.model.PaymentMethod.PIX;
import static com.challenge.core.policy.model.SalesChannel.*;
import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(TestDatabaseExtension.class)
public class PolicyDAOTest {

    private PolicyDAO policyDAO;

    @BeforeEach
    void setup(TestDatabaseExtension.DatabaseContext context) {
        this.policyDAO = context.getJdbi().onDemand(PolicyDAO.class);
    }

    @Test
    void testInsertAndFindById() {
        String policyId = UUID.randomUUID().toString();

        PolicyRequestDTO request = PolicyRequestDTO.builder()
                .productId(UUID.fromString("00000000-0000-0000-0000-000000000001"))
                .category(AUTO)
                .salesChannel(WEBSITE)
                .paymentMethod(PIX)
                .totalMonthlyPremiumAmount(new BigDecimal("123.45"))
                .insuredAmount(new BigDecimal("10000.00"))
                .build();

        policyDAO.insert(policyId, "00000000-0000-0000-0000-000000000002", request, PolicyStatus.RECEIVED);

        PolicyDTO saved = policyDAO.findById(policyId);
        assertNotNull(saved);
        assertEquals("00000000-0000-0000-0000-000000000002", saved.getCustomerId().toString());
        assertEquals(request.getProductId(), saved.getProductId().toString());
        assertEquals(request.getCategory(), saved.getCategory());
    }

    @Test
    void testInsertCoveragesAndFind() {
        String policyId = UUID.randomUUID().toString();

        PolicyRequestDTO request = PolicyRequestDTO.builder()
                .productId(UUID.fromString("00000000-0000-0000-0000-000000000002"))
                .category(VIDA)
                .salesChannel(MOBILE)
                .paymentMethod(PIX)
                .totalMonthlyPremiumAmount(BigDecimal.TEN)
                .insuredAmount(new BigDecimal("1000.00"))
                .build();

        policyDAO.insert(policyId, "00000000-0000-0000-0000-000000000005", request, PolicyStatus.PENDING);

        policyDAO.insertCoverages(policyId,
                List.of("Roubo", "Perda Total"),
                List.of(new BigDecimal("100.00"), new BigDecimal("200.00")));

        List<CoverageAmountResult> coverages = policyDAO.findCoveragesByPolicyIds(List.of(policyId));
        assertEquals(2, coverages.size());
        assertTrue(coverages.stream().anyMatch(c -> c.coverageAmount().compareTo(new BigDecimal("100.00")) == 0));
        assertTrue(coverages.stream().anyMatch(c -> c.coverageAmount().compareTo(new BigDecimal("200.00")) == 0));
    }

    @Test
    void testInsertAssistancesAndFind() {
        String policyId = UUID.randomUUID().toString();

        PolicyRequestDTO request = PolicyRequestDTO.builder()
                .productId(UUID.fromString("00000000-0000-0000-0000-000000000003"))
                .category(RESIDENCIAL)
                .salesChannel(WHATSAPP)
                .paymentMethod(DEBIT_CARD)
                .totalMonthlyPremiumAmount(new BigDecimal("50.00"))
                .insuredAmount(new BigDecimal("5000.00"))
                .build();

        policyDAO.insert(policyId, "00000000-0000-0000-0000-000000000005", request, PolicyStatus.RECEIVED);

        policyDAO.insertAssistances(policyId,
                List.of("Guincho até 250km", "Troca de Óleo"));

        List<AssistanceResult> assistances = policyDAO.findAssistancesByPolicyIds(List.of(policyId));
        assertEquals(2, assistances.size());
    }

    @Test
    void testInsertAndFindStatusHistory() {
        String policyId = UUID.randomUUID().toString();

        PolicyRequestDTO request = PolicyRequestDTO.builder()
                .productId(UUID.fromString("00000000-0000-0000-0000-000000000004"))
                .category(VIDA)
                .salesChannel(WEBSITE)
                .paymentMethod(PIX)
                .totalMonthlyPremiumAmount(new BigDecimal("90.00"))
                .insuredAmount(new BigDecimal("8000.00"))
                .build();

        policyDAO.insert(policyId, "00000000-0000-0000-0000-000000000005", request, PolicyStatus.PENDING);

        policyDAO.insertHistory(policyId, PolicyStatus.PENDING);

        List<PolicyStatusHistory> history = policyDAO.findStatusHistoryByPolicyIds(List.of(policyId));

        assertFalse(history.isEmpty());
        assertEquals(PolicyStatus.PENDING, history.get(0).status());
    }
}
