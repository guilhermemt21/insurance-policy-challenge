package com.challenge.core.policy;

import com.challenge.api.FraudAnalysisClient;
import com.challenge.api.FraudAnalysisResponse;
import com.challenge.core.policy.model.PolicyStatus;
import com.challenge.core.policy.model.RiskClassification;
import com.challenge.pubsub.RedisEventPublisher;
import com.challenge.security.AuthenticatedUser;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.HandleConsumer;
import org.jdbi.v3.core.Jdbi;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PolicyServiceTest {

    private Jdbi jdbi;
    private FraudAnalysisClient fraudClient;
    private PolicyStatusValidator policyStatusValidator;
    private PolicyStatusClassifierService policyStatusClassifierService;
    private RedisEventPublisher eventPublisher;

    private PolicyService service;

    @BeforeEach
    void setup() {
        jdbi = mock(Jdbi.class);
        fraudClient = mock(FraudAnalysisClient.class);
        policyStatusValidator = mock(PolicyStatusValidator.class);
        policyStatusClassifierService = mock(PolicyStatusClassifierService.class);
        eventPublisher = mock(RedisEventPublisher.class);

        service = spy(new PolicyService(jdbi, fraudClient, policyStatusValidator, policyStatusClassifierService, eventPublisher));
    }

    @Test
    void shouldCreatePolicyWhenValidRequest() {
        FraudAnalysisResponse fraudAnalysisResponse = mock(FraudAnalysisResponse.class);
        when(fraudAnalysisResponse.getClassification()).thenReturn(RiskClassification.REGULAR);
        when(fraudClient.getFraudAnalysis(any())).thenReturn(fraudAnalysisResponse);

        AuthenticatedUser user = AuthenticatedUser.builder().id(UUID.randomUUID()).build();
        PolicyRequestDTO dto = PolicyRequestDTO.builder()
                .assistances(List.of())
                .coverages(Map.of())
                .build();

        PolicyDAO policyDAO = mock(PolicyDAO.class);
        PolicyDTO expectedPolicy = PolicyDTO.builder()
                .id(UUID.randomUUID())
                .history(new ArrayList<>())
                .build();

        when(jdbi.inTransaction(any()))
                .thenAnswer(invocation -> {
                    org.jdbi.v3.core.HandleCallback<?, ?> callback = invocation.getArgument(0);
                    Handle handle = mock(Handle.class);
                    when(handle.attach(PolicyDAO.class)).thenReturn(policyDAO);
                    return callback.withHandle(handle);
                });


        doNothing().when(policyDAO).insert(anyString(), anyString(), any(), any());
        doNothing().when(policyDAO).insertAssistances(anyString(), anyList());
        doNothing().when(policyDAO).insertCoverages(anyString(), anyList(), anyList());
        doNothing().when(policyDAO).insertHistory(anyString(), any());

        when(policyDAO.findById(anyString())).thenReturn(expectedPolicy);
        when(policyDAO.findCoveragesByPolicyIds(anyList())).thenReturn(List.of());
        when(policyDAO.findAssistancesByPolicyIds(anyList())).thenReturn(List.of());
        when(policyDAO.findStatusHistoryByPolicyIds(anyList())).thenReturn(List.of());

        PolicyDTO result = service.createPolicyRequest(user, dto);

        assertNotNull(result);
        assertEquals(expectedPolicy, result);

        verify(policyDAO).insert(anyString(), eq(user.getId().toString()), eq(dto), eq(PolicyStatus.RECEIVED));
        verify(policyDAO).insertAssistances(anyString(), eq(dto.getAssistances()));
        verify(policyDAO).insertCoverages(anyString(), eq(new ArrayList<>(dto.getCoverages().keySet())), eq(new ArrayList<>(dto.getCoverages().values())));
        verify(policyDAO).insertHistory(anyString(), eq(PolicyStatus.RECEIVED));
    }

    @Test
    void shouldCancelPolicyWhenStatusAllows() {
        UUID policyId = UUID.randomUUID();

        PolicyDTO existingPolicy = PolicyDTO.builder().id(policyId).build();

        doReturn(existingPolicy).when(service).getPolicyRequestById(policyId);

        doNothing().when(policyStatusValidator).validateStatusTransition(existingPolicy, PolicyStatus.CANCELLED);

        doAnswer(invocation -> {
            HandleConsumer tx = invocation.getArgument(0);
            Handle handle = mock(Handle.class);
            PolicyDAO dao = mock(PolicyDAO.class);
            when(handle.attach(PolicyDAO.class)).thenReturn(dao);

            tx.useHandle(handle);

            verify(dao).update(policyId.toString(), PolicyStatus.CANCELLED.toString());
            verify(dao).insertHistory(policyId.toString(), PolicyStatus.CANCELLED);

            return null;
        }).when(jdbi).useTransaction(any(HandleConsumer.class));


        doNothing().when(service).publishChange(PolicyStatus.CANCELLED, policyId);

        service.cancelPolicyRequest(policyId);

        verify(policyStatusValidator).validateStatusTransition(existingPolicy, PolicyStatus.CANCELLED);
        verify(service).publishChange(PolicyStatus.CANCELLED, policyId);
    }


}
