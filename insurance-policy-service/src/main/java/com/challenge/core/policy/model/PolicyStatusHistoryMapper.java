package com.challenge.core.policy.model;

import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.UUID;

public class PolicyStatusHistoryMapper implements RowMapper<PolicyStatusHistory> {
    @Override
    public PolicyStatusHistory map(ResultSet rs, StatementContext ctx) throws SQLException {
        return new PolicyStatusHistory(UUID.fromString(rs.getString("policy_id")), PolicyStatus.valueOf(rs.getString("status")), rs.getObject("timestamp", OffsetDateTime.class));
    }
}
