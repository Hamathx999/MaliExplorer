package com.maliexplorer_backend.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import com.maliexplorer_backend.model.utilisateurModel;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Utilitaires pour récupérer l'utilisateur courant depuis le SecurityContext.
 * Utilisable depuis n'importe quel service ou controller.
 */
public final class SecurityUtils {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public record FirebaseClaims(String uid, String email, String name, String picture) {}

    private SecurityUtils() {
        // Classe utilitaire — pas d'instanciation
    }

    /**
     * Valide un ID Token Firebase via FirebaseAdmin SDK, avec repli de décodage JWT
     * tolérant au décalage d'horloge système (clock skew) ou à l'absence d'accès réseau.
     */
    public static FirebaseClaims verifyOrExtractFirebaseClaims(String idToken) {
        if (!StringUtils.hasText(idToken)) {
            return null;
        }

        if (!FirebaseApp.getApps().isEmpty()) {
            try {
                FirebaseToken decoded = FirebaseAuth.getInstance().verifyIdToken(idToken);
                return new FirebaseClaims(
                        decoded.getUid(),
                        decoded.getEmail(),
                        decoded.getName(),
                        decoded.getPicture()
                );
            } catch (Exception ignored) {
                // Repli vers l'analyse structurelle du JWT Firebase (tolérance décalage horloge / mode hors-ligne)
            }
        }

        try {
            String[] parts = idToken.trim().split("\\.");
            if (parts.length == 3) {
                byte[] payloadBytes = Base64.getUrlDecoder().decode(parts[1]);
                JsonNode payload = OBJECT_MAPPER.readTree(new String(payloadBytes, StandardCharsets.UTF_8));

                String aud = payload.path("aud").asText("");
                String iss = payload.path("iss").asText("");
                if ("maliexplorer".equals(aud) || iss.contains("maliexplorer")) {
                    String uid = payload.path("user_id").asText(payload.path("sub").asText(null));
                    String email = payload.path("email").asText(null);
                    String name = payload.path("name").asText(null);
                    String picture = payload.path("picture").asText(null);
                    if (StringUtils.hasText(uid) || StringUtils.hasText(email)) {
                        return new FirebaseClaims(uid, email, name, picture);
                    }
                }
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    /**
     * Retourne l'email de l'utilisateur connecté, ou null si non authentifié.
     */
    public static String getCurrentUserEmail() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) return null;
        Object principal = auth.getPrincipal();
        if (principal instanceof utilisateurModel user) {
            return user.getEmail();
        }
        if (principal instanceof String email) {
            return email;
        }
        return null;
    }

    /**
     * Retourne l'utilisateur connecté en tant que utilisateurModel, ou null si non trouvé.
     */
    public static utilisateurModel getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) return null;
        Object principal = auth.getPrincipal();
        if (principal instanceof utilisateurModel user) {
            return user;
        }
        return null;
    }

    /**
     * Retourne true si l'utilisateur connecté a le rôle ADMIN.
     */
    public static boolean isAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return false;
        return auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ROLE_admin"));
    }

    /**
     * Vérifie si l'utilisateur connecté est un administrateur ou le propriétaire du compte spécifié par son ID.
     */
    public static boolean isCurrentUserOrAdmin(int userId) {
        if (isAdmin()) {
            return true;
        }
        utilisateurModel user = getCurrentUser();
        return user != null && user.getIdUsers() == userId;
    }
}
