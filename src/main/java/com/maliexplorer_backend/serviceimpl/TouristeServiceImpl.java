package com.maliexplorer_backend.serviceimpl;

import com.maliexplorer_backend.config.SecurityUtils;
import com.maliexplorer_backend.dto.TouristeRequestDTO;
import com.maliexplorer_backend.dto.TouristeResponseDTO;
import com.maliexplorer_backend.exception.BadRequestException;
import com.maliexplorer_backend.exception.ResourceNotFoundException;
import com.maliexplorer_backend.model.ArticleModel;
import com.maliexplorer_backend.model.LieuHistoriqueModel;
import com.maliexplorer_backend.model.RoleModel;
import com.maliexplorer_backend.model.TouristeModel;
import com.maliexplorer_backend.repository.ArticleRepository;
import com.maliexplorer_backend.repository.LieuHistoriqueRepository;
import com.maliexplorer_backend.repository.TouristeRepository;
import com.maliexplorer_backend.service.TouristeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class TouristeServiceImpl implements TouristeService {

    private final TouristeRepository touristeRepository;
    private final LieuHistoriqueRepository lieuHistoriqueRepository;
    private final ArticleRepository articleRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<TouristeResponseDTO> obtenirTousLesTouristes(Pageable pageable) {
        return touristeRepository.findAll(pageable).map(this::mapToResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TouristeResponseDTO> obtenirTousLesTouristes() {
        return touristeRepository.findAll().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public TouristeResponseDTO obtenirTouristeParId(int id) {
        TouristeModel touriste = findTouristeOrThrow(id);
        return mapToResponseDTO(touriste);
    }

    @Override
    public TouristeResponseDTO creerTouriste(TouristeRequestDTO requestDTO) {
        if (touristeRepository.existsByEmail(requestDTO.getEmail())) {
            throw new BadRequestException("Cet email est déjà associé à un compte !");
        }

        TouristeModel touriste = new TouristeModel();
        touriste.setPrenom(requestDTO.getPrenom());
        touriste.setNom(requestDTO.getNom());
        touriste.setEmail(requestDTO.getEmail());
        touriste.setMotDePasse(requestDTO.getMotDePasse());
        touriste.setAdresse(requestDTO.getAdresse());
        touriste.setPhotoUrl(requestDTO.getPhotoUrl());
        touriste.setPoints(requestDTO.getPoints());
        touriste.setRole(RoleModel.touriste);
        touriste.setDateCreation(Date.valueOf(LocalDate.now()));
        touriste.setLieuxVisites(new ArrayList<>());
        touriste.setArticlesLus(new ArrayList<>());

        TouristeModel saved = touristeRepository.save(touriste);
        return mapToResponseDTO(saved);
    }

    @Override
    public TouristeResponseDTO mettreAJourTouriste(int id, TouristeRequestDTO requestDTO) {
        TouristeModel existant = findTouristeOrThrow(id);

        if (!existant.getEmail().equalsIgnoreCase(requestDTO.getEmail())
                && touristeRepository.existsByEmail(requestDTO.getEmail())) {
            throw new BadRequestException("Cet email est déjà utilisé par un autre compte !");
        }

        existant.setPrenom(requestDTO.getPrenom());
        existant.setNom(requestDTO.getNom());
        existant.setEmail(requestDTO.getEmail());
        existant.setAdresse(requestDTO.getAdresse());
        existant.setPhotoUrl(requestDTO.getPhotoUrl());
        existant.setPoints(requestDTO.getPoints());

        if (requestDTO.getMotDePasse() != null && !requestDTO.getMotDePasse().isBlank()) {
            existant.setMotDePasse(requestDTO.getMotDePasse());
        }

        TouristeModel updated = touristeRepository.save(existant);
        return mapToResponseDTO(updated);
    }

    @Override
    public void supprimerTouriste(int id) {
        if (!touristeRepository.existsById(id)) {
            throw new ResourceNotFoundException("Touriste introuvable avec l'ID : " + id);
        }
        touristeRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public TouristeResponseDTO getProfilUtilisateurConnecte() {
        TouristeModel current = getCurrentTouristeOrThrow();
        return mapToResponseDTO(current);
    }

    @Override
    public TouristeResponseDTO mettreAJourProfil(TouristeRequestDTO requestDTO) {
        TouristeModel current = getCurrentTouristeOrThrow();

        if (requestDTO.getPrenom() != null && !requestDTO.getPrenom().isBlank()) {
            current.setPrenom(requestDTO.getPrenom());
        }
        if (requestDTO.getNom() != null && !requestDTO.getNom().isBlank()) {
            current.setNom(requestDTO.getNom());
        }
        if (requestDTO.getAdresse() != null) {
            current.setAdresse(requestDTO.getAdresse());
        }
        if (requestDTO.getPhotoUrl() != null) {
            current.setPhotoUrl(requestDTO.getPhotoUrl());
        }

        TouristeModel updated = touristeRepository.save(current);
        return mapToResponseDTO(updated);
    }

    @Override
    public TouristeResponseDTO ajouterLieuVisite(Long idLieu) {
        TouristeModel current = getCurrentTouristeOrThrow();
        LieuHistoriqueModel lieu = lieuHistoriqueRepository.findById(idLieu)
                .orElseThrow(() -> new ResourceNotFoundException("Lieu historique introuvable avec l'ID : " + idLieu));

        if (current.getLieuxVisites() == null) {
            current.setLieuxVisites(new ArrayList<>());
        }

        if (!current.getLieuxVisites().contains(lieu)) {
            current.getLieuxVisites().add(lieu);
            current.setPoints(current.getPoints() + 10); // +10 points par lieu visité
        }

        TouristeModel saved = touristeRepository.save(current);
        return mapToResponseDTO(saved);
    }

    @Override
    public TouristeResponseDTO ajouterArticleLu(Long idArticle) {
        TouristeModel current = getCurrentTouristeOrThrow();
        ArticleModel article = articleRepository.findById(idArticle)
                .orElseThrow(() -> new ResourceNotFoundException("Article introuvable avec l'ID : " + idArticle));

        if (current.getArticlesLus() == null) {
            current.setArticlesLus(new ArrayList<>());
        }

        if (!current.getArticlesLus().contains(article)) {
            current.getArticlesLus().add(article);
            current.setPoints(current.getPoints() + 5); // +5 points par article lu
        }

        TouristeModel saved = touristeRepository.save(current);
        return mapToResponseDTO(saved);
    }

    private TouristeModel findTouristeOrThrow(int id) {
        return touristeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Touriste introuvable avec l'ID : " + id));
    }

    private TouristeModel getCurrentTouristeOrThrow() {
        String email = SecurityUtils.getCurrentUserEmail()
                .orElseThrow(() -> new BadRequestException("Aucun utilisateur authentifié dans la session"));

        return touristeRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Profil touriste introuvable pour l'utilisateur connecté : " + email));
    }

    private TouristeResponseDTO mapToResponseDTO(TouristeModel t) {
        List<Long> idsLieux = t.getLieuxVisites() != null
                ? t.getLieuxVisites().stream().map(LieuHistoriqueModel::getIdLieu).collect(Collectors.toList())
                : new ArrayList<>();

        List<Long> idsArticles = t.getArticlesLus() != null
                ? t.getArticlesLus().stream().map(ArticleModel::getIdArticle).collect(Collectors.toList())
                : new ArrayList<>();

        return TouristeResponseDTO.builder()
                .idUsers(t.getIdUsers())
                .idTouriste(t.getIdTouriste())
                .prenom(t.getPrenom())
                .nom(t.getNom())
                .email(t.getEmail())
                .adresse(t.getAdresse())
                .photoUrl(t.getPhotoUrl())
                .dateCreation(t.getDateCreation())
                .role(t.getRole())
                .points(t.getPoints())
                .nombreLieuxVisites(idsLieux.size())
                .nombreArticlesLus(idsArticles.size())
                .nombreVillesVisitees(t.getVillesVisitees() != null ? t.getVillesVisitees().size() : 0)
                .nombreEthniesVues(t.getEthniesVues() != null ? t.getEthniesVues().size() : 0)
                .nombrePlatsVus(t.getPlatsVus() != null ? t.getPlatsVus().size() : 0)
                .nombreQuizJoues(t.getQuizJoues() != null ? t.getQuizJoues().size() : 0)
                .idsLieuxVisites(idsLieux)
                .idsArticlesLus(idsArticles)
                .build();
    }
}
