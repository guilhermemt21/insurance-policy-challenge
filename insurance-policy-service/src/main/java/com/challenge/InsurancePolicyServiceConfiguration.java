package com.challenge;

import com.challenge.api.FraudApiConfiguration;
import com.challenge.pubsub.RedisConfiguration;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.core.Configuration;
import io.dropwizard.db.DataSourceFactory;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public class InsurancePolicyServiceConfiguration extends Configuration {

    @Valid
    @NotNull
    @JsonProperty("insurancePolicyDatabase")
    private DataSourceFactory insurancePolicyDatabase = new DataSourceFactory();

    @Valid
    @NotNull
    @JsonProperty("redis")
    private RedisConfiguration redis = new RedisConfiguration();

    @Valid
    @NotNull
    @JsonProperty("fraudApi")
    private FraudApiConfiguration fraudApi = new FraudApiConfiguration();

    public DataSourceFactory getDataSourceFactory() {
        return insurancePolicyDatabase;
    }

    public void setDataSourceFactory(DataSourceFactory database) {
        this.insurancePolicyDatabase = database;
    }

    public RedisConfiguration getRedis() {
        return redis;
    }

    public void setRedis(RedisConfiguration redis) {
        this.redis = redis;
    }

    public FraudApiConfiguration getFraudApi() {
        return fraudApi;
    }

    public void setFraudApi(FraudApiConfiguration fraudApi) {
        this.fraudApi = fraudApi;
    }
}
