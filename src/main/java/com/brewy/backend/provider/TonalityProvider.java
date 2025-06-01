package com.brewy.backend.provider;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

public class TonalityProvider {
    private static final String PROMPTS_BASE_PATH = "prompts/";

    public static String getTonality(String clientId) throws IOException {
        String path = PROMPTS_BASE_PATH + "default" + ".txt";
        if (clientId != null){
            path = PROMPTS_BASE_PATH + clientId + ".txt";
        }
        // Load and return tonality from the file
        return loadTonalityFromFile(path);
    }

    private static String loadTonalityFromFile(String path) throws IOException {
        try {
            Resource resource = new ClassPathResource(path);
            return Files.readString(Paths.get(resource.getURI()));
        } catch (IOException e) {
            Resource resource = new ClassPathResource(PROMPTS_BASE_PATH + "default" + ".txt");
            // Fallback to default if client-specific file doesn't exist
            return Files.readString(Paths.get(resource.getURI()));
        }
    }
}
