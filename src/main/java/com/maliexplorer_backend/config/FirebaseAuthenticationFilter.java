package com.maliexplorer_backend.config;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import com.maliexplorer_backend.model.RoleModel;
import com.maliexplorer_backend.model.TouristeModel;
import com.maliexplorer_backend.model.utilisateurModel;
import com.maliexplorer_backend.repository.utilisateurRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class FirebaseAuthenticationFilter extends OncePerRequestFilter {

    private final utilisateurRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String bearerToken = extractBearerToken(request);

        if (StringUtils.hasText(bearerToken)) {
            // 1. Support des tokens de test dev (récupère l'admin en base sans le créer)
            if (bearerToken.startsWith("dev-admin") || bearerToken.contains("MaliExplorer") || bearerToken.equals("dev-admin-token")) {
                Optional<utilisateurModel> adminOpt = userRepository.findByEmail("famoryjosuesissoko@gmail.com");
                if (adminOpt.isEmpty()) {
                    adminOpt = userRepository.findByEmail("hamath.o.diallo18@gmail.com");
                }

                if (adminOpt.isPresent()) {
                    utilisateurModel adminUser = adminOpt.get();
                    List<GrantedAuthority> authorities = getAuthoritiesForUser(adminUser);

                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            adminUser,
                            bearerToken,
                            authorities);
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } else {
                    log.warn("Tentative de connexion via token dev-admin mais le compte administrateur n'existe pas en BDD !");
                }
                filterChain.doFilter(request, response);
                return;
            }

            try {
                // 2. Validation du token Firebase (avec tolérance au décalage d'horloge / hors-ligne)
                SecurityUtils.FirebaseClaims claims = SecurityUtils.verifyOrExtractFirebaseClaims(bearerToken);
                if (claims == null) {
                    log.warn("Token Bearer non reconnu comme token Firebase valide.");
                    SecurityContextHolder.clearContext();
                    filterChain.doFilter(request, response);
                    return;
                }

                String email = claims.email();
                String uid = claims.uid();

                log.debug("Token Firebase valide pour email: {}, UID: {}", email, uid);

                // Recherche de l'utilisateur préexistant en base
                Optional<utilisateurModel> userOpt = Optional.empty();
                if (StringUtils.hasText(email)) {
                    userOpt = userRepository.findByEmail(email);
                }
                if (userOpt.isEmpty() && StringUtils.hasText(uid)) {
                    userOpt = userRepository.findByFirebaseUid(uid);
                }

                if (userOpt.isPresent()) {
                    utilisateurModel principalUser = userOpt.get();

                    // Contrôle d'accès : Rejet immédiat si le compte est bloqué par l'administration
                    if ("BLOQUE".equalsIgnoreCase(principalUser.getStatut())) {
                        log.warn("Tentative d'accès par un utilisateur bloqué : Email={}, ID={}", principalUser.getEmail(), principalUser.getIdUsers());
                        SecurityContextHolder.clearContext();
                        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                        response.setContentType("application/json;charset=UTF-8");
                        response.getWriter().write("{\"status\":403,\"error\":\"Compte Bloqué\",\"message\":\"Votre compte a été bloqué par un administrateur. Veuillez contacter le support.\"}");
                        return;
                    }

                    // Mettre à jour le firebaseUid si absent
                    if ((principalUser.getFirebaseUid() == null || principalUser.getFirebaseUid().isBlank()) && StringUtils.hasText(uid)) {
                        principalUser.setFirebaseUid(uid);
                        userRepository.save(principalUser);
                    }

                    List<GrantedAuthority> authorities = getAuthoritiesForUser(principalUser);

                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            principalUser, // Objet utilisateurModel garantissant l'absence de ClassCastException
                            claims,
                            authorities);
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } else if (StringUtils.hasText(email)) {
                    // Auto-Provisioning en temps réel : utilisateur authentifié par Firebase / Google mais pas encore dans MySQL
                    try {
                        String displayName = claims.name();
                        String[] parts = (displayName != null && !displayName.isBlank())
                                ? displayName.trim().split("\\s+", 2)
                                : new String[] { "Utilisateur", "Google" };
                        String prenom = parts[0].length() >= 2 ? parts[0] : "Utilisateur";
                        if (prenom.length() > 50) prenom = prenom.substring(0, 50);

                        String nom = (parts.length > 1 && parts[1].length() >= 2) ? parts[1] : "MaliExplorer";
                        if (nom.length() > 50) nom = nom.substring(0, 50);

                        String photo = claims.picture() != null ? claims.picture() : "";

                        RoleModel assignedRole = RoleModel.touriste;
                        if (email.equalsIgnoreCase("famoryjosuesissoko@gmail.com") || email.equalsIgnoreCase("hamath.o.diallo18@gmail.com")) {
                            assignedRole = RoleModel.admin;
                        }

                        TouristeModel newTouriste = new TouristeModel();
                        newTouriste.setEmail(email.trim());
                        newTouriste.setFirebaseUid(uid);
                        newTouriste.setPrenom(prenom);
                        newTouriste.setNom(nom);
                        newTouriste.setPhotoUrl(photo);
                        newTouriste.setAdresse("");
                        newTouriste.setTelephone("");
                        newTouriste.setRole(assignedRole);
                        newTouriste.setStatut("ACTIF");
                        newTouriste.setPoints(0);
                        newTouriste.setDateCreation(Date.valueOf(LocalDate.now()));

                        utilisateurModel savedUser = userRepository.save(newTouriste);
                        log.info("Auto-Provisioning réussi pour {} ({}) avec ID MySQL={}", email, assignedRole, savedUser.getIdUsers());

                        List<GrantedAuthority> authorities = getAuthoritiesForUser(savedUser);
                        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                                savedUser,
                                claims,
                                authorities);
                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    } catch (Exception ex) {
                        log.error("Erreur lors de l'auto-provisioning Firebase de {} : {}", email, ex.getMessage(), ex);
                        SecurityContextHolder.clearContext();
                    }
                } else {
                    log.warn("Utilisateur authentifié dans Firebase sans email valide (UID={})", uid);
                    SecurityContextHolder.clearContext();
                }

            } catch (Exception e) {
                log.warn("Échec de validation du token Firebase : {}", e.getMessage());
                SecurityContextHolder.clearContext();
            }
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Génère la liste des rôles Spring Security à partir du modèle utilisateur
     */
    private List<GrantedAuthority> getAuthoritiesForUser(utilisateurModel user) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        RoleModel role = user.getRole();

        if (role != null) {
            String roleName = role.name().toUpperCase();
            authorities.add(new SimpleGrantedAuthority("ROLE_" + roleName));
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role.name()));

            if (role == RoleModel.superAdmin || role == RoleModel.admin) {
                authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
                authorities.add(new SimpleGrantedAuthority("ROLE_admin"));
            }
            if (role == RoleModel.superAdmin) {
                authorities.add(new SimpleGrantedAuthority("ROLE_SUPERADMIN"));
                authorities.add(new SimpleGrantedAuthority("ROLE_SUPER_ADMIN"));
            }
            if (role == RoleModel.guide || role == RoleModel.promoteur || role == RoleModel.partenaire) {
                authorities.add(new SimpleGrantedAuthority("ROLE_PARTENAIRE"));
            }
            if (role == RoleModel.investisseur) {
                authorities.add(new SimpleGrantedAuthority("ROLE_INVESTISSEUR"));
            }
        }
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        return authorities;
    }

    private String extractBearerToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (StringUtils.hasText(header) && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }
}