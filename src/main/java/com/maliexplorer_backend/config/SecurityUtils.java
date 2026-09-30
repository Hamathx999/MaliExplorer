package com.maliexplorer_backend.config;

import com.maliexplorer_backend.model.utilisateurModel;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Utilitaires pour récupérer l'utilisateur courant depuis le SecurityContext.
 * Utilisable depuis n'importe quel service ou controller.
 */
public final class SecurityUtils {

    private SecurityUtils() {
        // Classe utilitaire — pas d'instanciation
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
}
