package com.example.intelligent_issue_router.service;

import com.example.intelligent_issue_router.dto.AiAnalysis;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.openai.client.OpenAIClient;
import com.openai.models.chat.completions.ChatCompletion;
import com.openai.models.chat.completions.ChatCompletionCreateParams;
import org.springframework.stereotype.Service;

@Service
public class AiService
{
    private final OpenAIClient openAIClient;
    // ObjectMapper converts JSON from AI to Java object.
    private final ObjectMapper objectMapper = new ObjectMapper();

    public AiService(OpenAIClient openAIClient)
    {
        this.openAIClient = openAIClient;
    }

    public AiAnalysis analyzeIssue(String title, String description)
    {
        ChatCompletionCreateParams params = ChatCompletionCreateParams.builder()
                .model("gpt-4o-mini")
                // Construct the actual prompt and give the AI the issue
                // Give the AI the issue and tell it exactly what JSON to return.
                .addUserMessage(
                        "Analyze this software issue.\n\n" +
                                "Title: " + title + "\n" +
                                "Description: " + description + "\n\n" +

                                "Return ONLY valid JSON. Do not use markdown or code blocks.\n" +
                                "Use exactly these fields:\n" +
                                "category\n" +
                                "priority\n" +
                                "recommendedTeam\n" +
                                "summary\n\n" +

                                "Allowed priority values: LOW, MEDIUM, HIGH, CRITICAL.\n" +
                                "Choose exactly one priority value.\n\n" +

                                "Choose the category that best describes the issue.\n" +
                                "Examples: AUTHENTICATION, DATABASE, PAYMENT, FRONTEND, BACKEND, PERFORMANCE, SECURITY.\n\n" +

                                "Choose the engineering team that should investigate the issue.\n" +
                                "Examples: BACKEND, FRONTEND, DATABASE, SECURITY, DEVOPS, PAYMENTS.\n\n" +

                                "Keep the summary concise and explain the main problem."
                )
                .build();

        // Creating a request object that describes what I want to send to the AI
        ChatCompletion response = openAIClient.chat().completions().create(params);

        // The API response contains one or more possible responses called choices. getFirst() grabs the first.
        String json = response.choices()
                .getFirst()
                .message()
                .content()
                .orElse("{}");

        try {
            // Convert the JSON returned by the AI into our Java AiAnalysis object.
            return objectMapper.readValue(json, AiAnalysis.class);

        } catch (Exception e) {
            throw new RuntimeException("Failed to parse AI response: " + json, e);
        }
    }

    public String answerQuestion(String question, String issueData)
    {
        ChatCompletionCreateParams params = ChatCompletionCreateParams.builder()
                .model("gpt-4o-mini")
                .addUserMessage(
                        "You are analyzing a collection of software issues.\n\n" +
                                "Here are the issues:\n" +
                                issueData + "\n\n" +
                                "Answer this question using only the issue data provided:\n" +
                                question
                )
                .build();

        ChatCompletion response =
                openAIClient.chat().completions().create(params);

        return response.choices()
                .getFirst()
                .message()
                .content()
                .orElse("No answer returned.");
    }
}
