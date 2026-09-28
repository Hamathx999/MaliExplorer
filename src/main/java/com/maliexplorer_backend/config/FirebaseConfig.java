package com.maliexplorer_backend.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import java.io.InputStream;

@Slf4j
@Configuration
public class FirebaseConfig {

    @Value("${firebase.service-account.path:classpath:firebase-service-account.json}")
    private String serviceAccountPath;

    @PostConstruct
    public void initializeFirebase() {
        if (!FirebaseApp.getApps().isEmpty()) {
            log.info("FirebaseApp déjà initialisé.");
            return;
        }

        try {
            InputStream serviceAccountStream = null;

            if (serviceAccountPath.startsWith("classpath:")) {
                String resourcePath = serviceAccountPath.replace("classpath:", "");
                Resource resource = new ClassPathResource(resourcePath);
                if (resource.exists()) {
                    serviceAccountStream = resource.getInputStream();
                    log.info("Chargement des credentials Firebase depuis le classpath : {}", resourcePath);
                }
            } else {
                Resource resource = new FileSystemResource(serviceAccountPath);
                if (resource.exists()) {
                    serviceAccountStream = resource.getInputStream();
                    log.info("Chargement des credentials Firebase depuis le fichier : {}", serviceAccountPath);
                }
            }

            FirebaseOptions options;
            if (serviceAccountStream != null) {
                options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(serviceAccountStream))
                        .build();
            } else {
                log.warn(
                        "Aucun fichier de compte de service Firebase trouvé à '{}'. Tentative avec les identifiants par défaut Google Application...",
                        serviceAccountPath);
                options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.getApplicationDefault())
                        .build();
            }

            FirebaseApp.initializeApp(options);
            log.info("Firebase Admin SDK initialisé avec succès.");
        } catch (Exception e) {
            log.error(
                    "Impossible d'initialiser Firebase Admin SDK automatiquement : {}. Veuillez placer votre fichier 'firebase-service-account.json' dans 'src/main/resources/' ou configurer 'firebase.service-account.path'.",
                    e.getMessage());
        }
    }
}
