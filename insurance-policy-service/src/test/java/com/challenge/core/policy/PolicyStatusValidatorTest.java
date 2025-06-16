package com.challenge.core.policy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.challenge.core.policy.model.PolicyStatus.*;
import static org.junit.jupiter.api.Assertions.*;

public class PolicyStatusValidatorTest {

    private PolicyStatusValidator validator;

    @BeforeEach
    void setUp() {
        validator = new PolicyStatusValidator();
    }

    @Test
    void shouldAllowTransitionToValidatedWhenStatusIsReceived() {
        assertDoesNotThrow(() ->
                validator.validateStatusTransition(PolicyDTO.builder().status(RECEIVED).build(), VALIDATED)
        );
    }

    @Test
    void shouldThrowExceptionWhenTransitionFromReceivedToInvalidStatus() {
        Exception ex = assertThrows(IllegalStateException.class, () ->
                validator.validateStatusTransition(PolicyDTO.builder().status(RECEIVED).build(), APPROVED)
        );
        assertTrue(ex.getMessage().contains("Invalid transition"));
    }

    @Test
    void shouldAllowTransitionToPendingWhenStatusIsValidated() {
        assertDoesNotThrow(() ->
                validator.validateStatusTransition(PolicyDTO.builder().status(VALIDATED).build(), PENDING)
        );
    }

    @Test
    void shouldThrowExceptionWhenTransitionFromValidatedToApproved() {
        assertThrows(IllegalStateException.class, () ->
                validator.validateStatusTransition(PolicyDTO.builder().status(VALIDATED).build(), APPROVED)
        );
    }

    @Test
    void shouldThrowExceptionWhenTransitionFromFinalStatus() {
        assertThrows(IllegalStateException.class, () ->
                validator.validateStatusTransition(PolicyDTO.builder().status(REJECTED).build(), APPROVED)
        );
    }

    @Test
    void shouldThrowExceptionWhenCurrentStatusIsNull() {
        assertThrows(IllegalArgumentException.class, () ->
                validator.validateStatusTransition(PolicyDTO.builder().status(null).build(), APPROVED)
        );
    }
}
