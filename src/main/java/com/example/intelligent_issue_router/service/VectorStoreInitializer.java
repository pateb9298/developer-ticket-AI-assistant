package com.example.intelligent_issue_router.service;

import com.example.intelligent_issue_router.model.Issue;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class VectorStoreInitializer implements CommandLineRunner {

    private final IssueService issueService;
    private final EmbeddingService embeddingService;
    private final VectorStore vectorStore;

    public VectorStoreInitializer(
            IssueService issueService,
            EmbeddingService embeddingService,
            VectorStore vectorStore) {

        this.issueService = issueService;
        this.embeddingService = embeddingService;
        this.vectorStore = vectorStore;
    }

    @Override
    public void run(String... args) {

        System.out.println("Loading existing issues into VectorStore...");

        List<Issue> issues = issueService.getAllIssues();

        for (Issue issue : issues) {

            String text =
                    issue.getTitle() + "\n" +
                            issue.getDescription();

            List<Float> embedding =
                    embeddingService.createEmbedding(text);

            vectorStore.addIssue(issue, embedding);
        }

        System.out.println(
                "Loaded " + issues.size() +
                        " issues into VectorStore."
        );
    }
}