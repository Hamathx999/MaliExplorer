package com.maliexplorer_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileUploadResponseDTO {

    private String originalFileName;
    private String storedFileName;
    private String filePath;
    private String fileUrl;
    private String bucket;
    private String contentType;
    private long size;
    private LocalDateTime uploadedAt;
    private String message;

    @com.fasterxml.jackson.annotation.JsonProperty("imageUrl")
    public String getImageUrl() {
        return this.fileUrl;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("fileName")
    public String getFileName() {
        return this.storedFileName;
    }
}

