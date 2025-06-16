package com.challenge.pubsub;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;

public class RedisConfiguration {

    @NotEmpty
    @JsonProperty
    private String host;

    @JsonProperty
    private int port = 6379;

    @JsonProperty
    private String password;

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

    @JsonProperty
    public String getPassword() {
        return password;
    }

    @JsonProperty
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Retorna a URI para conexão com o Redis, incluindo autenticação se houver senha.
     * Exemplo: redis://password@host:port ou redis://host:port
     */
    public String getRedisUri() {
        if (password == null || password.isEmpty()) {
            return String.format("redis://%s:%d", host, port);
        } else {
            return String.format("redis://%s@%s:%d", password, host, port);
        }
    }
}
