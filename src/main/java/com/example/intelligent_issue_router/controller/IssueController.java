package com.example.intelligent_issue_router.controller;
import com.example.intelligent_issue_router.model.Issue;
import com.example.intelligent_issue_router.service.IssueService;
import com.example.intelligent_issue_router.dto.ResolveIssueRequest;
import com.example.intelligent_issue_router.service.AiService;
import com.example.intelligent_issue_router.dto.AiAnalysis;
import com.example.intelligent_issue_router.dto.AIQueryRequest;
import org.springframework.web.multipart.MultipartFile;
import com.example.intelligent_issue_router.service.S3Service;

import com.example.intelligent_issue_router.service.EmbeddingService;
import com.example.intelligent_issue_router.service.SimilarityService;
import com.example.intelligent_issue_router.service.VectorStore;

import org.springframework.web.bind.annotation.*;

import java.util.List;
// Tells Spring: This class handles HTTP/API requests
@RestController
// Sets base URL for this controller
@RequestMapping("/issues")

public class IssueController {

    // IssueService handles creating, updating, finding, and resolving issues.
    private final IssueService issueService;
    // AiService handles communication with the OpenAI API.
    private final AiService aiService;
    private final S3Service s3Service;

    private final EmbeddingService embeddingService;
    private final SimilarityService similarityService;
    private final VectorStore vectorStore;

    public IssueController(
            IssueService issueService,
            AiService aiService,
            EmbeddingService embeddingService,
            SimilarityService similarityService,
            VectorStore vectorStore,
            S3Service s3Service) {

        this.issueService = issueService;
        this.aiService = aiService;
        this.embeddingService = embeddingService;
        this.similarityService = similarityService;
        this.vectorStore = vectorStore;
        this.s3Service = s3Service;
    }

    // When someone sends a POST request to /issues, run this method.
    @PostMapping
    public Issue createIssue(@RequestBody Issue issue)
    {
        // Save the issue to DynamoDB.
        Issue createdIssue = issueService.createIssue(issue);

        // Combine the title and description into the text
        // that will be used to create the embedding.
        String text =
                createdIssue.getTitle() + "\n" +
                        createdIssue.getDescription();

        // Create an embedding for the new issue.
        List<Float> embedding =
                embeddingService.createEmbedding(text);

        // Store the issue and its embedding in the VectorStore.
        vectorStore.addIssue(createdIssue, embedding);

        return createdIssue;
    }

    @PostMapping("/{id}/analyze")
    public Issue analyzeIssue(@PathVariable Long id)
    {
        // Get the issue from DynamoDB.
        Issue issue = issueService.getIssueById(id);

        // Send the issue to OpenAI and receive the structured AI analysis.
        AiAnalysis analysis = aiService.analyzeIssue(
                issue.getTitle(),
                issue.getDescription()
        );

        // Save the AI analysis results into DynamoDB.
        return issueService.updateAiAnalysis(
                id,
                analysis.getCategory(),
                analysis.getPriority(),
                analysis.getRecommendedTeam(),
                analysis.getSummary()
        );
    }

    @GetMapping
    public List<Issue> getAllIssues() {
        return issueService.getAllIssues();
    }

    @GetMapping("/{id}")
    public Issue getIssueById(@PathVariable Long id) {
        return issueService.getIssueById(id);
    }

    @PutMapping("/{id}/resolve")
    public Issue resolveIssue(
            @PathVariable Long id,
            @RequestBody ResolveIssueRequest request) {

        return issueService.resolveIssue(id, request.getResolution());
    }

    @DeleteMapping("/{id}")
    public void deleteIssue(@PathVariable Long id) {
        issueService.deleteIssue(id);
    }

    @PostMapping("/ai/query")
    public String answerQuestion(@RequestBody AIQueryRequest request) {

        // Create an embedding for the user's question.
        List<Float> queryEmbedding =
                embeddingService.createEmbedding(request.getQuestion());

        // Find the issues that are semantically most similar to the question.
        List<Issue> relevantIssues =
                similarityService.findMostSimilar(
                        queryEmbedding,
                        vectorStore.getVectors(),
                        3
                );

        StringBuilder issueData = new StringBuilder();

        for (Issue issue : relevantIssues) {
            issueData.append(
                    "Issue ID: " + issue.getId() + "\n" +
                            "Title: " + issue.getTitle() + "\n" +
                            "Description: " + issue.getDescription() + "\n" +
                            "Status: " + issue.getStatus() + "\n" +
                            "Category: " + issue.getCategory() + "\n" +
                            "Priority: " + issue.getPriority() + "\n" +
                            "Recommended Team: " + issue.getRecommendedTeam() + "\n" +
                            "AI Summary: " + issue.getAiSummary() + "\n\n"
            );
        }

        return aiService.answerQuestion(
                request.getQuestion(),
                issueData.toString()
        );
    }

    @PostMapping(
            value = "/{id}/attachments",
            consumes = "multipart/form-data"
    )
    public String uploadAttachment(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) {

        try {
            return s3Service.uploadFile(file, id);
        } catch (Exception e) {
            throw new RuntimeException("Failed to upload attachment", e);
        }
    }
}

