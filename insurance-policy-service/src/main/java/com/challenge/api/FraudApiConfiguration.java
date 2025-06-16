package com.challenge.api;


import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

public class FraudApiConfiguration {

    @NotNull
    private String host;

    private int port = 3000;

    @JsonProperty
    public String getHost() {
        return host;
    }

    @JsonProperty
    public void setHost(String host) {
        this.host = host;
    }

    @JsonProperty
    public int getPort() {
        return port;
    }

    @JsonProperty
    public void setPort(int port) {
        this.port = port;
    }

    public String getBaseUrl() {
        return String.format("http://%s:%d", host, port);
    }
}
