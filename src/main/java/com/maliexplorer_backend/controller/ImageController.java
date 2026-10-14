package com.maliexplorer_backend.controller;

<<<<<<< HEAD
import com.maliexplorer_backend.dto.FileUploadResponseDTO;
import com.maliexplorer_backend.service.StorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
=======
import com.maliexplorer_backend.model.ImageModel;
import com.maliexplorer_backend.repository.ImageRepository;
import com.maliexplorer_backend.service.SupabaseStorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
>>>>>>> 81c82d1b135213e0cb34fa3b6388b507d7f1b919
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
<<<<<<< HEAD

import java.io.File;
import java.io.IOException;
=======
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.net.MalformedURLException;
>>>>>>> 81c82d1b135213e0cb34fa3b6388b507d7f1b919
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
<<<<<<< HEAD
=======
import java.util.HashMap;
import java.util.List;
import java.util.Map;
>>>>>>> 81c82d1b135213e0cb34fa3b6388b507d7f1b919
import java.util.UUID;

@Slf4j
@RestController
<<<<<<< HEAD
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

=======
@RequestMapping("/api")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Tag(name = "Images", description = "Gestion, stockage Supabase et téléversement des images MaliExplorer")
public class ImageController {

    private final SupabaseStorageService supabaseStorageService;
    private final ImageRepository imageRepository;
    private final Path localStorageLocation = Paths.get("uploads/images").toAbsolutePath().normalize();

    @PostMapping(value = {"/images/upload", "/upload"}, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Téléverser une image vers Supabase Storage et enregistrer en base de données")
    public ResponseEntity<Map<String, Object>> uploadImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "folder", required = false, defaultValue = "general") String folder,
            @RequestParam(value = "entiteType", required = false) String entiteType,
            @RequestParam(value = "entiteId", required = false) Long entiteId) {

        if (file.isEmpty()) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Fichier vide");
            error.put("message", "Veuillez sélectionner un fichier valide.");
            return ResponseEntity.badRequest().body(error);
        }

        String originalFileName = StringUtils.cleanPath(
                file.getOriginalFilename() != null ? file.getOriginalFilename() : "image.jpg");

        String fileUrl = null;
        boolean uploadedToSupabase = false;

        // 1. Téléversement vers Supabase Storage
        try {
            fileUrl = supabaseStorageService.uploadFile(file, folder);
            uploadedToSupabase = true;
            log.info("Téléversement Supabase réussi pour '{}' -> {}", originalFileName, fileUrl);
        } catch (Exception e) {
            log.error("Échec du téléversement vers Supabase Storage : {}", e.getMessage(), e);
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Échec Supabase Storage");
            error.put("message", e.getMessage());
            error.put("hint", "Vérifiez que la clé Supabase service_role est utilisée ou que les RLS Policies de Supabase autorisent INSERT pour 'anon'.");
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(error);
        }

        // 3. Enregistrement systématique de la ligne dans la table 'images' côté back
        ImageModel imageModel = ImageModel.builder()
                .nomFichier(originalFileName)
                .imageUrl(fileUrl)
                .typeMime(file.getContentType())
                .taille(file.getSize())
                .entiteType(entiteType != null ? entiteType.toUpperCase() : folder.toUpperCase())
                .entiteId(entiteId)
                .dateUpload(LocalDateTime.now())
                .build();

        ImageModel savedImage = imageRepository.save(imageModel);
        log.info("Ligne enregistrée dans la table 'images' avec l'ID {}", savedImage.getIdImage());

        Map<String, Object> response = new HashMap<>();
        response.put("idImage", savedImage.getIdImage());
        response.put("fileUrl", fileUrl);
        response.put("imageUrl", fileUrl);
        response.put("fileName", originalFileName);
        response.put("size", file.getSize());
        response.put("contentType", file.getContentType());
        response.put("storageProvider", uploadedToSupabase ? "SUPABASE" : "LOCAL");
        response.put("success", true);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/images")
    @Operation(summary = "Lister toutes les images enregistrées dans la table 'images'")
    public ResponseEntity<List<ImageModel>> getAllImages() {
        return ResponseEntity.ok(imageRepository.findAll());
    }

    @GetMapping("/images/entite/{entiteType}/{entiteId}")
    @Operation(summary = "Lister les images liées à une entité spécifique (ex: VILLE/1, PLAT/2)")
    public ResponseEntity<List<ImageModel>> getImagesByEntite(
            @PathVariable String entiteType,
            @PathVariable Long entiteId) {
        return ResponseEntity.ok(imageRepository.findByEntiteTypeAndEntiteId(entiteType.toUpperCase(), entiteId));
    }

    @GetMapping("/images/{fileName:.+}")
    @Operation(summary = "Afficher / Télécharger une image locale de secours")
    public ResponseEntity<Resource> getImage(@PathVariable String fileName) {
        try {
            Path filePath = this.localStorageLocation.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                String contentType = "application/octet-stream";
                try {
                    contentType = Files.probeContentType(filePath);
                } catch (IOException ignored) {}

                if (contentType == null) {
                    contentType = "image/jpeg";
                }

                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (MalformedURLException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
>>>>>>> 81c82d1b135213e0cb34fa3b6388b507d7f1b919
