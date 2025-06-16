package com.challenge.pubsub;

import com.challenge.InsurancePolicyServiceConfiguration;
import com.challenge.core.policy.model.PolicyStatus;
import com.google.inject.Inject;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;

import java.util.UUID;

public class RedisEventPublisher implements EventPublisher {

    private final RedisClient redisClient;
    private final StatefulRedisConnection<String, String> connection;

    @Inject
    public RedisEventPublisher(InsurancePolicyServiceConfiguration configuration) {
        this.redisClient = RedisClient.create(configuration.getRedis().getRedisUri());
        this.connection = redisClient.connect();
    }

    @Override
    public void publishPolicyStatusChanged(UUID policyId, PolicyStatus newStatus) {
        RedisCommands<String, String> sync = connection.sync();
        String channel = "policy-status-changed";
        String message = policyId.toString() + ":" + newStatus.name();
        sync.publish(channel, message);
    }

    public void close() {
        connection.close();
        redisClient.shutdown();
    }
}
