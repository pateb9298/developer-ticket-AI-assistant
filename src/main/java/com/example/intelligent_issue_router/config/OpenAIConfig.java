package com.example.intelligent_issue_router.config;

import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAIConfig
{
    // Create an OpenAIClient object and manage it.
    @Bean
    public OpenAIClient openAIClient()
    {
        // Create the OpenAI client using the API key stored in env.
        return OpenAIOkHttpClient.fromEnv();
    }
}
