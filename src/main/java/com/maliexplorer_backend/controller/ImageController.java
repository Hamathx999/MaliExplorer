package com.maliexplorer_backend.controller;

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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
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
