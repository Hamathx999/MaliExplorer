package com.maliexplorer_backend.serviceimpl;

import com.maliexplorer_backend.exception.ResourceNotFoundException;
import com.maliexplorer_backend.model.EvenementModel;
import com.maliexplorer_backend.model.RoleModel;
import com.maliexplorer_backend.model.StatutModeration;
import com.maliexplorer_backend.model.utilisateurModel;
import com.maliexplorer_backend.repository.EvenementRepository;
import com.maliexplorer_backend.repository.utilisateurRepository;
import com.maliexplorer_backend.service.UtilisateurService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@Transactional
public class UtilisateurServiceImpl implements UtilisateurService {

    private final utilisateurRepository repository;
    private final EvenementRepository evenementRepository;

    @PersistenceContext
    private EntityManager entityManager;

    public UtilisateurServiceImpl(utilisateurRepository repository) {
        this.repository = repository;
        this.evenementRepository = null;
    }

    @Autowired
    public UtilisateurServiceImpl(utilisateurRepository repository, EvenementRepository evenementRepository) {
        this.repository = repository;
        this.evenementRepository = evenementRepository;
    }

    /**
     * CREATE : Créer un nouvel utilisateur
     */
    @Override
    public utilisateurModel creerUtilisateur(utilisateurModel utilisateur) {
        if (repository.existsByEmail(utilisateur.getEmail())) {
            throw new IllegalArgumentException("Cet email est déjà utilisé !");
        }

        if (utilisateur.getDateCreation() == null) {
            utilisateur.setDateCreation(Date.valueOf(LocalDate.now()));
        }
        if (utilisateur.getRole() == null) {
            utilisateur.setRole(RoleModel.touriste);
        }

        utilisateurModel saved = repository.saveAndFlush(utilisateur);
        int id = saved.getIdUsers();

        StatutModeration statutInitial = utilisateur.getStatutModeration() != null
                ? utilisateur.getStatutModeration()
                : StatutModeration.VALIDE;

        if (entityManager != null) {
            entityManager.clear();
            synchroniserTableRole(id, saved.getRole(), utilisateur, saved, statutInitial);
            creerEvenementInitialSiFourni(utilisateur, saved);
            entityManager.clear();
            return obtenirUtilisateurParId(id);
        }

        return saved;
    }

    /**
     * READ ALL : Obtenir la liste de tous les utilisateurs
     */
    @Override
    public List<utilisateurModel> obtenirTousLesUtilisateurs() {
        return repository.findAll();
    }

    /**
     * READ ONE : Obtenir un utilisateur par son identifiant
     */
    @Override
    public utilisateurModel obtenirUtilisateurParId(int id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("utilisateurModel", "id", id));
    }

    /**
     * UPDATE : Mettre à jour un utilisateur existant (y compris changement de rôle Guide <-> Promoteur <-> Touriste)
     */
    @Override
    public utilisateurModel mettreAJourUtilisateur(int id, utilisateurModel utilisateurModifie) {
        utilisateurModel existant = obtenirUtilisateurParId(id);

        if (utilisateurModifie.getEmail() != null
                && !existant.getEmail().equalsIgnoreCase(utilisateurModifie.getEmail())
                && repository.existsByEmail(utilisateurModifie.getEmail())) {
            throw new IllegalArgumentException("Cet email est déjà utilisé par un autre compte !");
        }

        String prenom = utilisateurModifie.getPrenom() != null && !utilisateurModifie.getPrenom().isBlank()
                ? utilisateurModifie.getPrenom().trim() : existant.getPrenom();
        String nom = utilisateurModifie.getNom() != null && !utilisateurModifie.getNom().isBlank()
                ? utilisateurModifie.getNom().trim() : existant.getNom();
        String email = utilisateurModifie.getEmail() != null && !utilisateurModifie.getEmail().isBlank()
                ? utilisateurModifie.getEmail().trim() : existant.getEmail();
        String adresse = utilisateurModifie.getAdresse() != null
                ? utilisateurModifie.getAdresse().trim() : (existant.getAdresse() != null ? existant.getAdresse() : "");
        String telephone = utilisateurModifie.getTelephone() != null
                ? utilisateurModifie.getTelephone().trim() : (existant.getTelephone() != null ? existant.getTelephone() : "");
        String photoUrl = utilisateurModifie.getPhotoUrl() != null && !utilisateurModifie.getPhotoUrl().isBlank()
                ? utilisateurModifie.getPhotoUrl().trim() : (existant.getPhotoUrl() != null ? existant.getPhotoUrl() : "");
        RoleModel targetRole = utilisateurModifie.getRole() != null ? utilisateurModifie.getRole() : existant.getRole();
        String statut = (utilisateurModifie.getStatut() != null && !utilisateurModifie.getStatut().isBlank())
                ? utilisateurModifie.getStatut().trim() : (existant.getStatut() != null ? existant.getStatut() : "ACTIF");
        int points = utilisateurModifie.getPoints() > 0 ? utilisateurModifie.getPoints() : existant.getPoints();

        // Déterminer le statut de modération à conserver (ne jamais rétrograder un compte déjà VALIDE)
        StatutModeration statutModeration = resoudreStatutModeration(id, existant, utilisateurModifie);

        if (entityManager == null) {
            existant.setPrenom(prenom);
            existant.setNom(nom);
            existant.setEmail(email);
            existant.setAdresse(adresse);
            existant.setTelephone(telephone);
            existant.setPhotoUrl(photoUrl);
            existant.setRole(targetRole);
            existant.setStatut(statut);
            existant.setPoints(points);
            return repository.save(existant);
        }

        // Détacher l'entité existante du PersistenceContext avant de manipuler les tables JOINED en SQL natif
        entityManager.clear();

        // 1. Mise à jour directe de la table parente utilisateurs
        entityManager.createNativeQuery(
                "UPDATE utilisateurs SET prenom = :prenom, nom = :nom, email = :email, " +
                "adresse = :adresse, telephone = :tel, photo_url = :photo, " +
                "role = :role, statut = :statut, points = :points WHERE id_users = :id")
                .setParameter("prenom", prenom)
                .setParameter("nom", nom)
                .setParameter("email", email)
                .setParameter("adresse", adresse)
                .setParameter("tel", telephone)
                .setParameter("photo", photoUrl)
                .setParameter("role", targetRole != null ? targetRole.name() : RoleModel.touriste.name())
                .setParameter("statut", statut)
                .setParameter("points", points)
                .setParameter("id", id)
                .executeUpdate();

        // 2. Synchronisation exclusive des tables enfants JOINED selon le nouveau rôle
        synchroniserTableRole(id, targetRole, utilisateurModifie, existant, statutModeration);

        // 3. Si un événement initial est renseigné dans le formulaire Promoteur, l'enregistrer
        creerEvenementInitialSiFourni(utilisateurModifie, existant);

        // 4. Vider le cache L1 pour recharger proprement la nouvelle sous-classe JPA
        entityManager.clear();
        return obtenirUtilisateurParId(id);
    }

    private StatutModeration resoudreStatutModeration(int id, utilisateurModel existant, utilisateurModel modifie) {
        if (modifie.getStatutModeration() != null) {
            return modifie.getStatutModeration();
        }
        if (existant.getStatutModeration() == StatutModeration.VALIDE) {
            return StatutModeration.VALIDE;
        }
        if (entityManager != null) {
            try {
                List<?> statutsGuide = entityManager.createNativeQuery(
                        "SELECT statut_moderation FROM guides WHERE id_users = :id")
                        .setParameter("id", id).getResultList();
                for (Object s : statutsGuide) {
                    if (s != null && ("VALIDE".equalsIgnoreCase(s.toString()) || "APPROUVE".equalsIgnoreCase(s.toString()))) {
                        return StatutModeration.VALIDE;
                    }
                }
                List<?> statutsPromoteur = entityManager.createNativeQuery(
                        "SELECT statut_moderation FROM promoteurs WHERE id_users = :id")
                        .setParameter("id", id).getResultList();
                for (Object s : statutsPromoteur) {
                    if (s != null && ("VALIDE".equalsIgnoreCase(s.toString()) || "APPROUVE".equalsIgnoreCase(s.toString()))) {
                        return StatutModeration.VALIDE;
                    }
                }
            } catch (Exception e) {
                log.warn("Lecture statut_moderation existant: {}", e.getMessage());
            }
        }
        return existant.getStatutModeration() != null ? existant.getStatutModeration() : StatutModeration.VALIDE;
    }

    private void nettoyerTablesTouriste(int id) {
        entityManager.createNativeQuery("DELETE FROM touriste_article WHERE id_touriste = :id").setParameter("id", id).executeUpdate();
        entityManager.createNativeQuery("DELETE FROM touriste_ethnie WHERE id_touriste = :id").setParameter("id", id).executeUpdate();
        entityManager.createNativeQuery("DELETE FROM touriste_lieu_historique WHERE id_touriste = :id").setParameter("id", id).executeUpdate();
        entityManager.createNativeQuery("DELETE FROM touriste_plat WHERE id_touriste = :id").setParameter("id", id).executeUpdate();
        entityManager.createNativeQuery("DELETE FROM touriste_quiz WHERE id_touriste = :id").setParameter("id", id).executeUpdate();
        entityManager.createNativeQuery("DELETE FROM touriste_ville WHERE id_touriste = :id").setParameter("id", id).executeUpdate();
        entityManager.createNativeQuery("DELETE FROM touristes WHERE id_users = :id").setParameter("id", id).executeUpdate();
    }

    private void synchroniserTableRole(
            int id,
            RoleModel targetRole,
            utilisateurModel modifie,
            utilisateurModel existant,
            StatutModeration statutModeration) {

        String piece = (modifie.getPieceIdentite() != null && !modifie.getPieceIdentite().isBlank())
                ? modifie.getPieceIdentite().trim()
                : (existant.getPieceIdentite() != null ? existant.getPieceIdentite() : "");

        String nomVal = (modifie.getNom() != null && !modifie.getNom().isBlank()) ? modifie.getNom().trim() : existant.getNom();
        String statutModStr = (statutModeration != null ? statutModeration : StatutModeration.VALIDE).name();

        if (targetRole == RoleModel.promoteur || targetRole == RoleModel.partenaire) {
            String org = (modifie.getNomOrganisation() != null && !modifie.getNomOrganisation().isBlank())
                    ? modifie.getNomOrganisation().trim()
                    : (existant.getNomOrganisation() != null && !existant.getNomOrganisation().isBlank()
                            ? existant.getNomOrganisation().trim()
                            : "Organisation " + nomVal);

            String titreProjet = (modifie.getNomEvenement() != null && !modifie.getNomEvenement().isBlank())
                    ? modifie.getNomEvenement().trim()
                    : null;
            String besoin = (modifie.getDescriptionEvenement() != null && !modifie.getDescriptionEvenement().isBlank())
                    ? modifie.getDescriptionEvenement().trim()
                    : ((modifie.getDescription() != null && !modifie.getDescription().isBlank())
                            ? modifie.getDescription().trim() : null);

            nettoyerTablesTouriste(id);
            entityManager.createNativeQuery("DELETE FROM guides WHERE id_users = :id").setParameter("id", id).executeUpdate();
            entityManager.createNativeQuery("DELETE FROM administrateurs WHERE id_users = :id").setParameter("id", id).executeUpdate();
            entityManager.createNativeQuery("DELETE FROM artisans WHERE id_users = :id").setParameter("id", id).executeUpdate();

            entityManager.createNativeQuery(
                    "INSERT INTO promoteurs (id_users, nom_organisation, piece_identite, recherche_partenariat, statut_moderation, titre_projet, besoin_partenariat, motif_rejet) " +
                    "VALUES (:id, :org, :piece, :rech, :statut, :titre, :besoin, NULL) " +
                    "ON DUPLICATE KEY UPDATE nom_organisation = :org, piece_identite = :piece, statut_moderation = :statut, motif_rejet = NULL" +
                    (titreProjet != null ? ", titre_projet = :titre" : "") +
                    (besoin != null ? ", besoin_partenariat = :besoin" : ""))
                    .setParameter("id", id)
                    .setParameter("org", org)
                    .setParameter("piece", piece)
                    .setParameter("rech", titreProjet != null)
                    .setParameter("statut", statutModStr)
                    .setParameter("titre", titreProjet)
                    .setParameter("besoin", besoin)
                    .executeUpdate();

        } else if (targetRole == RoleModel.guide) {
            String langue = (modifie.getLangues() != null && !modifie.getLangues().isBlank())
                    ? modifie.getLangues().trim()
                    : (existant.getLangues() != null && !existant.getLangues().isBlank()
                            ? existant.getLangues().trim()
                            : "Français, Bambara");
            int exp = modifie.getExperience() > 0 ? modifie.getExperience()
                    : (existant.getExperience() > 0 ? existant.getExperience() : 1);
            String desc = (modifie.getDescription() != null && !modifie.getDescription().isBlank())
                    ? modifie.getDescription().trim()
                    : (existant.getDescription() != null ? existant.getDescription() : null);

            nettoyerTablesTouriste(id);
            entityManager.createNativeQuery("DELETE FROM promoteurs WHERE id_users = :id").setParameter("id", id).executeUpdate();
            entityManager.createNativeQuery("DELETE FROM administrateurs WHERE id_users = :id").setParameter("id", id).executeUpdate();
            entityManager.createNativeQuery("DELETE FROM artisans WHERE id_users = :id").setParameter("id", id).executeUpdate();

            entityManager.createNativeQuery(
                    "INSERT INTO guides (id_users, piece_identite, recherche_partenariat, statut_moderation, experience, langue, description, motif_rejet) " +
                    "VALUES (:id, :piece, 0, :statut, :exp, :langue, :desc, NULL) " +
                    "ON DUPLICATE KEY UPDATE piece_identite = :piece, statut_moderation = :statut, experience = :exp, langue = :langue, motif_rejet = NULL" +
                    (desc != null ? ", description = :desc" : ""))
                    .setParameter("id", id)
                    .setParameter("piece", piece)
                    .setParameter("statut", statutModStr)
                    .setParameter("exp", exp)
                    .setParameter("langue", langue)
                    .setParameter("desc", desc)
                    .executeUpdate();

        } else if (targetRole == RoleModel.touriste) {
            nettoyerTablesTouriste(id);
            entityManager.createNativeQuery("DELETE FROM promoteurs WHERE id_users = :id").setParameter("id", id).executeUpdate();
            entityManager.createNativeQuery("DELETE FROM guides WHERE id_users = :id").setParameter("id", id).executeUpdate();
            entityManager.createNativeQuery("DELETE FROM administrateurs WHERE id_users = :id").setParameter("id", id).executeUpdate();
            entityManager.createNativeQuery("DELETE FROM artisans WHERE id_users = :id").setParameter("id", id).executeUpdate();
        } else if (targetRole == RoleModel.admin || targetRole == RoleModel.superAdmin) {
            nettoyerTablesTouriste(id);
            entityManager.createNativeQuery("DELETE FROM promoteurs WHERE id_users = :id").setParameter("id", id).executeUpdate();
            entityManager.createNativeQuery("DELETE FROM guides WHERE id_users = :id").setParameter("id", id).executeUpdate();
            entityManager.createNativeQuery("DELETE FROM artisans WHERE id_users = :id").setParameter("id", id).executeUpdate();
        }
    }

    private void creerEvenementInitialSiFourni(utilisateurModel modifie, utilisateurModel existant) {
        if (evenementRepository == null) return;
        String nomEvt = modifie.getNomEvenement();
        if (nomEvt == null || nomEvt.isBlank()) return;

        String email = (modifie.getEmail() != null && !modifie.getEmail().isBlank()) ? modifie.getEmail().trim() : existant.getEmail();
        String org = (modifie.getNomOrganisation() != null && !modifie.getNomOrganisation().isBlank())
                ? modifie.getNomOrganisation().trim()
                : (existant.getNomOrganisation() != null ? existant.getNomOrganisation() : existant.getPrenom() + " " + existant.getNom());
        String tel = (modifie.getTelephone() != null && !modifie.getTelephone().isBlank()) ? modifie.getTelephone().trim() : existant.getTelephone();
        String adresse = (modifie.getAdresse() != null && !modifie.getAdresse().isBlank()) ? modifie.getAdresse().trim() : "Bamako";
        String affiche = (modifie.getPhotosEvenement() != null && !modifie.getPhotosEvenement().isEmpty())
                ? modifie.getPhotosEvenement().get(0) : null;

        List<EvenementModel> existants = evenementRepository.findByEmailOrganisateur(email);
        boolean dejaCree = existants.stream().anyMatch(e -> nomEvt.trim().equalsIgnoreCase(e.getTitre()));
        if (!dejaCree) {
            EvenementModel evt = EvenementModel.builder()
                    .titre(nomEvt.trim())
                    .description(modifie.getDescriptionEvenement() != null ? modifie.getDescriptionEvenement().trim() : "")
                    .dateDebut(modifie.getDateEvenement() != null && !modifie.getDateEvenement().isBlank() ? modifie.getDateEvenement().trim() : LocalDate.now().toString())
                    .heureDebut("09:00")
                    .lieu(adresse)
                    .ville(adresse)
                    .region("District de Bamako")
                    .categorie("Festival & Événement culturel")
                    .prix("Gratuit")
                    .nomOrganisateur(org)
                    .emailOrganisateur(email)
                    .telephoneOrganisateur(tel)
                    .imageUrl(affiche)
                    .afficheUrl(affiche)
                    .statut(EvenementModel.Statut.VALIDE)
                    .dateSoumission(LocalDateTime.now())
                    .build();
            evenementRepository.save(evt);
        }
    }

    /**
     * DELETE : Supprimer un utilisateur par son identifiant et cascader proprement
     */
    @Override
    public void supprimerUtilisateur(int id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("utilisateurModel", "id", id);
        }

        // 1. Tables enfants de touristes (liaisons N-N)
        entityManager.createNativeQuery("DELETE FROM touriste_article WHERE id_touriste = :id").setParameter("id", id).executeUpdate();
        entityManager.createNativeQuery("DELETE FROM touriste_ethnie WHERE id_touriste = :id").setParameter("id", id).executeUpdate();
        entityManager.createNativeQuery("DELETE FROM touriste_lieu_historique WHERE id_touriste = :id").setParameter("id", id).executeUpdate();
        entityManager.createNativeQuery("DELETE FROM touriste_plat WHERE id_touriste = :id").setParameter("id", id).executeUpdate();
        entityManager.createNativeQuery("DELETE FROM touriste_quiz WHERE id_touriste = :id").setParameter("id", id).executeUpdate();
        entityManager.createNativeQuery("DELETE FROM touriste_ville WHERE id_touriste = :id").setParameter("id", id).executeUpdate();

        // 2. Tables enfants directes de utilisateurs (clés étrangères id_users)
        entityManager.createNativeQuery("DELETE FROM favoris WHERE id_users = :id").setParameter("id", id).executeUpdate();
        entityManager.createNativeQuery("DELETE FROM historique_points WHERE id_users = :id").setParameter("id", id).executeUpdate();
        entityManager.createNativeQuery("DELETE FROM touristes WHERE id_users = :id").setParameter("id", id).executeUpdate();
        entityManager.createNativeQuery("DELETE FROM guides WHERE id_users = :id").setParameter("id", id).executeUpdate();
        entityManager.createNativeQuery("DELETE FROM promoteurs WHERE id_users = :id").setParameter("id", id).executeUpdate();
        entityManager.createNativeQuery("DELETE FROM administrateurs WHERE id_users = :id").setParameter("id", id).executeUpdate();
        entityManager.createNativeQuery("DELETE FROM artisans WHERE id_users = :id").setParameter("id", id).executeUpdate();

        // 3. Suppression de la ligne principale dans utilisateurs
        repository.deleteById(id);
    }
}
