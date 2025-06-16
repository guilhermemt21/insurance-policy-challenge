package com.challenge.core.policy;


import com.challenge.api.FraudAnalysisClient;
import com.challenge.api.FraudAnalysisResponse;
import com.challenge.core.policy.model.AssistanceResult;
import com.challenge.core.policy.model.CoverageAmountResult;
import com.challenge.core.policy.model.PolicyStatus;
import com.challenge.core.policy.model.PolicyStatusHistory;
import com.challenge.pubsub.RedisEventPublisher;
import com.challenge.security.AuthenticatedUser;
import com.google.inject.Inject;
import lombok.AllArgsConstructor;
import org.jdbi.v3.core.Jdbi;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.challenge.core.policy.model.PolicyStatus.*;


@AllArgsConstructor(onConstructor_ = {@Inject})
public class PolicyService {

    private final Jdbi jdbi;
    private final FraudAnalysisClient fraudClient;
    private final PolicyStatusValidator policyStatusValidator;
    private final PolicyStatusClassifierService policyStatusClassifierService;
    private final RedisEventPublisher eventPublisher;


    public PolicyDTO createPolicyRequest(AuthenticatedUser user, PolicyRequestDTO dto) {


        UUID generatedPolicyId = jdbi.inTransaction(handle -> {
            PolicyDAO policyDAO = handle.attach(PolicyDAO.class);
            UUID policyId = UUID.randomUUID();
            PolicyStatus status = RECEIVED;
            policyDAO.insert(policyId.toString(), user.getId().toString(), dto, status);
            policyDAO.insertAssistances(policyId.toString(), dto.getAssistances());
            policyDAO.insertCoverages(policyId.toString(), new ArrayList<>(dto.getCoverages().keySet()), new ArrayList<>(dto.getCoverages().values()));
            policyDAO.insertHistory(policyId.toString(), status);

            publishChange(status, policyId);

            return policyId;
        });

        validatePolicyStatusFromFraudAnalysis(user, dto, generatedPolicyId);

        return getPolicyRequestById(generatedPolicyId);
    }

    private void validatePolicyStatusFromFraudAnalysis(AuthenticatedUser user, PolicyRequestDTO dto, UUID generatedPolicyId) {
        FraudAnalysisResponse fraudAnalysis = fraudClient.getFraudAnalysis(user.getId());

        PolicyStatus policyStatusFromFraudAnalysis = policyStatusClassifierService.classify(fraudAnalysis.getClassification(), dto.getCategory(), dto.getInsuredAmount());
        changePolicyStatusIfAllowedAndPublish(generatedPolicyId, policyStatusFromFraudAnalysis);
    }

    private PolicyDTO findPolicyById(PolicyDAO policyDAO, UUID policyId) {
        PolicyDTO policy = policyDAO.findById(policyId.toString());

        List<CoverageAmountResult> coverageResults = policyDAO.findCoveragesByPolicyIds(List.of(policyId.toString()));
        List<AssistanceResult> assistances = policyDAO.findAssistancesByPolicyIds(List.of(policyId.toString()));
        List<PolicyStatusHistory> history = policyDAO.findStatusHistoryByPolicyIds(List.of(policyId.toString()));

        policy.fill(coverageResults, assistances, history);

        return policy;
    }

    private List<PolicyDTO> findPoliciesByCustomerId(PolicyDAO policyDAO, UUID customerId) {
        List<PolicyDTO> policies = policyDAO.findByCustomerId(customerId.toString());

        if (policies.isEmpty()) {
            return policies;
        }

        List<String> policyIds = policies.stream().map(p -> p.getId().toString()).collect(Collectors.toList());

        List<CoverageAmountResult> coverageResults = policyDAO.findCoveragesByPolicyIds(policyIds);
        List<AssistanceResult> assistanceResults = policyDAO.findAssistancesByPolicyIds(policyIds);
        List<PolicyStatusHistory> historyResults = policyDAO.findStatusHistoryByPolicyIds(policyIds);

        Map<UUID, List<CoverageAmountResult>> coveragesByPolicy = coverageResults.stream().collect(Collectors.groupingBy(CoverageAmountResult::policyId));
        Map<UUID, List<AssistanceResult>> assistancesByPolicy = assistanceResults.stream().collect(Collectors.groupingBy(AssistanceResult::policyId));
        Map<UUID, List<PolicyStatusHistory>> historyByPolicy = historyResults.stream().collect(Collectors.groupingBy(PolicyStatusHistory::policyId));

        for (PolicyDTO policy : policies) {
            policy.fill(
                    coveragesByPolicy.getOrDefault(policy.getId(), List.of()),
                    assistancesByPolicy.getOrDefault(policy.getId(), List.of()),
                    historyByPolicy.getOrDefault(policy.getId(), List.of())
            );
        }

        return policies;
    }


    public PolicyDTO getPolicyRequestById(UUID id) {
        return jdbi.inTransaction(handle -> findPolicyById(handle.attach(PolicyDAO.class), id));
    }

    public List<PolicyDTO> getByCustomerId(AuthenticatedUser user) {
        return jdbi.inTransaction(handle -> {
            return findPoliciesByCustomerId(handle.attach(PolicyDAO.class), user.getId());
        });
    }

    public void cancelPolicyRequest(UUID policyId) {
        changePolicyStatusIfAllowedAndPublish(policyId, CANCELLED);
    }

    public void processApprovedPaymentEvent(UUID policyId) {
        changePolicyStatusIfAllowedAndPublish(policyId, APPROVED);
    }

    public void processDeniedPaymentEvent(UUID policyId) {
        changePolicyStatusIfAllowedAndPublish(policyId, REJECTED);
    }

    public void processAuthorizationEvent(UUID policyId) {
        changePolicyStatusIfAllowedAndPublish(policyId, PENDING);
    }

    private void changePolicyStatusIfAllowedAndPublish(UUID policyId, PolicyStatus newStatus) {
        PolicyDTO policy = getPolicyRequestById(policyId);

        policyStatusValidator.validateStatusTransition(policy, newStatus);

        jdbi.useTransaction(handle -> {
            PolicyDAO policyDAO = handle.attach(PolicyDAO.class);
            policyDAO.update(policyId.toString(), newStatus.toString());
            policyDAO.insertHistory(policyId.toString(), newStatus);

            publishChange(newStatus, policyId);
        });
    }

    protected void publishChange(PolicyStatus status, UUID policyId) {
        eventPublisher.publishPolicyStatusChanged(policyId, status);
    }

}
