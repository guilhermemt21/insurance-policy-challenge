package com.challenge.core.policy.model;


import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class AssistanceResultMapper implements RowMapper<AssistanceResult> {
    @Override
    public AssistanceResult map(ResultSet rs, StatementContext ctx) throws SQLException {
        return new AssistanceResult(
                UUID.fromString(rs.getString("policy_id")),
                rs.getString("description")
        );
    }
}
