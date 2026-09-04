package com.example.intelligent_issue_router.service;

import com.example.intelligent_issue_router.model.Issue;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.ScanRequest;
import software.amazon.awssdk.services.dynamodb.model.ScanResponse;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.UpdateItemRequest;
import software.amazon.awssdk.services.dynamodb.model.DeleteItemRequest;

import com.example.intelligent_issue_router.exception.IssueNotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

// Tells Spring: This class contains application/business logic. Spring will create
// IssueService object.
@Service
public class IssueService
{
    private static final String TABLE_NAME = "issues";
    private final DynamoDbClient dynamoDbClient;
    private final EmbeddingService embeddingService;
    private final VectorStore vectorStore;

    public IssueService(
            DynamoDbClient dynamoDbClient,
            EmbeddingService embeddingService,
            VectorStore vectorStore) {
        this.dynamoDbClient = dynamoDbClient;
        this.embeddingService = embeddingService;
        this.vectorStore = vectorStore;
    }

    private long nextId = 1;

    public Issue createIssue(Issue issue)
    {
        issue.setId(nextId++);

        // Building a map representing one DynamoDB row/item.
        Map<String, AttributeValue> item = new HashMap<>();

        // Put the issue's ID into the DynamoDB item as a Number.
        item.put("id", AttributeValue.builder()
                .n(String.valueOf(issue.getId()))
                .build());

        item.put("title", AttributeValue.builder()
                .s(issue.getTitle())
                .build());

        item.put("description", AttributeValue.builder()
                .s(issue.getDescription())
                .build());

        item.put("status", AttributeValue.builder()
                .s(issue.getStatus())
                .build());

        if (issue.getResolution() != null) {
            item.put("resolution", AttributeValue.builder()
                    .s(issue.getResolution())
                    .build());
        }

        // Tells AWS: to put this item into the DynamoDB table called issues.
        PutItemRequest request = PutItemRequest.builder()
                .tableName(TABLE_NAME)
                .item(item)
                .build();

        dynamoDbClient.putItem(request);

        dynamoDbClient.putItem(request);

        // Create an embedding from the issue's title and description.
        String text = issue.getTitle() + "\n" + issue.getDescription();

        List<Float> embedding = embeddingService.createEmbedding(text);

        // Store the issue and its embedding for semantic retrieval.
        vectorStore.addIssue(issue, embedding);

        return issue;
    }

    public List<Issue> getAllIssues()
    {
        ScanRequest request = ScanRequest.builder()
                // Creates a request to scan/read items from DynamoDB.
                .tableName(TABLE_NAME)
                .build();

        // Sends the scan request to DynamoDB and stores AWS's response.
        ScanResponse response = dynamoDbClient.scan(request);

        // Creates an empty Java list where we will store the issues retrieved.
        List<Issue> result = new ArrayList<>();

        // Goes through each item (row) returned by DynamoDB.
        for (Map<String, AttributeValue> item: response.items())
        {
            // Creates a new Java Issue object for the current DynamoDB item.
            Issue issue = new Issue();
            issue.setId(Long.valueOf(item.get("id").n()));
            issue.setTitle(item.get("title").s());
            issue.setDescription(item.get("description").s());
            issue.setStatus(item.get("status").s());

            if (item.containsKey("resolution")) {
                issue.setResolution(item.get("resolution").s());
            }

            // Read the AI analysis fields from DynamoDB if they exist.
            if (item.containsKey("category")) {
                issue.setCategory(item.get("category").s());
            }

            if (item.containsKey("priority")) {
                issue.setPriority(item.get("priority").s());
            }

            if (item.containsKey("recommendedTeam")) {
                issue.setRecommendedTeam(item.get("recommendedTeam").s());
            }

            if (item.containsKey("aiSummary")) {
                issue.setAiSummary(item.get("aiSummary").s());
            }

            result.add(issue);
        }
        return result;
    }

    public Issue getIssueById(Long id) {

        Map<String, AttributeValue> key = new HashMap<>();

        key.put("id", AttributeValue.builder()
                .n(String.valueOf(id))
                .build());

        GetItemRequest request = GetItemRequest.builder()
                .tableName(TABLE_NAME)
                .key(key)
                .build();

        Map<String, AttributeValue> item =
                dynamoDbClient.getItem(request).item();

        if (item.isEmpty()) {
            throw new IssueNotFoundException(id);
        }

        Issue issue = new Issue();

        issue.setId(Long.valueOf(item.get("id").n()));
        issue.setTitle(item.get("title").s());
        issue.setDescription(item.get("description").s());
        issue.setStatus(item.get("status").s());

        if (item.containsKey("resolution")) {
            issue.setResolution(item.get("resolution").s());
        }

        // Read the AI analysis fields from DynamoDB if they exist.
        if (item.containsKey("category")) {
            issue.setCategory(item.get("category").s());
        }

        if (item.containsKey("priority")) {
            issue.setPriority(item.get("priority").s());
        }

        if (item.containsKey("recommendedTeam")) {
            issue.setRecommendedTeam(item.get("recommendedTeam").s());
        }

        if (item.containsKey("aiSummary")) {
            issue.setAiSummary(item.get("aiSummary").s());
        }

        return issue;
    }

    public Issue resolveIssue(Long id, String resolution) {

        Map<String, AttributeValue> key = new HashMap<>();

        key.put("id", AttributeValue.builder()
                .n(String.valueOf(id))
                .build());

        Map<String, AttributeValue> values = new HashMap<>();

        values.put(":resolution", AttributeValue.builder()
                .s(resolution)
                .build());

        values.put(":status", AttributeValue.builder()
                .s("RESOLVED")
                .build());

        UpdateItemRequest request = UpdateItemRequest.builder()
                .tableName(TABLE_NAME)
                .key(key)
                .updateExpression("SET resolution = :resolution, #status = :status")
                .expressionAttributeNames(
                        // Creates a mapping: DynamoDB has certain words can cause problems when
                        // used directly in expressions.
                        Map.of("#status", "status")
                )
                .expressionAttributeValues(values)
                .build();

        dynamoDbClient.updateItem(request);

        return getIssueById(id);
    }

    public void deleteIssue(Long id) {

        Map<String, AttributeValue> key = new HashMap<>();

        key.put("id", AttributeValue.builder()
                .n(String.valueOf(id))
                .build());

        DeleteItemRequest request = DeleteItemRequest.builder()
                .tableName(TABLE_NAME)
                .key(key)
                .build();

        dynamoDbClient.deleteItem(request);
    }

    public Issue updateAiAnalysis(Long id, String category, String priority,
                                  String recommendedTeam, String summary) {

        // Create the key used to find the issue in DynamoDB.
        Map<String, AttributeValue> key = new HashMap<>();

        key.put("id", AttributeValue.builder()
                .n(String.valueOf(id))
                .build());

        // Create the values that we want to save.
        Map<String, AttributeValue> values = new HashMap<>();

        values.put(":category", AttributeValue.builder()
                .s(category)
                .build());

        values.put(":priority", AttributeValue.builder()
                .s(priority)
                .build());

        values.put(":team", AttributeValue.builder()
                .s(recommendedTeam)
                .build());

        values.put(":summary", AttributeValue.builder()
                .s(summary)
                .build());

        // Tell DynamoDB which fields to update.
        UpdateItemRequest request = UpdateItemRequest.builder()
                .tableName(TABLE_NAME)
                .key(key)
                .updateExpression(
                        "SET category = :category, " +
                                "priority = :priority, " +
                                "recommendedTeam = :team, " +
                                "aiSummary = :summary"
                )
                .expressionAttributeValues(values)
                .build();

        // Send the update to DynamoDB.
        dynamoDbClient.updateItem(request);

        // Return the updated issue.
        return getIssueById(id);
    }
}
