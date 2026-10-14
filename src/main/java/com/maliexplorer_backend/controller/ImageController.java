package com.maliexplorer_backend.controller;

import com.maliexplorer_backend.dto.FileUploadResponseDTO;
import com.maliexplorer_backend.service.StorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/images")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Images & Médias", description = "Téléversement et gestion unifiée des images pour l'administration et l'application")
public class ImageController {

    private final StorageService storageService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Téléversement d'image", description = "Téléverse une image vers Supabase Storage ou le stockage local en fallback")
    public ResponseEntity<FileUploadResponseDTO> uploadImage(
            @Parameter(description = "Fichier image à téléverser", required = true)
            @RequestParam("file") MultipartFile file,
            @Parameter(description = "Dossier de destination")
            @RequestParam(value = "folder", defaultValue = "general") String folder,
            @Parameter(description = "Type d'entité associée (optionnel)")
            @RequestParam(value = "entiteType", required = false) String entiteType,
            @Parameter(description = "Identifiant de l'entité (optionnel)")
            @RequestParam(value = "entiteId", required = false) String entiteId,
            HttpServletRequest request) {

        if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest().body(
                    FileUploadResponseDTO.builder()
                            .message("Le fichier envoyé est vide ou manquant.")
                            .build()
            );
        }

        // 1. Tenter l'envoi vers Supabase Storage
        try {
            FileUploadResponseDTO response = storageService.uploadFile(file, folder);
            if (response != null && StringUtils.hasText(response.getFileUrl())) {
                log.info("Image uploadée avec succès sur Supabase : {}", response.getFileUrl());
                return new ResponseEntity<>(response, HttpStatus.CREATED);
            }
        } catch (Exception e) {
            log.warn("Échec du téléversement vers Supabase ({}), basculement automatique sur le stockage local : {}",
                    e.getMessage(), file.getOriginalFilename());
        }

        // 2. Fallback résilient local si Supabase Storage est inaccessible / hors ligne
        try {
            String originalFilename = StringUtils.cleanPath(file.getOriginalFilename() != null ? file.getOriginalFilename() : "image.jpg");
            String extension = "jpg";
            int dotIndex = originalFilename.lastIndexOf('.');
            if (dotIndex >= 0) {
                extension = originalFilename.substring(dotIndex + 1);
            }

            String storedFileName = System.currentTimeMillis() + "_" + UUID.randomUUID().toString().substring(0, 8) + "." + extension;
            String cleanFolder = StringUtils.hasText(folder) ? folder.replaceAll("^/+|/+$", "") : "general";

            Path targetDir = Paths.get("uploads", cleanFolder);
            if (!Files.exists(targetDir)) {
                Files.createDirectories(targetDir);
            }

            Path targetPath = targetDir.resolve(storedFileName);
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            String baseUrl = request.getScheme() + "://" + request.getServerName() + (request.getServerPort() == 80 || request.getServerPort() == 443 ? "" : ":" + request.getServerPort());
            String publicUrl = baseUrl + "/api/uploads/" + cleanFolder + "/" + storedFileName;

            log.info("Image sauvegardée en local avec succès : {}", publicUrl);

            FileUploadResponseDTO localResponse = FileUploadResponseDTO.builder()
                    .originalFileName(originalFilename)
                    .storedFileName(storedFileName)
                    .filePath(cleanFolder + "/" + storedFileName)
                    .fileUrl(publicUrl)
                    .bucket("local")
                    .contentType(file.getContentType())
                    .size(file.getSize())
                    .uploadedAt(LocalDateTime.now())
                    .message("Fichier téléversé avec succès (stockage local)")
                    .build();

            return new ResponseEntity<>(localResponse, HttpStatus.CREATED);
        } catch (IOException ioException) {
            log.error("Erreur critique lors de la sauvegarde locale de l'image : {}", ioException.getMessage(), ioException);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    FileUploadResponseDTO.builder()
                            .message("Erreur interne lors de la sauvegarde du fichier : " + ioException.getMessage())
                            .build()
            );
        }
    }
}

