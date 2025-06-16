package com.challenge.api;


import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
public class FraudOccurrence {
    private UUID id;
    private Long productId;
    private String type;
    private String description;
    @JsonDeserialize(using = OffsetDateTimeDeserializer.class)
    private OffsetDateTime createdAt;
    @JsonDeserialize(using = OffsetDateTimeDeserializer.class)
    private OffsetDateTime updatedAt;
}
