package com.example.intelligent_issue_router.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

@Configuration
public class DynamoDbConfig {

    @Bean
    public DynamoDbClient dynamoDbClient() {
        return DynamoDbClient.builder()
                // Tells AWS: Use the Canada Central region.
                .region(Region.US_EAST_2)
                // Creates the DynamoDB client
                .build();
    }
}