package com.challenge.core.policy;

import com.challenge.core.policy.model.*;
import org.jdbi.v3.sqlobject.config.RegisterRowMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.customizer.BindList;
import org.jdbi.v3.sqlobject.statement.SqlBatch;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

public interface PolicyDAO {

    @SqlUpdate("""
                INSERT INTO policy_requests (
                    id, customer_id, product_id, category, sales_channel, payment_method,
                    total_monthly_premium_amount, insured_amount, status, created_at
                )
                VALUES (
                    :id, :customerId, :productId, :category, :salesChannel, :paymentMethod,
                    :totalMonthlyPremiumAmount, :insuredAmount, :status, NOW()
                )
            """)
    void insert(@Bind("id") String id, @Bind("customerId") String customerId, @BindBean PolicyRequestDTO policy, @Bind("status") PolicyStatus status);

    @SqlBatch("""
                INSERT INTO policy_request_coverages (policy_id, coverage_id, coverage_amount)
                SELECT
                    :policyId,
                    c.id,
                    :amount
                FROM coverages c
                WHERE c.name = :coverageName
            """)
    void insertCoverages(
            @Bind("policyId") String policyId,
            @Bind("coverageName") List<String> coverageNames,
            @Bind("amount") List<BigDecimal> amounts
    );

    @SqlUpdate("""
                INSERT INTO policy_history (policy_id, status, timestamp)
                VALUES (:policyId, :status, NOW())
                ON DUPLICATE KEY UPDATE timestamp = NOW()
            """)
    void insertHistory(@Bind("policyId") String policyId, @Bind("status") PolicyStatus status);


    @SqlBatch("""
                INSERT INTO policy_request_assistances (policy_id, assistance_id)
                SELECT
                    :policyId,
                    a.id
                FROM assistances a
                WHERE a.description = :description
            """)
    void insertAssistances(
            @Bind("policyId") String policyId,
            @Bind("description") List<String> descriptions
    );


    @RegisterRowMapper(PolicyDTOMapper.class)
    @SqlQuery("SELECT * FROM policy_requests WHERE id = :id")
    PolicyDTO findById(@Bind("id") String id);


    @RegisterRowMapper(CoverageAmountResultMapper.class)
    @SqlQuery("""
                SELECT prc.policy_id, c.name, prc.coverage_amount
                FROM policy_request_coverages prc
                JOIN coverages c ON c.id = prc.coverage_id
                WHERE prc.policy_id IN (<policyIds>)
            """)
    List<CoverageAmountResult> findCoveragesByPolicyIds(@BindList(value = "policyIds", onEmpty = BindList.EmptyHandling.NULL_STRING) Collection<String> ids);


    @RegisterRowMapper(AssistanceResultMapper.class)
    @SqlQuery("""
                SELECT pra.policy_id, a.description
                FROM policy_request_assistances pra
                JOIN assistances a ON a.id = pra.assistance_id
                WHERE pra.policy_id IN (<policyIds>)
            """)
    List<AssistanceResult> findAssistancesByPolicyIds(@BindList(value = "policyIds", onEmpty = BindList.EmptyHandling.NULL_STRING) Collection<String> ids);


    @RegisterRowMapper(PolicyStatusHistoryMapper.class)
    @SqlQuery("""
                SELECT policy_id, status, timestamp
                FROM policy_history
                WHERE policy_id IN (<policyIds>)
            """)
    List<PolicyStatusHistory> findStatusHistoryByPolicyIds(@BindList(value = "policyIds", onEmpty = BindList.EmptyHandling.NULL_STRING) Collection<String> ids);


    @RegisterRowMapper(PolicyDTOMapper.class)
    @SqlQuery("SELECT * FROM policy_requests WHERE customer_id = :customerId")
    List<PolicyDTO> findByCustomerId(@Bind("customerId") String customerId);


    @SqlUpdate("UPDATE policy_requests SET status = :policyStatus WHERE id = :policyId")
    void update(@Bind("policyId") String policyId, @Bind("policyStatus") String policyStatus);
}
