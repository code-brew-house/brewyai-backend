package com.brewy.backend.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Base64;
import java.util.UUID;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.brewy.backend.exception.AudioProcessingException;

import ws.schild.jave.EncoderException;
import ws.schild.jave.InputFormatException;
import ws.schild.jave.MultimediaObject;
import ws.schild.jave.info.MultimediaInfo;

@Service
public class AudioProcessingService {

    @Value("${app.temp-dir}")
    private String tempDir;

    private static final String[] SUPPORTED_FORMATS = {"mp3", "wav", "ogg", "flac", "aac", "m4a"};

    /**
     * Process an audio file, extract information and convert it to base64
     *
     * @param audioFile The uploaded audio file
     * @return Information about the processed audio and its base64 encoding
     */
    public AudioInfo processAudioFile(MultipartFile audioFile) {
        // Validate the file
        validateAudioFile(audioFile);

        try {
            // Create temp directory if it doesn't exist
            Path tempDirPath = Paths.get(tempDir);
            if (!Files.exists(tempDirPath)) {
                Files.createDirectories(tempDirPath);
            }

            // Save the file to a temp location
            String originalFilename = audioFile.getOriginalFilename();
            String fileExtension = FilenameUtils.getExtension(originalFilename).toLowerCase();
            String tempFilename = UUID.randomUUID() + "." + fileExtension;
            File tempFile = new File(tempDir, tempFilename);
            audioFile.transferTo(tempFile);

            // Extract audio information
            MultimediaObject multimediaObject = new MultimediaObject(tempFile);
            MultimediaInfo info = multimediaObject.getInfo();

            // Convert to base64
            byte[] fileContent = Files.readAllBytes(tempFile.toPath());
            String base64Content = Base64.getEncoder().encodeToString(fileContent);

            // Create result
            AudioInfo audioInfo = new AudioInfo();
            audioInfo.setFileName(originalFilename);
            audioInfo.setFileFormat(fileExtension);
            audioInfo.setFileSize(tempFile.length());
            audioInfo.setDurationInSeconds((double) info.getDuration() / 1000);
            audioInfo.setBase64Content(base64Content);

            // Clean up
            Files.deleteIfExists(tempFile.toPath());

            return audioInfo;
        } catch (IOException e) {
            throw new AudioProcessingException("Failed to process audio file: " + e.getMessage(), e);
        } catch (InputFormatException e) {
            throw new AudioProcessingException("Invalid audio format: " + e.getMessage(), e);
        } catch (EncoderException e) {
            throw new AudioProcessingException("Error analyzing audio file: " + e.getMessage(), e);
        }
    }

    /**
     * Validates that the uploaded file is acceptable
     */
    private void validateAudioFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new AudioProcessingException("Audio file is empty or not provided");
        }

        String filename = file.getOriginalFilename();
        if (StringUtils.isBlank(filename)) {
            throw new AudioProcessingException("Audio filename is blank");
        }

        String extension = FilenameUtils.getExtension(filename).toLowerCase();
        if (!Arrays.asList(SUPPORTED_FORMATS).contains(extension)) {
            throw new AudioProcessingException("Unsupported file format: " + extension +
                    ". Supported formats are: " + String.join(", ", SUPPORTED_FORMATS));
        }
    }

    /**
     * Inner class to hold audio processing results
     */
    public static class AudioInfo {
        private String fileName;
        private String fileFormat;
        private long fileSize;
        private double durationInSeconds;
        private String base64Content;

        // Getters and setters
        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        public String getFileFormat() {
            return fileFormat;
        }

        public void setFileFormat(String fileFormat) {
            this.fileFormat = fileFormat;
        }

        public long getFileSize() {
            return fileSize;
        }

        public void setFileSize(long fileSize) {
            this.fileSize = fileSize;
        }

        public double getDurationInSeconds() {
            return durationInSeconds;
        }

        public void setDurationInSeconds(double durationInSeconds) {
            this.durationInSeconds = durationInSeconds;
        }

        public String getBase64Content() {
            return base64Content;
        }

        public void setBase64Content(String base64Content) {
            this.base64Content = base64Content;
        }
    }
}
