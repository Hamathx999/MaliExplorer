package com.maliexplorer_backend.serviceimpl;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import com.maliexplorer_backend.model.*;
import com.maliexplorer_backend.repository.utilisateurRepository;
import com.maliexplorer_backend.service.AuthService;
import com.maliexplorer_backend.dto.AuthResponseDTO;
import com.maliexplorer_backend.dto.LoginRequestDTO;
import com.maliexplorer_backend.dto.RegisterRequestDTO;
import com.maliexplorer_backend.exception.BadRequestException;
import com.maliexplorer_backend.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.sql.Date;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AuthServiceImpl implements AuthService {

    private final utilisateurRepository userRepository;

    @Override
    public AuthResponseDTO login(LoginRequestDTO requestDTO) {
        if (com.google.firebase.FirebaseApp.getApps().isEmpty()) {
            throw new BadRequestException("Le service d'authentification Firebase Admin n'est pas initialisé sur le serveur. Veuillez configurer firebase-service-account.json.");
        }
        try {
            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(requestDTO.getIdToken());
            String email = decodedToken.getEmail();
            String uid = decodedToken.getUid();
            String name = decodedToken.getName();
            String picture = decodedToken.getPicture();

            log.info("Connexion Firebase réussie pour email: {}, UID: {}", email, uid);

            Optional<utilisateurModel> userOpt = Optional.empty();
            if (StringUtils.hasText(email)) {
                userOpt = userRepository.findByEmail(email);
            }
            if (userOpt.isEmpty() && StringUtils.hasText(uid)) {
                userOpt = userRepository.findByFirebaseUid(uid);
            }

            utilisateurModel user;
            if (userOpt.isPresent()) {
                user = userOpt.get();
                if (!StringUtils.hasText(user.getFirebaseUid())) {
                    user.setFirebaseUid(uid);
                }
                if (StringUtils.hasText(picture) && !StringUtils.hasText(user.getPhotoUrl())) {
                    user.setPhotoUrl(picture);
                }
                user = userRepository.save(user);
            } else {
                String[] parts = (StringUtils.hasText(name) ? name.split(" ", 2)
                        : new String[] { "Utilisateur", "Firebase" });
                String prenom = parts[0];
                String nom = parts.length > 1 ? parts[1] : "MaliExplorer";

                user = utilisateurModel.builder()
                        .firebaseUid(uid)
                        .email(email != null ? email : uid + "@firebase.user")
                        .prenom(prenom)
                        .nom(nom)
                        .photoUrl(picture)
                        .role(RoleModel.touriste)
                        .dateCreation(new Date(System.currentTimeMillis()))
                        .build();

                user = userRepository.save(user);
                log.info("Nouvel utilisateur créé automatiquement via Firebase: {}", user.getEmail());
            }

            return mapToAuthResponseDTO(user, "Connexion réussie");
        } catch (Exception e) {
            log.error("Erreur lors de la validation du token Firebase : {}", e.getMessage());
            throw new BadRequestException("Token Firebase invalide ou expiré : " + e.getMessage());
        }
    }

    @Override
    public AuthResponseDTO register(RegisterRequestDTO requestDTO) {
        if (userRepository.existsByEmail(requestDTO.getEmail())) {
            throw new BadRequestException(
                    "Un utilisateur avec l'adresse email '" + requestDTO.getEmail() + "' existe déjà");
        }

        // 1. Validation du rôle demandé (par défaut: touriste)
        RoleModel roleDemande = requestDTO.getRole() != null ? requestDTO.getRole() : RoleModel.touriste;

        // 2. Sécurité : Blocage absolu des rôles d'administration à l'inscription publique
        if (roleDemande == RoleModel.admin || roleDemande == RoleModel.superAdmin) {
            log.warn("Tentative d'inscription non autorisée avec privilèges d'administrateur : Email={}", requestDTO.getEmail());
            throw new BadRequestException("L'inscription directe avec le rôle '" + roleDemande + "' est strictement interdite.");
        }

        String firebaseUid = null;
        String photoUrl = requestDTO.getPhotoUrl();
        if (StringUtils.hasText(requestDTO.getIdToken()) && !com.google.firebase.FirebaseApp.getApps().isEmpty()) {
            try {
                FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(requestDTO.getIdToken());
                firebaseUid = decodedToken.getUid();
                if (!StringUtils.hasText(photoUrl) && StringUtils.hasText(decodedToken.getPicture())) {
                    photoUrl = decodedToken.getPicture();
                }
            } catch (Exception e) {
                log.warn("Impossible de vérifier le idToken lors de l'inscription: {}", e.getMessage());
            }
        }

        utilisateurModel user;
        if (roleDemande == RoleModel.artisan) {
            ArtisanModel a = new ArtisanModel();
            a.setTypeArtisanat("Artisan");
            user = a;
        } else if (roleDemande == RoleModel.promoteur || roleDemande == RoleModel.partenaire) {
            PromoteurModel p = new PromoteurModel();
            p.setNomOrganisation("Organisation Partenaire");
            user = p;
        } else if (roleDemande == RoleModel.guide) {
            GuideModel g = new GuideModel();
            g.setLangue("Français");
            user = g;
        } else if (roleDemande == RoleModel.touriste) {
            user = new TouristeModel();
        } else {
            user = new utilisateurModel();
        }

        user.setFirebaseUid(firebaseUid);
        user.setPrenom(requestDTO.getPrenom());
        user.setNom(requestDTO.getNom());
        user.setEmail(requestDTO.getEmail());
        user.setAdresse(requestDTO.getAdresse());
        user.setPhotoUrl(photoUrl);
        user.setRole(roleDemande);
        user.setDateCreation(new Date(System.currentTimeMillis()));

        utilisateurModel saved = userRepository.save(user);
        log.info("Utilisateur inscrit avec succès: ID={}, Email={}, Role={}", saved.getIdUsers(), saved.getEmail(),
                saved.getRole());

        return mapToAuthResponseDTO(saved, "Inscription effectuée avec succès");
    }

    @Override
    @Transactional(readOnly = true)
    public AuthResponseDTO getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BadRequestException("Aucun utilisateur actuellement connecté");
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof utilisateurModel user) {
            return mapToAuthResponseDTO(user, "Profil récupéré avec succès");
        }

        if (principal instanceof String email) {
            utilisateurModel user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé pour l'email: " + email));
            return mapToAuthResponseDTO(user, "Profil récupéré avec succès");
        }

        throw new BadRequestException("Impossible d'identifier l'utilisateur connecté");
    }

    private AuthResponseDTO mapToAuthResponseDTO(utilisateurModel user, String message) {
        return AuthResponseDTO.builder()
                .idUsers(user.getIdUsers())
                .firebaseUid(user.getFirebaseUid())
                .prenom(user.getPrenom())
                .nom(user.getNom())
                .email(user.getEmail())
                .adresse(user.getAdresse())
                .photoUrl(user.getPhotoUrl())
                .role(user.getRole())
                .message(message)
                .build();
    }
}
