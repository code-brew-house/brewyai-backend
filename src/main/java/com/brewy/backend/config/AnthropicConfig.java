package com.brewy.backend.config;

// This file is no longer needed as Spring AI provides autoconfiguration
// We'll keep it as a placeholder but it won't have any content
// Spring AI will automatically configure the AnthropicChatModel based on properties in application.properties

import org.springframework.ai.anthropic.AnthropicChatModel;
import org.springframework.ai.anthropic.AnthropicChatOptions;
import org.springframework.ai.anthropic.api.AnthropicApi;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AnthropicConfig {
    // Spring AI handles the configuration automatically

    @Value("${spring.ai.anthropic.api-key}")
    private String apiKey;

    @Value("${spring.ai.anthropic.chat.options.model}")
    private String model;

    @Value("${spring.ai.anthropic.chat.options.temperature:0.7}")
    private Double temperature;

    @Value("${spring.ai.anthropic.chat.options.max-tokens:4096}")
    private Integer maxTokens;


    @Bean
    public AnthropicApi anthropicApi() {
        return AnthropicApi.builder().apiKey(apiKey).build();
    }

    @Bean
    public AnthropicChatOptions anthropicChatOptions() {
        return AnthropicChatOptions.builder()
                .model(model)
                .temperature(temperature)
                .maxTokens(maxTokens)
                .build();
    }

    @Bean
    public ChatClient chatClient(AnthropicApi anthropicApi, AnthropicChatOptions options) {
        return ChatClient.builder(
                        AnthropicChatModel.builder().defaultOptions(options).anthropicApi(anthropicApi).build())
                .build();
    }

}

