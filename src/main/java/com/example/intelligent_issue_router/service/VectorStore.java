package com.example.intelligent_issue_router.service;

import com.example.intelligent_issue_router.model.Issue;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class VectorStore {

    private final List<StoredVector> vectors = new ArrayList<>();

    public void addIssue(Issue issue, List<Float> embedding) {
        vectors.add(new StoredVector(issue, embedding));
    }

    public List<StoredVector> getVectors() {
        return vectors;
    }

    public static class StoredVector {

        private final Issue issue;
        private final List<Float> embedding;

        public StoredVector(Issue issue, List<Float> embedding) {
            this.issue = issue;
            this.embedding = embedding;
        }

        public Issue getIssue() {
            return issue;
        }

        public List<Float> getEmbedding() {
            return embedding;
        }
    }
}