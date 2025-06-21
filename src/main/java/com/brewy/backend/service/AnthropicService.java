package com.brewy.backend.service;

import static com.brewy.backend.provider.TonalityProvider.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import com.brewy.backend.exception.AudioProcessingException;
import com.brewy.backend.model.TonalityResult;
import com.brewy.backend.service.AudioProcessingService.AudioInfo;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class AnthropicService {

    @Autowired
    private ChatClient chatClient;

    @Autowired
    private ObjectMapper objectMapper;

    private String tonalityAnalysisPrompt;

    public AnthropicService() {
        // Load the prompt from the resources folder
        try {
            ClassPathResource resource = new ClassPathResource("prompts/default.txt");
            tonalityAnalysisPrompt = new String(Files.readAllBytes(resource.getFile().toPath()),
                    StandardCharsets.UTF_8);
        } catch (IOException e) {
            // Fall back to a default prompt if the file can't be loaded
            tonalityAnalysisPrompt = "You are an expert audio analyst. Analyze the following audio file and provide details about its tonality. " +
                    "Return your analysis in JSON format with fields: dominantTone, confidenceScore, toneDescription, " +
                    "emotionalRange, musicalKey, and additionalNotes.";
        }
    }

    /**
     * Analyze the tonality of an audio file (based on its properties)
     *
     * @param audioInfo Information about the audio file including duration, format, and encoded content
     * @return Tonality analysis results
     */
    public TonalityResult analyzeTonality(AudioInfo audioInfo,
                                          String clientId,
                                          Optional<String> customPrompt) {
        try {
            // Create a modified prompt based on the parameters
            String systemPrompt = getTonality(clientId);


            // Build the prompt with audio information
            String audioInfoText = String.format(
                    "Audio file name: %s\n" +
                            "Format: %s\n" +
                            "Duration: %.2f seconds\n" +
                            "File size: %d bytes\n" +
                            "Base64 audio content: %s",
                    audioInfo.getFileName(),
                    audioInfo.getFileFormat(),
                    audioInfo.getDurationInSeconds(),
                    audioInfo.getFileSize(),
                    audioInfo.getBase64Content()
            );

            //Append custom prompt to system prompt if not empty
            if (customPrompt.isPresent()) {
                systemPrompt += "\n\nAdditional Instructions: " + customPrompt.get();
            }

            SystemMessage systemMessage = new SystemMessage(systemPrompt);
            UserMessage userMessage = new UserMessage(audioInfoText);
            List<Message> messages = Arrays.asList(systemMessage, userMessage);

            // Create the prompt using Spring AI components
            Prompt prompt = Prompt.builder().messages(messages).build();

            // Send to Claude via Spring AI
            String responseText = chatClient.prompt(prompt).call().chatResponse().getResult().toString();

            // Extract JSON part of the response
            String jsonResponse = extractJsonFromResponse(responseText);

            // Parse the response into a TonalityResult object
            return objectMapper.readValue(jsonResponse, TonalityResult.class);

        } catch (Exception e) {
            throw new AudioProcessingException("Failed to analyze audio tonality: " + e.getMessage(), e);
        }
    }

    /**
     * Extract JSON from Claude's response, which might contain additional text
     */
    private String extractJsonFromResponse(String response) {
        // Look for JSON object in the response
        int startIndex = response.indexOf('{');
        int endIndex = response.lastIndexOf('}');

        if (startIndex >= 0 && endIndex >= 0 && endIndex > startIndex) {
            return response.substring(startIndex, endIndex + 1);
        }

        // If no JSON found, throw an exception
        throw new AudioProcessingException("Could not extract valid JSON from AI response");
    }
}