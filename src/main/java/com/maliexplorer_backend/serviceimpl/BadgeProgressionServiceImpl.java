package com.maliexplorer_backend.serviceimpl;

import com.maliexplorer_backend.config.SecurityUtils;
import com.maliexplorer_backend.dto.HistoriquePointResponseDTO;
import com.maliexplorer_backend.dto.ProgressionResponseDTO;
import com.maliexplorer_backend.exception.BadRequestException;
import com.maliexplorer_backend.exception.ResourceNotFoundException;
import com.maliexplorer_backend.model.BadgeBambara;
import com.maliexplorer_backend.model.HistoriquePointModel;
import com.maliexplorer_backend.model.utilisateurModel;
import com.maliexplorer_backend.repository.HistoriquePointRepository;
import com.maliexplorer_backend.repository.utilisateurRepository;
import com.maliexplorer_backend.service.BadgeProgressionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class BadgeProgressionServiceImpl implements BadgeProgressionService {

    private final utilisateurRepository userRepo;
    private final HistoriquePointRepository historiquePointRepository;

    @Override
    @Transactional(readOnly = true)
    public ProgressionResponseDTO getProgressionUtilisateur(int userId) {
        utilisateurModel user = findUserOrThrow(userId);
        return calculerProgression(user.getPoints(), user);
    }

    @Override
    @Transactional(readOnly = true)
    public ProgressionResponseDTO getProgressionCurrentUtilisateur() {
        utilisateurModel user = getCurrentAuthenticatedUser();
        return calculerProgression(user.getPoints(), user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<HistoriquePointResponseDTO> getHistoriquePoints(int userId) {
        // Valide que l'utilisateur existe
        findUserOrThrow(userId);
        return historiquePointRepository.findByUtilisateurIdUsersOrderByDateGainDesc(userId)
                .stream()
                .map(this::mapToHistoriqueDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<HistoriquePointResponseDTO> getHistoriquePointsCurrentUtilisateur() {
        utilisateurModel user = getCurrentAuthenticatedUser();
        return historiquePointRepository.findByUtilisateurIdUsersOrderByDateGainDesc(user.getIdUsers())
                .stream()
                .map(this::mapToHistoriqueDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ProgressionResponseDTO attribuerPoints(int userId, String action, int points, String description, String referenceActivite) {
        utilisateurModel user = findUserOrThrow(userId);

        // Anti-duplication : si l'activité a déjà été validée par cet utilisateur, on bloque
        if (referenceActivite != null && !referenceActivite.isBlank()) {
            if (historiquePointRepository.existsByUtilisateurIdUsersAndReferenceActivite(userId, referenceActivite)) {
                log.info("Tentative de duplication de points évitée pour l'utilisateur ID={} et l'activité={}", userId, referenceActivite);
                ProgressionResponseDTO progression = calculerProgression(user.getPoints(), user);
                progression.setMessage("Cette activité a déjà été validée. Aucun point supplémentaire n'a été attribué.");
                return progression;
            }
        }

        // Attribution des points
        int nouveauTotal = user.getPoints() + Math.max(0, points);
        user.setPoints(nouveauTotal);
        userRepo.save(user);

        // Enregistrement de l'historique
        HistoriquePointModel historique = HistoriquePointModel.builder()
                .utilisateur(user)
                .action(action)
                .pointsGagnes(points)
                .description(description)
                .referenceActivite(referenceActivite)
                .dateGain(LocalDateTime.now())
                .build();
        historiquePointRepository.save(historique);

        ProgressionResponseDTO progression = calculerProgression(nouveauTotal, user);
        progression.setMessage(String.format("+%d points gagnés pour '%s' ! %s", points, (description != null ? description : action), progression.getMessage()));
        return progression;
    }

    @Override
    public ProgressionResponseDTO attribuerPointsUtilisateurConnecte(String action, int points, String description, String referenceActivite) {
        utilisateurModel user = getCurrentAuthenticatedUser();
        return attribuerPoints(user.getIdUsers(), action, points, description, referenceActivite);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean aDejaValideActivite(int userId, String referenceActivite) {
        if (referenceActivite == null || referenceActivite.isBlank()) {
            return false;
        }
        return historiquePointRepository.existsByUtilisateurIdUsersAndReferenceActivite(userId, referenceActivite);
    }

    @Override
    public ProgressionResponseDTO calculerProgression(int points, utilisateurModel user) {
        BadgeBambara currentBadge = BadgeBambara.fromPoints(points);
        BadgeBambara nextBadge = currentBadge.getNextBadge();

        int pointsToNext = 0;
        Integer nextSeuil = null;
        double progressionPourcent = 100.0;
        String message;

        if (nextBadge != null) {
            nextSeuil = nextBadge.getSeuilMin();
            pointsToNext = Math.max(0, nextSeuil - points);

            // Calcul du pourcentage de progression vers le prochain seuil
            if (nextSeuil > 0) {
                double raw = ((double) points / (double) nextSeuil) * 100.0;
                progressionPourcent = Math.min(100.0, Math.round(raw * 10.0) / 10.0);
            }
            message = String.format("Badge actuel : %s (%s). Encore %d point(s) pour atteindre %s !",
                    currentBadge.getNom(), currentBadge.getDescription(), pointsToNext, nextBadge.getNom());
        } else {
            message = String.format("Félicitations ! Vous avez atteint le niveau maximal : %s (%s) !",
                    currentBadge.getNom(), currentBadge.getDescription());
        }

        String nomComplet = "";
        String email = "";
        Integer idUsers = null;

        if (user != null) {
            idUsers = user.getIdUsers();
            email = user.getEmail();
            nomComplet = ((user.getPrenom() != null ? user.getPrenom() : "") + " " + (user.getNom() != null ? user.getNom() : "")).trim();
        }

        return ProgressionResponseDTO.builder()
                .idUsers(idUsers)
                .nomComplet(nomComplet)
                .email(email)
                .points(points)
                .badge(currentBadge.getNom())
                .badgeCode(currentBadge.name())
                .badgeDescription(currentBadge.getDescription())
                .nextBadge(nextBadge != null ? nextBadge.getNom() : null)
                .pointsToNextBadge(pointsToNext)
                .pointsNextBadgeSeuil(nextSeuil)
                .progression(progressionPourcent)
                .message(message)
                .build();
    }

    // ======================== PRIVATE HELPERS ========================

    private utilisateurModel findUserOrThrow(int userId) {
        return userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable avec l'ID : " + userId));
    }

    private utilisateurModel getCurrentAuthenticatedUser() {
        utilisateurModel user = SecurityUtils.getCurrentUser();
        if (user != null) {
            return user;
        }

        String email = SecurityUtils.getCurrentUserEmail();
        if (email == null || email.isBlank()) {
            throw new BadRequestException("Aucun utilisateur actuellement connecté.");
        }

        return userRepo.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé avec l'email : " + email));
    }

    private HistoriquePointResponseDTO mapToHistoriqueDTO(HistoriquePointModel h) {
        return HistoriquePointResponseDTO.builder()
                .idHistorique(h.getIdHistorique())
                .action(h.getAction())
                .pointsGagnes(h.getPointsGagnes())
                .description(h.getDescription())
                .referenceActivite(h.getReferenceActivite())
                .dateGain(h.getDateGain())
                .build();
    }
}
