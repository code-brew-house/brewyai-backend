package com.brewy.backend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TonalityResult {
    private String dominantTone;      // e.g., "happy", "sad", "angry", "neutral"
    private Double confidenceScore;   // 0.0 to 1.0
    private String toneDescription;   // Detailed description of the tone
    private String emotionalRange;    // e.g., "high", "medium", "low"
    private String musicalKey;        // e.g., "C major", "A minor" (if applicable)
    private String additionalNotes;   // Any other observations
}
