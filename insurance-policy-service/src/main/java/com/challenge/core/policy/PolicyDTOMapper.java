package com.challenge.core.policy;

import com.challenge.core.policy.model.InsuranceCategory;
import com.challenge.core.policy.model.PaymentMethod;
import com.challenge.core.policy.model.PolicyStatus;
import com.challenge.core.policy.model.SalesChannel;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.ZoneOffset;
import java.util.*;

public class PolicyDTOMapper implements RowMapper<PolicyDTO> {
    @Override
    public PolicyDTO map(ResultSet rs, StatementContext ctx) throws SQLException {
        return PolicyDTO.builder()
                .id(UUID.fromString(rs.getString("id")))
                .customerId(UUID.fromString(rs.getString("customer_id")))
                .productId(UUID.fromString(rs.getString("product_id")))
                .category(InsuranceCategory.valueOf(rs.getString("category")))
                .salesChannel(SalesChannel.valueOf(rs.getString("sales_channel")))
                .paymentMethod(PaymentMethod.valueOf(rs.getString("payment_method")))
                .status(PolicyStatus.valueOf(rs.getString("status")))
                .createdAt(Optional.ofNullable(rs.getTimestamp("created_at"))
                        .map(ts -> ts.toInstant().atOffset(ZoneOffset.UTC)).orElse(null))
                .finishedAt(Optional.ofNullable(rs.getTimestamp("finished_at"))
                        .map(ts -> ts.toInstant().atOffset(ZoneOffset.UTC)).orElse(null))
                .totalMonthlyPremiumAmount(rs.getBigDecimal("total_monthly_premium_amount"))
                .insuredAmount(rs.getBigDecimal("insured_amount"))
                .receivedAuthorizationEvent(rs.getBoolean("received_authorization_event"))
                .receivedApprovedPaymentEvent(rs.getBoolean("received_approved_payment_event"))
                .coverages(new HashMap<>())
                .assistances(new ArrayList<>())
                .history(new ArrayList<>())
                .build();
    }
}
