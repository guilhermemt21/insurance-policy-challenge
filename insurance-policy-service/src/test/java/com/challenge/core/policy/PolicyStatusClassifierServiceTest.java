package com.challenge.core.policy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static com.challenge.core.policy.model.InsuranceCategory.*;
import static com.challenge.core.policy.model.PolicyStatus.VALIDATED;
import static com.challenge.core.policy.model.PolicyStatus.REJECTED;
import static com.challenge.core.policy.model.RiskClassification.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class PolicyStatusClassifierServiceTest {

    private PolicyStatusClassifierService classifier;

    @BeforeEach
    void setUp() {
        classifier = new PolicyStatusClassifierService();
    }

    // === REGULAR ===

    @Test
    void shouldReturnValidatedWhenRegularAndVidaAndAmountEqualsLimit() {
        assertEquals(VALIDATED, classifier.classify(REGULAR, VIDA, new BigDecimal("500000.00")));
    }

    @Test
    void shouldReturnRejectedWhenRegularAndVidaAndAmountAboveLimit() {
        assertEquals(REJECTED, classifier.classify(REGULAR, VIDA, new BigDecimal("500000.01")));
    }

    @Test
    void shouldReturnValidatedWhenRegularAndAutoAndAmountEqualsLimit() {
        assertEquals(VALIDATED, classifier.classify(REGULAR, AUTO, new BigDecimal("350000.00")));
    }

    @Test
    void shouldReturnRejectedWhenRegularAndAutoAndAmountAboveLimit() {
        assertEquals(REJECTED, classifier.classify(REGULAR, AUTO, new BigDecimal("350000.01")));
    }

    @Test
    void shouldReturnValidatedWhenRegularAndEmpresarialAndAmountEqualsLimit() {
        assertEquals(VALIDATED, classifier.classify(REGULAR, EMPRESARIAL, new BigDecimal("255000.00")));
    }

    @Test
    void shouldReturnRejectedWhenRegularAndEmpresarialAndAmountAboveLimit() {
        assertEquals(REJECTED, classifier.classify(REGULAR, EMPRESARIAL, new BigDecimal("255000.01")));
    }

    // === HIGH_RISK ===

    @Test
    void shouldReturnValidatedWhenHighRiskAndAutoAndAmountEqualsLimit() {
        assertEquals(VALIDATED, classifier.classify(HIGH_RISK, AUTO, new BigDecimal("250000.00")));
    }

    @Test
    void shouldReturnRejectedWhenHighRiskAndAutoAndAmountAboveLimit() {
        assertEquals(REJECTED, classifier.classify(HIGH_RISK, AUTO, new BigDecimal("250000.01")));
    }

    @Test
    void shouldReturnValidatedWhenHighRiskAndResidencialAndAmountEqualsLimit() {
        assertEquals(VALIDATED, classifier.classify(HIGH_RISK, RESIDENCIAL, new BigDecimal("150000.00")));
    }

    @Test
    void shouldReturnRejectedWhenHighRiskAndResidencialAndAmountAboveLimit() {
        assertEquals(REJECTED, classifier.classify(HIGH_RISK, RESIDENCIAL, new BigDecimal("150000.01")));
    }

    @Test
    void shouldReturnValidatedWhenHighRiskAndEmpresarialAndAmountEqualsLimit() {
        assertEquals(VALIDATED, classifier.classify(HIGH_RISK, EMPRESARIAL, new BigDecimal("125000.00")));
    }

    @Test
    void shouldReturnRejectedWhenHighRiskAndEmpresarialAndAmountAboveLimit() {
        assertEquals(REJECTED, classifier.classify(HIGH_RISK, EMPRESARIAL, new BigDecimal("125000.01")));
    }

    // === PREFERRED ===

    @Test
    void shouldReturnValidatedWhenPreferredAndVidaAndAmountBelowLimit() {
        assertEquals(VALIDATED, classifier.classify(PREFERRED, VIDA, new BigDecimal("799999.99")));
    }

    @Test
    void shouldReturnRejectedWhenPreferredAndVidaAndAmountEqualsLimit() {
        assertEquals(REJECTED, classifier.classify(PREFERRED, VIDA, new BigDecimal("800000.00")));
    }

    @Test
    void shouldReturnValidatedWhenPreferredAndAutoAndAmountBelowLimit() {
        assertEquals(VALIDATED, classifier.classify(PREFERRED, AUTO, new BigDecimal("449999.99")));
    }

    @Test
    void shouldReturnRejectedWhenPreferredAndAutoAndAmountEqualsLimit() {
        assertEquals(REJECTED, classifier.classify(PREFERRED, AUTO, new BigDecimal("450000.00")));
    }

    @Test
    void shouldReturnValidatedWhenPreferredAndEmpresarialAndAmountEqualsLimit() {
        assertEquals(VALIDATED, classifier.classify(PREFERRED, EMPRESARIAL, new BigDecimal("375000.00")));
    }

    @Test
    void shouldReturnRejectedWhenPreferredAndEmpresarialAndAmountAboveLimit() {
        assertEquals(REJECTED, classifier.classify(PREFERRED, EMPRESARIAL, new BigDecimal("375000.01")));
    }

    // === NO_INFORMATION ===

    @Test
    void shouldReturnValidatedWhenNoInfoAndVidaAndAmountEqualsLimit() {
        assertEquals(VALIDATED, classifier.classify(NO_INFORMATION, VIDA, new BigDecimal("200000.00")));
    }

    @Test
    void shouldReturnRejectedWhenNoInfoAndVidaAndAmountAboveLimit() {
        assertEquals(REJECTED, classifier.classify(NO_INFORMATION, VIDA, new BigDecimal("200000.01")));
    }

    @Test
    void shouldReturnValidatedWhenNoInfoAndAutoAndAmountEqualsLimit() {
        assertEquals(VALIDATED, classifier.classify(NO_INFORMATION, AUTO, new BigDecimal("75000.00")));
    }

    @Test
    void shouldReturnRejectedWhenNoInfoAndAutoAndAmountAboveLimit() {
        assertEquals(REJECTED, classifier.classify(NO_INFORMATION, AUTO, new BigDecimal("75000.01")));
    }

    @Test
    void shouldReturnValidatedWhenNoInfoAndEmpresarialAndAmountEqualsLimit() {
        assertEquals(VALIDATED, classifier.classify(NO_INFORMATION, EMPRESARIAL, new BigDecimal("55000.00")));
    }

    @Test
    void shouldReturnRejectedWhenNoInfoAndEmpresarialAndAmountAboveLimit() {
        assertEquals(REJECTED, classifier.classify(NO_INFORMATION, EMPRESARIAL, new BigDecimal("55000.01")));
    }

    // === NULL CHECKS ===

    @Test
    void shouldReturnRejectedWhenInsuredAmountIsNull() {
        assertEquals(REJECTED, classifier.classify(REGULAR, VIDA, null));
    }

}
