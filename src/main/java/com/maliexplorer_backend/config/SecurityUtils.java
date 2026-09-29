package com.maliexplorer_backend.config;

import com.maliexplorer_backend.model.utilisateurModel;
import com.maliexplorer_backend.repository.utilisateurRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public final class SecurityUtils {

    private SecurityUtils() {
    }

    /**
     * Récupère l'email de l'utilisateur actuellement authentifié via Firebase ou Spring Security.
     */
    public static Optional<String> getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            return Optional.empty();
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof utilisateurModel) {
            return Optional.ofNullable(((utilisateurModel) principal).getEmail());
        } else if (principal instanceof String) {
            return Optional.of((String) principal);
        }

        return Optional.empty();
    }

    /**
     * Récupère l'entité utilisateurModel de l'utilisateur connecté depuis la base de données.
     */
    public static Optional<utilisateurModel> getCurrentUser(utilisateurRepository repository) {
        return getCurrentUserEmail().flatMap(repository::findByEmail);
    }
}
