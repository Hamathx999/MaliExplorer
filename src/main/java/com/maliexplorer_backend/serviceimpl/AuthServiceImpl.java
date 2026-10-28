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
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import java.sql.Date;
import java.util.Optional;

@Slf4j
@Service
@Transactional
public class AuthServiceImpl implements AuthService {

    private final utilisateurRepository userRepository;
    private final com.maliexplorer_backend.repository.PromoteurRepository promoteurRepository;
    private final com.maliexplorer_backend.repository.GuideRepository guideRepository;
    @jakarta.persistence.PersistenceContext
    private jakarta.persistence.EntityManager entityManager;

    public AuthServiceImpl(utilisateurRepository userRepository) {
        this.userRepository = userRepository;
        this.promoteurRepository = null;
        this.guideRepository = null;
    }

    @org.springframework.beans.factory.annotation.Autowired
    public AuthServiceImpl(utilisateurRepository userRepository,
                           com.maliexplorer_backend.repository.PromoteurRepository promoteurRepository,
                           com.maliexplorer_backend.repository.GuideRepository guideRepository) {
        this.userRepository = userRepository;
        this.promoteurRepository = promoteurRepository;
        this.guideRepository = guideRepository;
    }

    @Override
    public AuthResponseDTO login(LoginRequestDTO requestDTO) {
        try {
            com.maliexplorer_backend.config.SecurityUtils.FirebaseClaims claims =
                    com.maliexplorer_backend.config.SecurityUtils.verifyOrExtractFirebaseClaims(requestDTO.getIdToken());
            if (claims == null) {
                throw new BadRequestException("Token Firebase invalide ou expiré.");
            }
            String email = claims.email();
            String uid = claims.uid();
            String name = claims.name();
            String picture = claims.picture();

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
                if ("BLOQUE".equalsIgnoreCase(user.getStatut())) {
                    log.warn("Tentative de connexion par un utilisateur bloqué : {}", user.getEmail());
                    throw new BadRequestException("Votre compte a été bloqué par un administrateur. Veuillez contacter le support.");
                }
                if (!StringUtils.hasText(user.getFirebaseUid())) {
                    user.setFirebaseUid(uid);
                }
                if (StringUtils.hasText(picture) && !StringUtils.hasText(user.getPhotoUrl())) {
                    user.setPhotoUrl(picture);
                }
                // Si l'utilisateur a déjà un rôle défini en base, on le respecte impérativement sans l'écraser
                user = userRepository.save(user);
            } else {
                String[] parts = (StringUtils.hasText(name) ? name.trim().split("\\s+", 2)
                        : new String[] { "Utilisateur", "Firebase" });
                String prenom = parts[0].length() >= 2 ? parts[0] : "Utilisateur";
                String nom = (parts.length > 1 && parts[1].length() >= 2) ? parts[1] : "MaliExplorer";

                RoleModel initialRole = RoleModel.touriste;
                if (email != null && (email.equalsIgnoreCase("famoryjosuesissoko@gmail.com") || email.equalsIgnoreCase("hamath.o.diallo18@gmail.com"))) {
                    initialRole = RoleModel.admin;
                }

                if (initialRole == RoleModel.touriste) {
                    com.maliexplorer_backend.model.TouristeModel touriste = new com.maliexplorer_backend.model.TouristeModel();
                    touriste.setFirebaseUid(uid);
                    touriste.setEmail(email != null ? email : uid + "@firebase.user");
                    touriste.setPrenom(prenom);
                    touriste.setNom(nom);
                    touriste.setPhotoUrl(picture != null ? picture : "");
                    touriste.setRole(RoleModel.touriste);
                    touriste.setStatut("ACTIF");
                    touriste.setDateCreation(new Date(System.currentTimeMillis()));
                    user = userRepository.save(touriste);
                } else {
                    user = utilisateurModel.builder()
                            .firebaseUid(uid)
                            .email(email != null ? email : uid + "@firebase.user")
                            .prenom(prenom)
                            .nom(nom)
                            .photoUrl(picture != null ? picture : "")
                            .role(initialRole)
                            .statut("ACTIF")
                            .dateCreation(new Date(System.currentTimeMillis()))
                            .build();
                    user = userRepository.save(user);
                }
                log.info("Nouvel utilisateur créé automatiquement via Firebase: {} avec rôle {}", user.getEmail(), user.getRole());
            }

            return mapToAuthResponseDTO(user, "Connexion réussie");
        } catch (Exception e) {
            log.error("Erreur lors de la validation du token Firebase : {}", e.getMessage());
            throw new BadRequestException("Token Firebase invalide ou expiré : " + e.getMessage());
        }
    }

    @Override
    public AuthResponseDTO register(RegisterRequestDTO requestDTO) {
        boolean isExisting = requestDTO.getEmail() != null && userRepository.existsByEmail(requestDTO.getEmail().trim());
        if (isExisting && !StringUtils.hasText(requestDTO.getIdToken())) {
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
        if (StringUtils.hasText(requestDTO.getIdToken())) {
            com.maliexplorer_backend.config.SecurityUtils.FirebaseClaims claims =
                    com.maliexplorer_backend.config.SecurityUtils.verifyOrExtractFirebaseClaims(requestDTO.getIdToken());
            if (claims != null) {
                firebaseUid = claims.uid();
                if (!StringUtils.hasText(photoUrl) && StringUtils.hasText(claims.picture())) {
                    photoUrl = claims.picture();
                }
            }
        }

        Optional<utilisateurModel> existingUserOpt = Optional.empty();
        if (isExisting && requestDTO.getEmail() != null) {
            existingUserOpt = userRepository.findByEmail(requestDTO.getEmail().trim());
        }
        if (existingUserOpt.isEmpty() && StringUtils.hasText(firebaseUid)) {
            existingUserOpt = userRepository.findByFirebaseUid(firebaseUid);
        }

        utilisateurModel saved;
        if (existingUserOpt.isPresent()) {
            utilisateurModel existing = existingUserOpt.get();
            if (existing.getRole() == RoleModel.admin || existing.getRole() == RoleModel.superAdmin) {
                log.warn("Tentative d'écrasement d'un compte administrateur : Email={}", existing.getEmail());
                throw new BadRequestException("Un compte administrateur existe déjà avec cet e-mail.");
            }

            log.info("Réconciliation course critique auth pour ID={}, Email={}, Ancien rôle={}, Rôle choisi={}",
                    existing.getIdUsers(), existing.getEmail(), existing.getRole(), roleDemande);

            if (roleDemande == RoleModel.promoteur || roleDemande == RoleModel.partenaire) {
                String orgName = StringUtils.hasText(requestDTO.getNomOrganisation())
                        ? requestDTO.getNomOrganisation().trim()
                        : "Organisation Partenaire";
                String piece = requestDTO.getPieceIdentite() != null ? requestDTO.getPieceIdentite().trim() : "";
                String tel = requestDTO.getTelephone() != null ? requestDTO.getTelephone().trim() : "";
                String desc = StringUtils.hasText(requestDTO.getDescription())
                        ? requestDTO.getDescription().trim()
                        : "Demande d'inscription et de partenariat culturel soumise depuis l'application mobile.";

                if (existing instanceof PromoteurModel p) {
                    p.setNomOrganisation(orgName);
                    p.setPieceIdentite(piece);
                    p.setTelephone(tel);
                    p.setRecherchePartenariat(true);
                    p.setTitreProjet("Demande d'adhésion : " + orgName);
                    p.setBesoinPartenariat(desc);
                    p.setStatutModeration(StatutModeration.EN_ATTENTE_VALIDATION);
                    p.setStatut("EN_ATTENTE");
                    p.setRole(RoleModel.promoteur);
                    if (StringUtils.hasText(requestDTO.getPrenom())) p.setPrenom(requestDTO.getPrenom().trim());
                    if (StringUtils.hasText(requestDTO.getNom())) p.setNom(requestDTO.getNom().trim());
                    if (StringUtils.hasText(requestDTO.getAdresse())) p.setAdresse(requestDTO.getAdresse().trim());
                    if (StringUtils.hasText(photoUrl)) p.setPhotoUrl(photoUrl);
                    if (StringUtils.hasText(firebaseUid)) p.setFirebaseUid(firebaseUid);
                    saved = promoteurRepository != null ? promoteurRepository.save(p) : userRepository.save(p);
                } else {
                    Date creationDate = existing.getDateCreation() != null ? existing.getDateCreation() : new Date(System.currentTimeMillis());
                    try {
                        userRepository.delete(existing);
                        userRepository.flush();

                        PromoteurModel p = new PromoteurModel();
                        p.setFirebaseUid(firebaseUid != null ? firebaseUid : existing.getFirebaseUid());
                        p.setEmail(requestDTO.getEmail().trim());
                        p.setPrenom(StringUtils.hasText(requestDTO.getPrenom()) ? requestDTO.getPrenom().trim() : existing.getPrenom());
                        p.setNom(StringUtils.hasText(requestDTO.getNom()) ? requestDTO.getNom().trim() : existing.getNom());
                        p.setAdresse(StringUtils.hasText(requestDTO.getAdresse()) ? requestDTO.getAdresse().trim() : existing.getAdresse());
                        p.setPhotoUrl(StringUtils.hasText(photoUrl) ? photoUrl : existing.getPhotoUrl());
                        p.setTelephone(tel);
                        p.setPieceIdentite(piece);
                        p.setNomOrganisation(orgName);
                        p.setRecherchePartenariat(true);
                        p.setTitreProjet("Demande d'adhésion : " + orgName);
                        p.setBesoinPartenariat(desc);
                        p.setStatutModeration(StatutModeration.EN_ATTENTE_VALIDATION);
                        p.setStatut("EN_ATTENTE");
                        p.setRole(RoleModel.promoteur);
                        p.setDateCreation(creationDate);
                        saved = promoteurRepository != null ? promoteurRepository.save(p) : userRepository.save(p);
                    } catch (Exception ex) {
                        log.warn("Suppression/recréation non applicable, mise à jour directe et liaison promoteurs: {}", ex.getMessage());
                        existing.setRole(RoleModel.promoteur);
                        existing.setStatut("EN_ATTENTE");
                        existing.setNomOrganisation(orgName);
                        existing.setPieceIdentite(piece);
                        existing.setTelephone(tel);
                        if (StringUtils.hasText(requestDTO.getPrenom())) existing.setPrenom(requestDTO.getPrenom().trim());
                        if (StringUtils.hasText(requestDTO.getNom())) existing.setNom(requestDTO.getNom().trim());
                        if (StringUtils.hasText(requestDTO.getAdresse())) existing.setAdresse(requestDTO.getAdresse().trim());
                        if (StringUtils.hasText(photoUrl)) existing.setPhotoUrl(photoUrl);
                        if (StringUtils.hasText(firebaseUid)) existing.setFirebaseUid(firebaseUid);
                        saved = userRepository.save(existing);
                        if (entityManager != null) {
                            try {
                                entityManager.createNativeQuery(
                                    "INSERT INTO promoteurs (id_users, nom_organisation, piece_identite, recherche_partenariat, titre_projet, besoin_partenariat, statut_moderation) " +
                                    "VALUES (:id, :org, :piece, :rech, :titre, :besoin, :statut) " +
                                    "ON DUPLICATE KEY UPDATE nom_organisation = :org, piece_identite = :piece, statut_moderation = :statut")
                                    .setParameter("id", saved.getIdUsers())
                                    .setParameter("org", orgName)
                                    .setParameter("piece", piece)
                                    .setParameter("rech", true)
                                    .setParameter("titre", "Demande d'adhésion : " + orgName)
                                    .setParameter("besoin", desc)
                                    .setParameter("statut", "EN_ATTENTE_VALIDATION")
                                    .executeUpdate();
                            } catch (Exception sqlEx) {
                                log.error("Erreur insertion native promoteurs: {}", sqlEx.getMessage());
                            }
                        }
                    }
                }
            } else if (roleDemande == RoleModel.guide) {
                String piece = requestDTO.getPieceIdentite() != null ? requestDTO.getPieceIdentite().trim() : "";
                String tel = requestDTO.getTelephone() != null ? requestDTO.getTelephone().trim() : "";
                String langue = StringUtils.hasText(requestDTO.getLangue()) ? requestDTO.getLangue().trim() : "Français";
                int exp = requestDTO.getExperience() != null ? requestDTO.getExperience() : 1;
                String descGuide = StringUtils.hasText(requestDTO.getDescription())
                        ? requestDTO.getDescription().trim()
                        : "Guide touristique professionnel inscrit depuis l'application mobile.";

                if (existing instanceof GuideModel g) {
                    g.setLangue(langue);
                    g.setExperience(exp);
                    g.setDescription(descGuide);
                    g.setPieceIdentite(piece);
                    g.setTelephone(tel);
                    g.setRecherchePartenariat(true);
                    g.setTitreProjet("Candidature Guide Touristique : " + requestDTO.getPrenom() + " " + requestDTO.getNom());
                    g.setBesoinPartenariat(descGuide);
                    g.setStatutModeration(StatutModeration.EN_ATTENTE_VALIDATION);
                    g.setStatut("EN_ATTENTE");
                    g.setRole(RoleModel.guide);
                    if (StringUtils.hasText(requestDTO.getPrenom())) g.setPrenom(requestDTO.getPrenom().trim());
                    if (StringUtils.hasText(requestDTO.getNom())) g.setNom(requestDTO.getNom().trim());
                    if (StringUtils.hasText(requestDTO.getAdresse())) g.setAdresse(requestDTO.getAdresse().trim());
                    if (StringUtils.hasText(photoUrl)) g.setPhotoUrl(photoUrl);
                    if (StringUtils.hasText(firebaseUid)) g.setFirebaseUid(firebaseUid);
                    saved = guideRepository != null ? guideRepository.save(g) : userRepository.save(g);
                } else {
                    Date creationDate = existing.getDateCreation() != null ? existing.getDateCreation() : new Date(System.currentTimeMillis());
                    try {
                        userRepository.delete(existing);
                        userRepository.flush();

                        GuideModel g = new GuideModel();
                        g.setFirebaseUid(firebaseUid != null ? firebaseUid : existing.getFirebaseUid());
                        g.setEmail(requestDTO.getEmail().trim());
                        g.setPrenom(StringUtils.hasText(requestDTO.getPrenom()) ? requestDTO.getPrenom().trim() : existing.getPrenom());
                        g.setNom(StringUtils.hasText(requestDTO.getNom()) ? requestDTO.getNom().trim() : existing.getNom());
                        g.setAdresse(StringUtils.hasText(requestDTO.getAdresse()) ? requestDTO.getAdresse().trim() : existing.getAdresse());
                        g.setPhotoUrl(StringUtils.hasText(photoUrl) ? photoUrl : existing.getPhotoUrl());
                        g.setTelephone(tel);
                        g.setPieceIdentite(piece);
                        g.setLangue(langue);
                        g.setExperience(exp);
                        g.setDescription(descGuide);
                        g.setRecherchePartenariat(true);
                        g.setTitreProjet("Candidature Guide Touristique : " + g.getPrenom() + " " + g.getNom());
                        g.setBesoinPartenariat(descGuide);
                        g.setStatutModeration(StatutModeration.EN_ATTENTE_VALIDATION);
                        g.setStatut("EN_ATTENTE");
                        g.setRole(RoleModel.guide);
                        g.setDateCreation(creationDate);
                        saved = guideRepository != null ? guideRepository.save(g) : userRepository.save(g);
                    } catch (Exception ex) {
                        log.warn("Suppression/recréation guide non applicable, mise à jour directe: {}", ex.getMessage());
                        existing.setRole(RoleModel.guide);
                        existing.setStatut("EN_ATTENTE");
                        existing.setPieceIdentite(piece);
                        existing.setTelephone(tel);
                        if (StringUtils.hasText(requestDTO.getPrenom())) existing.setPrenom(requestDTO.getPrenom().trim());
                        if (StringUtils.hasText(requestDTO.getNom())) existing.setNom(requestDTO.getNom().trim());
                        if (StringUtils.hasText(requestDTO.getAdresse())) existing.setAdresse(requestDTO.getAdresse().trim());
                        if (StringUtils.hasText(photoUrl)) existing.setPhotoUrl(photoUrl);
                        if (StringUtils.hasText(firebaseUid)) existing.setFirebaseUid(firebaseUid);
                        saved = userRepository.save(existing);
                        if (entityManager != null) {
                            try {
                                entityManager.createNativeQuery(
                                    "INSERT INTO guides (id_users, langue, experience, description, piece_identite, recherche_partenariat, titre_projet, besoin_partenariat, statut_moderation) " +
                                    "VALUES (:id, :langue, :exp, :desc, :piece, :rech, :titre, :besoin, :statut) " +
                                    "ON DUPLICATE KEY UPDATE langue = :langue, experience = :exp, piece_identite = :piece, statut_moderation = :statut")
                                    .setParameter("id", saved.getIdUsers())
                                    .setParameter("langue", langue)
                                    .setParameter("exp", exp)
                                    .setParameter("desc", descGuide)
                                    .setParameter("piece", piece)
                                    .setParameter("rech", true)
                                    .setParameter("titre", "Candidature Guide : " + saved.getPrenom())
                                    .setParameter("besoin", descGuide)
                                    .setParameter("statut", "EN_ATTENTE_VALIDATION")
                                    .executeUpdate();
                            } catch (Exception sqlEx) {
                                log.error("Erreur insertion native guides: {}", sqlEx.getMessage());
                            }
                        }
                    }
                }
            } else {
                // Touriste ou autre
                if (StringUtils.hasText(requestDTO.getPrenom())) existing.setPrenom(requestDTO.getPrenom().trim());
                if (StringUtils.hasText(requestDTO.getNom())) existing.setNom(requestDTO.getNom().trim());
                if (StringUtils.hasText(requestDTO.getAdresse())) existing.setAdresse(requestDTO.getAdresse().trim());
                if (StringUtils.hasText(requestDTO.getTelephone())) existing.setTelephone(requestDTO.getTelephone().trim());
                if (StringUtils.hasText(photoUrl)) existing.setPhotoUrl(photoUrl);
                if (StringUtils.hasText(firebaseUid)) existing.setFirebaseUid(firebaseUid);
                existing.setRole(roleDemande);
                saved = userRepository.save(existing);
            }
        } else {
            // Nouvel utilisateur
            utilisateurModel user;
            if (roleDemande == RoleModel.promoteur || roleDemande == RoleModel.partenaire) {
                PromoteurModel p = new PromoteurModel();
                String orgName = StringUtils.hasText(requestDTO.getNomOrganisation())
                        ? requestDTO.getNomOrganisation().trim()
                        : "Organisation Partenaire";
                p.setNomOrganisation(orgName);
                p.setPieceIdentite(requestDTO.getPieceIdentite());
                p.setTelephone(requestDTO.getTelephone());
                p.setRecherchePartenariat(true);
                p.setTitreProjet("Demande d'adhésion : " + orgName);
                p.setBesoinPartenariat(StringUtils.hasText(requestDTO.getDescription())
                        ? requestDTO.getDescription().trim()
                        : "Demande d'inscription et de partenariat culturel soumise depuis l'application mobile.");
                p.setStatutModeration(StatutModeration.EN_ATTENTE_VALIDATION);
                p.setStatut("EN_ATTENTE");
                user = p;
                user.setNomOrganisation(orgName);
                user.setPieceIdentite(requestDTO.getPieceIdentite());
            } else if (roleDemande == RoleModel.guide) {
                GuideModel g = new GuideModel();
                g.setLangue(StringUtils.hasText(requestDTO.getLangue()) ? requestDTO.getLangue().trim() : "Français");
                g.setExperience(requestDTO.getExperience() != null ? requestDTO.getExperience() : 1);
                String descGuide = StringUtils.hasText(requestDTO.getDescription())
                        ? requestDTO.getDescription().trim()
                        : "Guide touristique professionnel inscrit depuis l'application mobile.";
                g.setDescription(descGuide);
                g.setPieceIdentite(requestDTO.getPieceIdentite());
                g.setTelephone(requestDTO.getTelephone());
                g.setRecherchePartenariat(true);
                g.setTitreProjet("Candidature Guide Touristique : " + requestDTO.getPrenom() + " " + requestDTO.getNom());
                g.setBesoinPartenariat(descGuide);
                g.setStatutModeration(StatutModeration.EN_ATTENTE_VALIDATION);
                g.setStatut("EN_ATTENTE");
                user = g;
            } else if (roleDemande == RoleModel.touriste) {
                user = new TouristeModel();
                user.setTelephone(requestDTO.getTelephone());
            } else {
                user = new utilisateurModel();
                user.setTelephone(requestDTO.getTelephone());
            }

            user.setFirebaseUid(firebaseUid);
            user.setPrenom(requestDTO.getPrenom());
            user.setNom(requestDTO.getNom());
            user.setEmail(requestDTO.getEmail());
            user.setAdresse(requestDTO.getAdresse());
            user.setPhotoUrl(photoUrl);
            user.setRole(roleDemande);
            user.setDateCreation(new Date(System.currentTimeMillis()));

            saved = (roleDemande == RoleModel.promoteur && promoteurRepository != null)
                    ? promoteurRepository.save((PromoteurModel) user)
                    : ((roleDemande == RoleModel.guide && guideRepository != null)
                            ? guideRepository.save((GuideModel) user)
                            : userRepository.save(user));
        }

        log.info("Utilisateur inscrit avec succès: ID={}, Email={}, Role={}", saved.getIdUsers(), saved.getEmail(),
                saved.getRole());

        return mapToAuthResponseDTO(saved, "Inscription effectuée avec succès");
    }

    @Override
    @Transactional(readOnly = true)
    public AuthResponseDTO getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token d'authentification manquant ou session expirée");
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof utilisateurModel user) {
            return mapToAuthResponseDTO(user, "Profil récupéré avec succès");
        }

        if (principal instanceof String email) {
            utilisateurModel user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Utilisateur introuvable pour la session courante"));
            return mapToAuthResponseDTO(user, "Profil récupéré avec succès");
        }

        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Impossible d'identifier l'utilisateur connecté");
    }

    private AuthResponseDTO mapToAuthResponseDTO(utilisateurModel user, String message) {
        StatutModeration statutModeration = null;
        String nomOrg = user.getNomOrganisation();
        String pieceId = user.getPieceIdentite();
        String tel = user.getTelephone();
        String statut = user.getStatut();

        if (user.getRole() == RoleModel.promoteur || user.getRole() == RoleModel.partenaire) {
            if (promoteurRepository != null) {
                Optional<PromoteurModel> pOpt = promoteurRepository.findById(user.getIdUsers());
                if (pOpt.isPresent()) {
                    PromoteurModel p = pOpt.get();
                    statutModeration = p.getStatutModeration();
                    if (StringUtils.hasText(p.getNomOrganisation())) nomOrg = p.getNomOrganisation();
                    if (StringUtils.hasText(p.getPieceIdentite())) pieceId = p.getPieceIdentite();
                }
            }
            if (statutModeration == null && user instanceof PromoteurModel p) {
                statutModeration = p.getStatutModeration();
                if (StringUtils.hasText(p.getNomOrganisation())) nomOrg = p.getNomOrganisation();
                if (StringUtils.hasText(p.getPieceIdentite())) pieceId = p.getPieceIdentite();
            }
            if (statutModeration == null) {
                statutModeration = StatutModeration.VALIDE;
            }
        } else if (user.getRole() == RoleModel.guide) {
            if (guideRepository != null) {
                Optional<GuideModel> gOpt = guideRepository.findById(user.getIdUsers());
                if (gOpt.isPresent()) {
                    GuideModel g = gOpt.get();
                    statutModeration = g.getStatutModeration();
                    if (StringUtils.hasText(g.getPieceIdentite())) pieceId = g.getPieceIdentite();
                }
            }
            if (statutModeration == null && user instanceof GuideModel g) {
                statutModeration = g.getStatutModeration();
                if (StringUtils.hasText(g.getPieceIdentite())) pieceId = g.getPieceIdentite();
            }
            if (statutModeration == null) {
                statutModeration = StatutModeration.VALIDE;
            }
        } else if (user.getRole() == RoleModel.admin || user.getRole() == RoleModel.superAdmin) {
            statutModeration = StatutModeration.VALIDE;
        }

        return AuthResponseDTO.builder()
                .idUsers(user.getIdUsers())
                .firebaseUid(user.getFirebaseUid())
                .prenom(user.getPrenom())
                .nom(user.getNom())
                .email(user.getEmail())
                .adresse(user.getAdresse())
                .telephone(tel)
                .pieceIdentite(pieceId)
                .nomOrganisation(nomOrg)
                .photoUrl(user.getPhotoUrl())
                .statut(statut)
                .statutModeration(statutModeration)
                .role(user.getRole())
                .message(message)
                .build();
    }
}
