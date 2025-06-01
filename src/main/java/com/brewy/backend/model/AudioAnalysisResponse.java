package com.brewy.backend.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AudioAnalysisResponse {
    private String fileName;
    private String fileFormat;
    private long fileSize;
    private double durationInSeconds;
    private TonalityResult tonalityResult;
    private long processingTimeMs;
}
