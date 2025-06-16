package com.challenge.api;

import com.challenge.InsurancePolicyServiceConfiguration;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.inject.Inject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

public class FraudAnalysisClient {

    private final String baseUrl;
    private final ObjectMapper objectMapper;

    @Inject
    public FraudAnalysisClient(InsurancePolicyServiceConfiguration configuration) {
        FraudApiConfiguration config = configuration.getFraudApi();
        this.baseUrl = "http://" + config.getHost() + ":" + config.getPort();
        this.objectMapper = new ObjectMapper();
        this.objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        this.objectMapper.disable(MapperFeature.REQUIRE_HANDLERS_FOR_JAVA8_TIMES);
    }

    public FraudAnalysisResponse getFraudAnalysis(UUID clientId) {
        try {
            URL url = new URL(baseUrl + "/api/fraud_check/" + clientId);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            if (conn.getResponseCode() == 200) {
                return objectMapper.readValue(conn.getInputStream(), FraudAnalysisResponse.class);
            } else {
                throw new RuntimeException("Failed request with status code: " + conn.getResponseCode());
            }
        } catch (IOException e) {
            throw new RuntimeException("Error while calling fraud analysis API", e);
        }
    }
}
