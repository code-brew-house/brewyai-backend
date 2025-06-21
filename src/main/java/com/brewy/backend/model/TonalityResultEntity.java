package com.brewy.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tonality_results")
@Builder
public class TonalityResultEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String dominantTone;      // e.g., "happy", "sad", "angry", "neutral"

    @Column(nullable = false)
    private Double confidenceScore;   // 0.0 to 1.0

    @Column(nullable = false)
    private String toneDescription;   // Detailed description of the tone

    @Column(nullable = false)
    private String emotionalRange;    // e.g., "high", "medium", "low"

    @Column(nullable = true)
    private String musicalKey;        // e.g., "C major", "A minor" (if applicable)

    @Column(nullable = true)
    private String additionalNotes;   // Any other observations


}
