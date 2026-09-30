package com.maliexplorer_backend.controller;

import com.maliexplorer_backend.dto.FileUploadResponseDTO;
import com.maliexplorer_backend.service.StorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/storage")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Stockage Supabase", description = "Téléversement et gestion des médias (photos, panoramas 360, documents) sur Supabase Storage")
public class StorageController {

    private final StorageService storageService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Téléverser un fichier dans un dossier spécifique",
            description = "Téléverse un fichier (image, document) vers Supabase Storage dans le dossier indiqué (par défaut 'general')")
    @ApiResponse(responseCode = "201", description = "Fichier téléversé avec succès",
            content = @Content(schema = @Schema(implementation = FileUploadResponseDTO.class)))
    public ResponseEntity<FileUploadResponseDTO> uploadFile(
            @Parameter(description = "Fichier binaire à téléverser", required = true)
            @RequestParam("file") MultipartFile file,
            @Parameter(description = "Nom du sous-dossier de destination (ex: lieux, villes, avatars)")
            @RequestParam(value = "folder", defaultValue = "general") String folder) {

        FileUploadResponseDTO response = storageService.uploadFile(file, folder);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping(value = "/upload/panoramas", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Téléverser une image ou vidéo panoramique 360°",
            description = "Réservé aux administrateurs. Stocke les panoramas immersifs des lieux historiques dans le dossier 'panoramas_360'")
    public ResponseEntity<FileUploadResponseDTO> uploadPanorama(
            @RequestParam("file") MultipartFile file) {

        FileUploadResponseDTO response = storageService.uploadFile(file, "panoramas_360");
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping(value = "/upload/lieux", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Téléverser une photographie de lieu historique",
            description = "Stocke les photos illustrant les monuments et sites historiques dans 'lieux_historiques'")
    public ResponseEntity<FileUploadResponseDTO> uploadPhotoLieu(
            @RequestParam("file") MultipartFile file) {

        FileUploadResponseDTO response = storageService.uploadFile(file, "lieux_historiques");
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping(value = "/upload/villes", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Téléverser une photographie de ville",
            description = "Stocke les photos illustrant les villes du Mali dans 'villes'")
    public ResponseEntity<FileUploadResponseDTO> uploadPhotoVille(
            @RequestParam("file") MultipartFile file) {

        FileUploadResponseDTO response = storageService.uploadFile(file, "villes");
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping(value = "/upload/avatars", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Téléverser une photo de profil utilisateur",
            description = "Accessible à tout utilisateur connecté pour modifier sa photo de profil dans 'avatars'")
    public ResponseEntity<FileUploadResponseDTO> uploadAvatar(
            @RequestParam("file") MultipartFile file) {

        FileUploadResponseDTO response = storageService.uploadFile(file, "avatars");
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @DeleteMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Supprimer un fichier sur Supabase Storage",
            description = "Supprime un fichier hébergé via son chemin relatif (ex: 'lieux_historiques/photo.jpg')")
    public ResponseEntity<Void> deleteFile(
            @Parameter(description = "Chemin relatif du fichier dans le bucket", required = true)
            @RequestParam("filePath") String filePath) {

        storageService.deleteFile(filePath);
        return ResponseEntity.noContent().build();
    }
}

