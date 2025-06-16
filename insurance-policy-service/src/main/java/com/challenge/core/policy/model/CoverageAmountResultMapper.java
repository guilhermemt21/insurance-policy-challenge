package com.challenge.core.policy.model;

import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class CoverageAmountResultMapper implements RowMapper<CoverageAmountResult> {
    @Override
    public CoverageAmountResult map(ResultSet rs, StatementContext ctx) throws SQLException {
        return new CoverageAmountResult(
                UUID.fromString(rs.getString("policy_id")),
                rs.getString("name"),
                rs.getBigDecimal("coverage_amount")
        );
    }
}
