package com.brewy.backend.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
public class AudioAnalysisController {

    @Autowired
    private AudioAnalysisService audioAnalysisService;

    @PostMapping(value = "/analyze", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AudioAnalysisResponse> analyzeAudio(
            @RequestParam("file") MultipartFile audioFile,
            @RequestParam(value = "clientId") String clientId) {

        // Create request object
        AudioAnalysisRequest request = new AudioAnalysisRequest();
        request.setAudioFile(audioFile);
        request.setClientId(clientId);
        // Process the request
        AudioAnalysisResponse response = audioAnalysisService.analyzeAudio(request);

        return ResponseEntity.ok(response);
    }
}
