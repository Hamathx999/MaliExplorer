package com.maliexplorer_backend.config;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import com.maliexplorer_backend.model.Role;
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
            try {
                FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(bearerToken);
                String email = decodedToken.getEmail();
                String uid = decodedToken.getUid();

                log.debug("Token Firebase valide pour email: {}, UID: {}", email, uid);

                Optional<utilisateurModel> userOpt = Optional.empty();
                if (StringUtils.hasText(email)) {
                    userOpt = userRepository.findByEmail(email);
                }
                if (userOpt.isEmpty() && StringUtils.hasText(uid)) {
                    userOpt = userRepository.findByFirebaseUid(uid);
                }

                List<GrantedAuthority> authorities = new ArrayList<>();
                utilisateurModel principalUser = null;

                if (userOpt.isPresent()) {
                    principalUser = userOpt.get();
                    Role role = principalUser.getRole();

                    if (role != null) {
                        String roleName = role.name().toUpperCase();
                        authorities.add(new SimpleGrantedAuthority("ROLE_" + roleName));
                        authorities.add(new SimpleGrantedAuthority("ROLE_" + role.name()));

                        if (role == Role.superAdmin || role == Role.admin) {
                            authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
                            authorities.add(new SimpleGrantedAuthority("ROLE_admin"));
                        }
                    }
                    authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
                } else {
                    // Utilisateur authentifié via Firebase mais pas encore synchronisé en base
                    // locale
                    authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
                    authorities.add(new SimpleGrantedAuthority("ROLE_TOURISME"));
                }

                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        principalUser != null ? principalUser : email,
                        decodedToken,
                        authorities);
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (Exception e) {
                log.warn("Échec de validation du token Firebase : {}", e.getMessage());
                SecurityContextHolder.clearContext();
            }
        }

        filterChain.doFilter(request, response);
    }

    private String extractBearerToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (StringUtils.hasText(header) && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }
}
