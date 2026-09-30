package com.maliexplorer_backend.serviceimpl;

import com.maliexplorer_backend.config.SupabaseConfig;
import com.maliexplorer_backend.dto.FileUploadResponseDTO;
import com.maliexplorer_backend.exception.BadRequestException;
import com.maliexplorer_backend.service.StorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class SupabaseStorageServiceImpl implements StorageService {

    private final WebClient supabaseWebClient;
    private final SupabaseConfig supabaseConfig;

    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList(
            "jpg", "jpeg", "png", "webp", "gif", "svg", "pdf", "mp4"
    );

    @Override
    public FileUploadResponseDTO uploadFile(MultipartFile file, String folder) {
        return uploadFile(file, supabaseConfig.getDefaultBucket(), folder);
    }

    @Override
    public FileUploadResponseDTO uploadFile(MultipartFile file, String bucket, String folder) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("Le fichier envoyé est vide ou manquant.");
        }

        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename() != null ? file.getOriginalFilename() : "fichier");
        String extension = extractExtension(originalFilename);

        if (!ALLOWED_EXTENSIONS.contains(extension.toLowerCase())) {
            throw new BadRequestException("Format de fichier non supporté (." + extension + "). Formats acceptés : " + ALLOWED_EXTENSIONS);
        }

        String uniqueFileName = System.currentTimeMillis() + "_" + UUID.randomUUID().toString().substring(0, 8) + "." + extension;
        String sanitizedFolder = (StringUtils.hasText(folder)) ? folder.replaceAll("^/+|/+$", "") : "general";
        String fullPath = sanitizedFolder + "/" + uniqueFileName;

        byte[] fileBytes;
        try {
            fileBytes = file.getBytes();
        } catch (IOException e) {
            log.error("Erreur lors de la lecture du fichier : {}", e.getMessage());
            throw new BadRequestException("Erreur lors de la lecture du fichier : " + e.getMessage());
        }

        String contentType = file.getContentType();
        if (!StringUtils.hasText(contentType)) {
            contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
        }

        log.info("Téléversement vers Supabase Storage : Bucket={}, Path={}, Taille={} octets", bucket, fullPath, fileBytes.length);

        try {
            supabaseWebClient.post()
                    .uri("/storage/v1/object/{bucket}/{path}", bucket, fullPath)
                    .header("x-upsert", "true")
                    .contentType(MediaType.parseMediaType(contentType))
                    .bodyValue(fileBytes)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, clientResponse ->
                            clientResponse.bodyToMono(String.class)
                                    .flatMap(errorBody -> {
                                        log.error("Erreur Supabase Storage HTTP {}: {}", clientResponse.statusCode(), errorBody);
                                        return Mono.error(new BadRequestException("Échec de l'envoi vers Supabase : " + errorBody));
                                    })
                    )
                    .toBodilessEntity()
                    .block();

            String publicUrl = getPublicUrl(bucket, fullPath);

            log.info("Fichier téléversé avec succès. URL : {}", publicUrl);

            return FileUploadResponseDTO.builder()
                    .originalFileName(originalFilename)
                    .storedFileName(uniqueFileName)
                    .filePath(fullPath)
                    .fileUrl(publicUrl)
                    .bucket(bucket)
                    .contentType(contentType)
                    .size(file.getSize())
                    .uploadedAt(LocalDateTime.now())
                    .message("Fichier téléversé avec succès sur Supabase Storage")
                    .build();

        } catch (BadRequestException e) {
            throw e;
        } catch (Exception e) {
            log.error("Exception lors du téléversement vers Supabase : {}", e.getMessage(), e);
            throw new BadRequestException("Erreur de communication avec Supabase Storage : " + e.getMessage());
        }
    }

    @Override
    public void deleteFile(String filePath) {
        deleteFile(supabaseConfig.getDefaultBucket(), filePath);
    }

    @Override
    public void deleteFile(String bucket, String filePath) {
        if (!StringUtils.hasText(filePath)) {
            throw new BadRequestException("Le chemin du fichier à supprimer est obligatoire.");
        }

        String cleanedPath = filePath.replaceAll("^/+", "");
        log.info("Suppression du fichier Supabase Storage : Bucket={}, Path={}", bucket, cleanedPath);

        try {
            supabaseWebClient.delete()
                    .uri("/storage/v1/object/{bucket}/{path}", bucket, cleanedPath)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, clientResponse ->
                            clientResponse.bodyToMono(String.class)
                                    .flatMap(errorBody -> {
                                        log.warn("Erreur lors de la suppression sur Supabase Storage HTTP {}: {}", clientResponse.statusCode(), errorBody);
                                        return Mono.error(new BadRequestException("Échec de la suppression : " + errorBody));
                                    })
                    )
                    .toBodilessEntity()
                    .block();

            log.info("Fichier supprimé avec succès : {}", cleanedPath);
        } catch (BadRequestException e) {
            throw e;
        } catch (Exception e) {
            log.error("Exception lors de la suppression sur Supabase : {}", e.getMessage(), e);
            throw new BadRequestException("Erreur lors de la suppression sur Supabase Storage : " + e.getMessage());
        }
    }

    @Override
    public String getPublicUrl(String bucket, String filePath) {
        String baseUrl = supabaseConfig.getSupabaseUrl().replaceAll("/+$", "");
        String cleanedPath = filePath.replaceAll("^/+", "");
        return baseUrl + "/storage/v1/object/public/" + bucket + "/" + cleanedPath;
    }

    private String extractExtension(String filename) {
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex == -1 || lastDotIndex == filename.length() - 1) {
            return "bin";
        }
        return filename.substring(lastDotIndex + 1);
    }
}

