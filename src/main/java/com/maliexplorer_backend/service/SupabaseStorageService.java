package com.maliexplorer_backend.service;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

@Service
public class SupabaseStorageService {

    @Value("${supabase.url:https://dzhqwkpwaljqsjwoqvso.supabase.co}")
    private String supabaseUrl;

    @Value("${supabase.key:}")
    private String supabaseKey;

    @Value("${supabase.bucket:maliexplorer-media}")
    private String bucket;

    private final HttpClient httpClient;
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(SupabaseStorageService.class);

    public SupabaseStorageService() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(15))
                .build();
    }

    @PostConstruct
    public void init() {
        if (!StringUtils.hasText(this.supabaseKey)) {
            List<Path> candidates = List.of(
                    Paths.get("application-local.properties"),
                    Paths.get("maliexplorer-backend/application-local.properties"),
                    Paths.get("src/main/resources/application-local.properties"),
                    Paths.get("maliexplorer-backend/src/main/resources/application-local.properties")
            );
            for (Path candidate : candidates) {
                if (Files.exists(candidate)) {
                    try (InputStream in = Files.newInputStream(candidate)) {
                        Properties props = new Properties();
                        props.load(in);
                        String key = props.getProperty("supabase.key");
                        if (StringUtils.hasText(key)) {
                            this.supabaseKey = key.trim();
                            log.info("Clé Supabase Storage chargée depuis {}", candidate.toAbsolutePath());
                            break;
                        }
                    } catch (Exception ignored) {
                    }
                }
            }
        }
    }

    public String getPublicUrl(String folder, String cleanFileName) {
        String baseUrl = (this.supabaseUrl != null ? this.supabaseUrl : "https://dzhqwkpwaljqsjwoqvso.supabase.co")
                .replaceAll("/+$", "");
        String targetBucket = (this.bucket != null && !this.bucket.isBlank()) ? this.bucket : "maliexplorer-media";
        String targetFolder = (folder != null && !folder.trim().isEmpty())
                ? folder.trim().replaceAll("[^a-zA-Z0-9_-]", "") + "/"
                : "";
        return baseUrl + "/storage/v1/object/public/" + targetBucket + "/" + targetFolder + cleanFileName;
    }

    /**
     * Téléverse un fichier local vers Supabase Storage avec un nom déterministe (upsert)
     * et retourne son URL publique permanente.
     */
    public String uploadLocalFileDeterministic(Path filePath, String folder, String targetFileName) {
        String cleanName = targetFileName.replaceAll("[^a-zA-Z0-9._-]", "_");
        String publicUrl = getPublicUrl(folder, cleanName);

        if (filePath == null || !Files.exists(filePath)) {
            return publicUrl;
        }
        init();
        if (!StringUtils.hasText(this.supabaseKey)) {
            return publicUrl;
        }

        try {
            String baseUrl = this.supabaseUrl.replaceAll("/+$", "");
            String targetFolder = (folder != null && !folder.trim().isEmpty())
                    ? folder.trim().replaceAll("[^a-zA-Z0-9_-]", "") + "/"
                    : "";
            String uploadEndpoint = baseUrl + "/storage/v1/object/" + this.bucket + "/" + targetFolder + cleanName;

            String contentType = Files.probeContentType(filePath);
            if (contentType == null || contentType.isBlank()) {
                String lower = cleanName.toLowerCase();
                if (lower.endsWith(".png")) contentType = "image/png";
                else if (lower.endsWith(".webp")) contentType = "image/webp";
                else contentType = "image/jpeg";
            }

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(uploadEndpoint))
                    .timeout(Duration.ofSeconds(30))
                    .header("Authorization", "Bearer " + this.supabaseKey)
                    .header("apikey", this.supabaseKey)
                    .header("Content-Type", contentType)
                    .header("x-upsert", "true")
                    .POST(HttpRequest.BodyPublishers.ofByteArray(Files.readAllBytes(filePath)))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                log.info("Asset local synchronisé vers Supabase Storage : {}", publicUrl);
            } else {
                log.debug("Réponse Supabase Storage ({}) pour {} : {}", response.statusCode(), cleanName, response.body());
            }
        } catch (Exception e) {
            log.debug("Note synchronisation Supabase pour {} : {}", cleanName, e.getMessage());
        }
        return publicUrl;
    }

    /**
     * Téléverse un fichier vers Supabase Storage et retourne l'URL publique directe.
     *
     * @param file Fichier multipart envoyé depuis le client
     * @param folder Sous-dossier optionnel dans le bucket (ex: 'villes', 'ethnies', 'plats')
     * @return URL publique complète et permanente du fichier sur Supabase Storage
     */
    public String uploadFile(MultipartFile file, String folder) throws IOException, InterruptedException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Le fichier à téléverser est vide.");
        }

        init();

        String rawName = file.getOriginalFilename() != null ? file.getOriginalFilename() : "image.jpg";
        String cleanName = StringUtils.cleanPath(rawName).replaceAll("[^a-zA-Z0-9._-]", "_");

        String targetFolder = (folder != null && !folder.trim().isEmpty())
                ? folder.trim().replaceAll("[^a-zA-Z0-9_-]", "") + "/"
                : "";

        String uniqueFileName = targetFolder + UUID.randomUUID() + "_" + cleanName;

        if (!StringUtils.hasText(this.supabaseKey)) {
            throw new IllegalStateException("Clé Supabase Storage non configurée (variable SUPABASE_KEY absente).");
        }

        String baseUrl = this.supabaseUrl.replaceAll("/+$", "");
        String uploadEndpoint = baseUrl + "/storage/v1/object/" + this.bucket + "/" + uniqueFileName;

        String contentType = file.getContentType();
        if (contentType == null || contentType.isBlank()) {
            contentType = "application/octet-stream";
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uploadEndpoint))
                .timeout(Duration.ofSeconds(30))
                .header("Authorization", "Bearer " + this.supabaseKey)
                .header("apikey", this.supabaseKey)
                .header("Content-Type", contentType)
                .header("x-upsert", "true")
                .POST(HttpRequest.BodyPublishers.ofByteArray(file.getBytes()))
                .build();

        log.info("Téléversement vers Supabase Storage : {}", uploadEndpoint);
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        int statusCode = response.statusCode();
        if (statusCode >= 200 && statusCode < 300) {
            String publicUrl = baseUrl + "/storage/v1/object/public/" + this.bucket + "/" + uniqueFileName;
            log.info("Image téléversée avec succès sur Supabase. URL publique : {}", publicUrl);
            return publicUrl;
        } else {
            log.error("Échec du téléversement sur Supabase Storage. Statut : {}, Réponse : {}", statusCode, response.body());
            throw new RuntimeException("Erreur Supabase Storage (" + statusCode + ") : " + response.body());
        }
    }
}
