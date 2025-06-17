package com.brewy.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.brewy.backend.model.AudioAnalysisRequest;
import com.brewy.backend.model.AudioAnalysisResponse;
import com.brewy.backend.model.TonalityResult;
import com.brewy.backend.service.AudioProcessingService.AudioInfo;

import java.util.Optional;

@Service
public class AudioAnalysisService {

    @Autowired
    private AudioProcessingService audioProcessingService;

    @Autowired
    private AnthropicService anthropicService;

    /**
     * Process audio file and analyze its tonality
     *
     * @param request The analysis request containing the audio file and analysis options
     * @return Analysis results
     */
    public AudioAnalysisResponse analyzeAudio(AudioAnalysisRequest request) {
        long startTime = System.currentTimeMillis();

        // Process the audio file
        MultipartFile audioFile = request.getAudioFile();
        AudioInfo audioInfo = audioProcessingService.processAudioFile(audioFile);

        // Analyze tonality using Claude
        TonalityResult tonalityResult = anthropicService.analyzeTonality(
                audioInfo,
                request.getClientId(),
                Optional.of(request.getCustomPrompt())
        );

        // Create the response
        AudioAnalysisResponse response = new AudioAnalysisResponse();
        response.setFileName(audioInfo.getFileName());
        response.setFileFormat(audioInfo.getFileFormat());
        response.setFileSize(audioInfo.getFileSize());
        response.setDurationInSeconds(audioInfo.getDurationInSeconds());
        response.setTonalityResult(tonalityResult);
        response.setProcessingTimeMs(System.currentTimeMillis() - startTime);

        return response;
    }
}
