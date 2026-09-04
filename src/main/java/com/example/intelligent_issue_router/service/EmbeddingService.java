package com.example.intelligent_issue_router.service;
import com.openai.client.OpenAIClient;
import com.openai.models.embeddings.EmbeddingCreateParams;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmbeddingService
{
    private final OpenAIClient openAIClient;

    public EmbeddingService(OpenAIClient openAIClient) {
        this.openAIClient = openAIClient;
    }

    public List<Float> createEmbedding(String text) {

        EmbeddingCreateParams params = EmbeddingCreateParams.builder()
                .model("text-embedding-3-small")
                .input(text)
                .build();

        return openAIClient.embeddings()
                .create(params)
                .data()
                .getFirst()
                .embedding();
    }
}
