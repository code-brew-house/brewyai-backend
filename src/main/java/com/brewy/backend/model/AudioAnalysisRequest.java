package com.brewy.backend.model;

import org.springframework.web.multipart.MultipartFile;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AudioAnalysisRequest {

    private MultipartFile audioFile;
    private String clientId;
    private String customPrompt;

}
