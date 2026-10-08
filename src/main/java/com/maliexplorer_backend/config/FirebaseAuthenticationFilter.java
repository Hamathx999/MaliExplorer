package com.maliexplorer_backend.config;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import com.maliexplorer_backend.model.RoleModel;
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
                Optional<utilisateurModel> adminOpt = userRepository.findByEmail("hamath.o.diallo18@gmail.com");

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
                    log.warn("Tentative de connexion via token dev-admin mais le compte hamath.o.diallo@gmail.com n'existe pas en BDD !");
                }
                filterChain.doFilter(request, response);
                return;
            }

            try {
                // 2. Validation du token Firebase
                FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(bearerToken);
                String email = decodedToken.getEmail();
                String uid = decodedToken.getUid();

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
                    List<GrantedAuthority> authorities = getAuthoritiesForUser(principalUser);

                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            principalUser, // Objet utilisateurModel garantissant l'absence de ClassCastException
                            decodedToken,
                            authorities);
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } else {
                    log.warn("Utilisateur authentifié dans Firebase ({}) mais introuvable dans la base de données !", email != null ? email : uid);
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
            if (role == RoleModel.artisan || role == RoleModel.guide || role == RoleModel.promoteur || role == RoleModel.partenaire) {
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