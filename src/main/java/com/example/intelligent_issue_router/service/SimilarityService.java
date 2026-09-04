package com.example.intelligent_issue_router.service;

import com.example.intelligent_issue_router.model.Issue;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class SimilarityService {

    public double cosineSimilarity(List<Float> a, List<Float> b) {

        double dotProduct = 0.0;
        double magnitudeA = 0.0;
        double magnitudeB = 0.0;

        for (int i = 0; i < a.size(); i++) {

            dotProduct += a.get(i) * b.get(i);

            magnitudeA += a.get(i) * a.get(i);
            magnitudeB += b.get(i) * b.get(i);
        }

        if (magnitudeA == 0 || magnitudeB == 0) {
            return 0.0;
        }

        return dotProduct /
                (Math.sqrt(magnitudeA) * Math.sqrt(magnitudeB));
    }

    public List<Issue> findMostSimilar(
            List<Float> queryEmbedding,
            List<VectorStore.StoredVector> vectors,
            int limit) {

        return vectors.stream()
                .sorted(Comparator.comparingDouble(
                        vector -> -cosineSimilarity(
                                queryEmbedding,
                                vector.getEmbedding()
                        )
                ))
                .limit(limit)
                .map(VectorStore.StoredVector::getIssue)
                .toList();
    }
}