package com.brewy.backend.controller;


import com.brewy.backend.model.TonalityResultEntity;
import com.brewy.backend.model.User;
import com.brewy.backend.repository.TonalityResultRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.brewy.backend.model.AudioAnalysisRequest;
import com.brewy.backend.model.AudioAnalysisResponse;
import com.brewy.backend.service.AudioAnalysisService;

@RestController
@RequestMapping("/audio")
@RequiredArgsConstructor
public class AudioAnalysisController {

    private final AudioAnalysisService audioAnalysisService;

    private final TonalityResultRepository tonalityResultRepository;

    @PostMapping(value = "/analyze", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AudioAnalysisResponse> analyzeAudio(
            @RequestParam("file") MultipartFile audioFile,
            @RequestParam(value = "clientId") String clientId,
            @RequestParam(value = "customPrompt", required = false) String customPrompt) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        User currentUser = (User) authentication.getPrincipal();

        // Create request object
        AudioAnalysisRequest request = new AudioAnalysisRequest();
        request.setAudioFile(audioFile);
        request.setClientId(clientId);
        request.setCustomPrompt(customPrompt);

        // Process the request
        AudioAnalysisResponse response = audioAnalysisService.analyzeAudio(request);

        if (response.getTonalityResult() != null) {
            TonalityResultEntity tonalityResultEntity = response.getTonalityResult().to(currentUser);
            tonalityResultRepository.save(tonalityResultEntity);
        }

        return ResponseEntity.ok(response);
    }
}
