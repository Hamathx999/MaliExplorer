package com.maliexplorer_backend.service;

import com.maliexplorer_backend.dto.FileUploadResponseDTO;
import org.springframework.web.multipart.MultipartFile;

public interface StorageService {

    FileUploadResponseDTO uploadFile(MultipartFile file, String folder);

    FileUploadResponseDTO uploadFile(MultipartFile file, String bucket, String folder);

    void deleteFile(String filePath);

    void deleteFile(String bucket, String filePath);

    String getPublicUrl(String bucket, String filePath);
}

