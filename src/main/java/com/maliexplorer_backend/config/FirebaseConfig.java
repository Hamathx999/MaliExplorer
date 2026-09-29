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
            Resource resource = serviceAccountPath.startsWith("classpath:")
                    ? new ClassPathResource(serviceAccountPath.replace("classpath:", ""))
                    : new FileSystemResource(serviceAccountPath);

            FirebaseOptions options = null;

            if (resource.exists()) {
                byte[] content = resource.getInputStream().readAllBytes();
                String contentStr = new String(content, java.nio.charset.StandardCharsets.UTF_8);

                if (contentStr.contains("YOUR_PRIVATE_KEY")) {
                    log.warn(
                            "Le fichier '{}' contient une clé factice (YOUR_PRIVATE_KEY). Veuillez y coller votre vraie clé privée de compte de service Firebase téléchargée depuis la console Google.",
                            serviceAccountPath);
                } else {
                    try (InputStream is = new java.io.ByteArrayInputStream(content)) {
                        options = FirebaseOptions.builder()
                                .setCredentials(GoogleCredentials.fromStream(is))
                                .build();
                        log.info("Chargement des credentials Firebase depuis : {}", serviceAccountPath);
                    }
                }
            }

            if (options == null) {
                log.warn(
                        "Tentative d'initialisation Firebase avec les identifiants par défaut Google Application Credentials...");
                try {
                    options = FirebaseOptions.builder()
                            .setCredentials(GoogleCredentials.getApplicationDefault())
                            .build();
                } catch (Exception ex) {
                    log.warn(
                            "Identifiants par défaut Google non trouvés. Firebase démarrera en mode dégradé (placez votre 'firebase-service-account.json' valide pour activer l'authentification).");
                    return;
                }
            }

            FirebaseApp.initializeApp(options);
            log.info("Firebase Admin SDK initialisé avec succès.");
        } catch (Exception e) {
            log.warn(
                    "Notice Firebase Admin SDK : {}. Placez un fichier 'firebase-service-account.json' valide dans 'src/main/resources/' pour valider les tokens front-end.",
                    e.getMessage());
        }
    }
}
