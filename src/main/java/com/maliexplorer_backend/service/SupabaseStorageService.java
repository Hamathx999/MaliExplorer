package com.maliexplorer_backend.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.UUID;

@Slf4j
@Service
public class SupabaseStorageService {

    @Value("${supabase.url:https://dzhqwkpwaljqsjwoqvso.supabase.co}")
    private String supabaseUrl;

    @Value("${supabase.key}")
    private String supabaseKey;

    @Value("${supabase.bucket:maliexplorer-media}")
    private String bucket;

    private final HttpClient httpClient;

    public SupabaseStorageService() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(15))
                .build();
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

        String rawName = file.getOriginalFilename() != null ? file.getOriginalFilename() : "image.jpg";
        String cleanName = StringUtils.cleanPath(rawName).replaceAll("[^a-zA-Z0-9._-]", "_");

        String targetFolder = (folder != null && !folder.trim().isEmpty())
                ? folder.trim().replaceAll("[^a-zA-Z0-9_-]", "") + "/"
                : "";

        String uniqueFileName = targetFolder + UUID.randomUUID() + "_" + cleanName;

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

