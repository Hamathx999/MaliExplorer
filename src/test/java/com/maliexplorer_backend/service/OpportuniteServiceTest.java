package com.maliexplorer_backend.service;

import com.maliexplorer_backend.dto.OpportuniteResponseDTO;
import com.maliexplorer_backend.exception.ResourceNotFoundException;
import com.maliexplorer_backend.model.*;
import com.maliexplorer_backend.repository.GuideRepository;
import com.maliexplorer_backend.repository.PromoteurRepository;
import com.maliexplorer_backend.repository.utilisateurRepository;
import com.maliexplorer_backend.serviceimpl.OpportuniteServiceImpl;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OpportuniteServiceTest {

    @Mock
    private PromoteurRepository promoteurRepository;

    @Mock
    private GuideRepository guideRepository;

    @Mock
    private utilisateurRepository userRepository;

    @Mock
    private EntityManager entityManager;

    @InjectMocks
    private OpportuniteServiceImpl opportuniteService;

    private PromoteurModel promoteur;
    private GuideModel guide;

    @BeforeEach
    void setUp() {
        promoteur = new PromoteurModel();
        promoteur.setIdUsers(2);
        promoteur.setPrenom("Fatoumata");
        promoteur.setNom("Traoré");
        promoteur.setEmail("fatou@festival.ml");
        promoteur.setTelephone("+223 70 11 22 33");
        promoteur.setRole(RoleModel.promoteur);
        promoteur.setNomOrganisation("Festival du Désert");

        guide = new GuideModel();
        guide.setIdUsers(3);
        guide.setPrenom("Ousmane");
        guide.setNom("Sissoko");
        guide.setEmail("ousmane@guide.ml");
        guide.setTelephone("+223 66 44 55 66");
        guide.setRole(RoleModel.guide);
        guide.setLangue("Bambara, Français");
        guide.setExperience(5);
    }

    @Test
    @DisplayName("Soumettre un projet de partenariat bascule en EN_ATTENTE_VALIDATION")
    void testSoumettreProjetPartenaire() {
        when(promoteurRepository.findById(2)).thenReturn(Optional.of(promoteur));
        when(promoteurRepository.save(any(PromoteurModel.class))).thenAnswer(i -> i.getArgument(0));

        OpportuniteResponseDTO res = opportuniteService.soumettreProjetPartenaire(
                2, "Festival du Désert 2027", "Recherche sponsors et mécènes culturels", true);

        assertNotNull(res);
        assertTrue(res.isRecherchePartenariat());
        assertEquals("Festival du Désert 2027", res.getTitreProjet());
        assertEquals("Recherche sponsors et mécènes culturels", res.getBesoinPartenariat());
        assertEquals(StatutModeration.EN_ATTENTE_VALIDATION, res.getStatutModeration());
        assertNull(res.getMotifRejet());
    }

    @Test
    @DisplayName("Validation admin valide le statut en VALIDE et active l'utilisateur")
    void testValiderOpportunite() {
        promoteur.setStatutModeration(StatutModeration.EN_ATTENTE_VALIDATION);
        promoteur.setStatut("EN_ATTENTE");

        when(promoteurRepository.findById(2)).thenReturn(Optional.of(promoteur));
        when(promoteurRepository.save(any(PromoteurModel.class))).thenAnswer(i -> i.getArgument(0));
        when(userRepository.findById(2)).thenReturn(Optional.of(promoteur));
        when(userRepository.save(any(utilisateurModel.class))).thenAnswer(i -> i.getArgument(0));

        OpportuniteResponseDTO res = opportuniteService.validerOpportunite(2);

        assertNotNull(res);
        assertEquals(StatutModeration.VALIDE, res.getStatutModeration());
        assertEquals("ACTIF", promoteur.getStatut());
        assertEquals("Festival du Désert", res.getNomOrganisation());
        verify(promoteurRepository, times(1)).save(promoteur);
        verify(userRepository, times(1)).save(promoteur);
    }

    @Test
    @DisplayName("Validation admin d'un Guide valide le statut en VALIDE et active le compte utilisateur")
    void testValiderOpportuniteGuide() {
        guide.setStatutModeration(StatutModeration.EN_ATTENTE_VALIDATION);
        guide.setStatut("EN_ATTENTE");

        when(promoteurRepository.findById(3)).thenReturn(Optional.empty());
        when(guideRepository.findById(3)).thenReturn(Optional.of(guide));
        when(guideRepository.save(any(GuideModel.class))).thenAnswer(i -> i.getArgument(0));
        when(userRepository.findById(3)).thenReturn(Optional.of(guide));
        when(userRepository.save(any(utilisateurModel.class))).thenAnswer(i -> i.getArgument(0));

        OpportuniteResponseDTO res = opportuniteService.validerOpportunite(3);

        assertNotNull(res);
        assertEquals(StatutModeration.VALIDE, res.getStatutModeration());
        assertEquals("GUIDE", res.getTypePartenaire());
        assertEquals("ACTIF", guide.getStatut());
        assertEquals(5, res.getExperience());
        assertEquals("Bambara, Français", res.getLangue());
        assertNull(guide.getMotifRejet());
        verify(guideRepository, times(1)).save(guide);
        verify(userRepository, times(1)).save(guide);
    }

    @Test
    @DisplayName("Rejet admin passe le statut en REJETE avec un motif explicite")
    void testRejeterOpportunite() {
        promoteur.setStatutModeration(StatutModeration.EN_ATTENTE_VALIDATION);
        when(promoteurRepository.findById(2)).thenReturn(Optional.of(promoteur));
        when(promoteurRepository.save(any(PromoteurModel.class))).thenAnswer(i -> i.getArgument(0));

        OpportuniteResponseDTO res = opportuniteService.rejeterOpportunite(2, "Dossier incomplet, précisions budgétaires requises");

        assertNotNull(res);
        assertEquals(StatutModeration.REJETE, res.getStatutModeration());
        assertEquals("Dossier incomplet, précisions budgétaires requises", res.getMotifRejet());
        assertEquals(StatutModeration.REJETE, promoteur.getStatutModeration());
        assertEquals("Dossier incomplet, précisions budgétaires requises", promoteur.getMotifRejet());
        verify(promoteurRepository, times(1)).save(promoteur);
    }

    @Test
    @DisplayName("Rejet admin d'un Guide passe le statut en REJETE avec motif explicite")
    void testRejeterOpportuniteGuide() {
        guide.setStatutModeration(StatutModeration.EN_ATTENTE_VALIDATION);

        when(promoteurRepository.findById(3)).thenReturn(Optional.empty());
        when(guideRepository.findById(3)).thenReturn(Optional.of(guide));
        when(guideRepository.save(any(GuideModel.class))).thenAnswer(i -> i.getArgument(0));

        OpportuniteResponseDTO res = opportuniteService.rejeterOpportunite(3, "Carte professionnelle de guide expirée");

        assertNotNull(res);
        assertEquals(StatutModeration.REJETE, res.getStatutModeration());
        assertEquals("Carte professionnelle de guide expirée", res.getMotifRejet());
        assertEquals(StatutModeration.REJETE, guide.getStatutModeration());
        assertEquals("Carte professionnelle de guide expirée", guide.getMotifRejet());
        verify(guideRepository, times(1)).save(guide);
    }

    @Test
    @DisplayName("Catalogue public B2B filtre par type et mot-clé")
    void testGetOpportunitesValideesAvecFiltres() {
        promoteur.setRecherchePartenariat(true);
        promoteur.setTitreProjet("Festival International de Musique");
        promoteur.setStatutModeration(StatutModeration.VALIDE);

        guide.setRecherchePartenariat(true);
        guide.setTitreProjet("Circuits Dogon et Tombouctou");
        guide.setStatutModeration(StatutModeration.VALIDE);

        when(promoteurRepository.findByRecherchePartenariatTrueAndStatutModeration(StatutModeration.VALIDE))
                .thenReturn(List.of(promoteur));
        when(guideRepository.findByRecherchePartenariatTrueAndStatutModeration(StatutModeration.VALIDE))
                .thenReturn(List.of(guide));

        // Recherche tous types avec mot-clé "festival"
        List<OpportuniteResponseDTO> filtrerFestival = opportuniteService.getOpportunitesValidees(null, "festival");
        assertEquals(1, filtrerFestival.size());
        assertEquals("PROMOTEUR", filtrerFestival.get(0).getTypePartenaire());

        // Filtrage spécifique par type GUIDE
        List<OpportuniteResponseDTO> guides = opportuniteService.getOpportunitesValidees("GUIDE", null);
        assertEquals(1, guides.size());
        assertEquals("GUIDE", guides.get(0).getTypePartenaire());
    }

    @Test
    @DisplayName("Admin peut lister simultanément les promoteurs et guides en attente de modération")
    void testGetOpportunitesEnAttente_PromoteurEtGuide() {
        promoteur.setStatutModeration(StatutModeration.EN_ATTENTE_VALIDATION);
        guide.setStatutModeration(StatutModeration.EN_ATTENTE_VALIDATION);

        when(promoteurRepository.findByStatutModeration(StatutModeration.EN_ATTENTE_VALIDATION))
                .thenReturn(List.of(promoteur));
        when(guideRepository.findByStatutModeration(StatutModeration.EN_ATTENTE_VALIDATION))
                .thenReturn(List.of(guide));

        List<OpportuniteResponseDTO> enAttente = opportuniteService.getOpportunitesEnAttente();

        assertEquals(2, enAttente.size());
        assertTrue(enAttente.stream().anyMatch(o -> "PROMOTEUR".equals(o.getTypePartenaire())));
        assertTrue(enAttente.stream().anyMatch(o -> "GUIDE".equals(o.getTypePartenaire())));
        verify(promoteurRepository, times(1)).findByStatutModeration(StatutModeration.EN_ATTENTE_VALIDATION);
        verify(guideRepository, times(1)).findByStatutModeration(StatutModeration.EN_ATTENTE_VALIDATION);
    }

    @Test
    @DisplayName("getOpportunitesParStatut filtre fidèlement par statut VALIDE")
    void testGetOpportunitesParStatut_Valide() {
        promoteur.setStatutModeration(StatutModeration.VALIDE);
        guide.setStatutModeration(StatutModeration.VALIDE);

        when(promoteurRepository.findByStatutModeration(StatutModeration.VALIDE))
                .thenReturn(List.of(promoteur));
        when(guideRepository.findByStatutModeration(StatutModeration.VALIDE))
                .thenReturn(List.of(guide));

        List<OpportuniteResponseDTO> res = opportuniteService.getOpportunitesParStatut(StatutModeration.VALIDE);

        assertEquals(2, res.size());
        assertEquals(StatutModeration.VALIDE, res.get(0).getStatutModeration());
        assertEquals(StatutModeration.VALIDE, res.get(1).getStatutModeration());
        verify(promoteurRepository, times(1)).findByStatutModeration(StatutModeration.VALIDE);
        verify(guideRepository, times(1)).findByStatutModeration(StatutModeration.VALIDE);
    }

    @Test
    @DisplayName("getOpportunitesParStatut filtre fidèlement par statut REJETE")
    void testGetOpportunitesParStatut_Rejete() {
        promoteur.setStatutModeration(StatutModeration.REJETE);
        promoteur.setMotifRejet("Dossier non conforme");

        when(promoteurRepository.findByStatutModeration(StatutModeration.REJETE))
                .thenReturn(List.of(promoteur));
        when(guideRepository.findByStatutModeration(StatutModeration.REJETE))
                .thenReturn(Collections.emptyList());

        List<OpportuniteResponseDTO> res = opportuniteService.getOpportunitesParStatut(StatutModeration.REJETE);

        assertEquals(1, res.size());
        assertEquals(StatutModeration.REJETE, res.get(0).getStatutModeration());
        assertEquals("Dossier non conforme", res.get(0).getMotifRejet());
        verify(promoteurRepository, times(1)).findByStatutModeration(StatutModeration.REJETE);
        verify(guideRepository, times(1)).findByStatutModeration(StatutModeration.REJETE);
    }

    @Test
    @DisplayName("getOpportunitesParStatut avec paramètre null bascule par défaut sur EN_ATTENTE_VALIDATION")
    void testGetOpportunitesParStatut_NullFallback() {
        promoteur.setStatutModeration(StatutModeration.EN_ATTENTE_VALIDATION);

        when(promoteurRepository.findByStatutModeration(StatutModeration.EN_ATTENTE_VALIDATION))
                .thenReturn(List.of(promoteur));
        when(guideRepository.findByStatutModeration(StatutModeration.EN_ATTENTE_VALIDATION))
                .thenReturn(Collections.emptyList());

        List<OpportuniteResponseDTO> res = opportuniteService.getOpportunitesParStatut(null);

        assertEquals(1, res.size());
        assertEquals(StatutModeration.EN_ATTENTE_VALIDATION, res.get(0).getStatutModeration());
    }

    @Test
    @DisplayName("Valider un ID inexistant lève ResourceNotFoundException")
    void testValiderOpportunite_NotFound() {
        when(promoteurRepository.findById(999)).thenReturn(Optional.empty());
        when(guideRepository.findById(999)).thenReturn(Optional.empty());
        when(userRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> opportuniteService.validerOpportunite(999));
    }

    @Test
    @DisplayName("Rejeter un ID inexistant lève ResourceNotFoundException")
    void testRejeterOpportunite_NotFound() {
        when(promoteurRepository.findById(999)).thenReturn(Optional.empty());
        when(guideRepository.findById(999)).thenReturn(Optional.empty());
        when(userRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> opportuniteService.rejeterOpportunite(999, "Dossier fictif"));
    }

    @Test
    @DisplayName("Validation Promoteur synchronise nomOrganisation depuis utilisateurModel si null ou vide sur PromoteurModel")
    void testValiderOpportunite_PromoteurNomOrganisationSynchronisee() {
        promoteur.setStatutModeration(StatutModeration.EN_ATTENTE_VALIDATION);
        promoteur.setStatut("EN_ATTENTE");
        promoteur.setNomOrganisation(null);

        utilisateurModel userEntity = new utilisateurModel();
        userEntity.setIdUsers(2);
        userEntity.setNom("Traoré");
        userEntity.setPrenom("Fatoumata");
        userEntity.setRole(RoleModel.promoteur);

        when(promoteurRepository.findById(2)).thenReturn(Optional.of(promoteur));
        when(userRepository.findById(2)).thenReturn(Optional.of(userEntity));
        when(promoteurRepository.save(any(PromoteurModel.class))).thenAnswer(i -> {
            PromoteurModel p = i.getArgument(0);
            assertNotNull(p.getNomOrganisation(), "nomOrganisation ne doit jamais être null lors de la sauvegarde Promoteur");
            assertFalse(p.getNomOrganisation().isBlank(), "nomOrganisation ne doit pas être vide lors de la sauvegarde Promoteur");
            return p;
        });
        when(userRepository.save(any(utilisateurModel.class))).thenAnswer(i -> i.getArgument(0));

        OpportuniteResponseDTO res = opportuniteService.validerOpportunite(2);

        assertNotNull(res);
        assertEquals(StatutModeration.VALIDE, res.getStatutModeration());
        assertNotNull(res.getNomOrganisation());
        assertFalse(res.getNomOrganisation().isBlank());
        verify(promoteurRepository, times(1)).save(promoteur);
        verify(userRepository, times(1)).save(userEntity);
    }

    @Test
    @DisplayName("Rejet Promoteur synchronise nomOrganisation pour prévenir toute ConstraintViolationException")
    void testRejeterOpportunite_PromoteurNomOrganisationSynchronisee() {
        promoteur.setStatutModeration(StatutModeration.EN_ATTENTE_VALIDATION);
        promoteur.setNomOrganisation(null);

        utilisateurModel userEntity = new utilisateurModel();
        userEntity.setIdUsers(2);
        userEntity.setNom("Traoré");

        when(promoteurRepository.findById(2)).thenReturn(Optional.of(promoteur));
        when(userRepository.findById(2)).thenReturn(Optional.of(userEntity));
        when(promoteurRepository.save(any(PromoteurModel.class))).thenAnswer(i -> {
            PromoteurModel p = i.getArgument(0);
            assertNotNull(p.getNomOrganisation(), "nomOrganisation doit être renseigné avant le rejet");
            assertFalse(p.getNomOrganisation().isBlank());
            return p;
        });

        OpportuniteResponseDTO res = opportuniteService.rejeterOpportunite(2, "Dossier incomplet : manque statuts officiels");

        assertNotNull(res);
        assertEquals(StatutModeration.REJETE, res.getStatutModeration());
        assertEquals("Dossier incomplet : manque statuts officiels", res.getMotifRejet());
        assertNotNull(res.getNomOrganisation());
        verify(promoteurRepository, times(1)).save(promoteur);
    }

    @Test
    @DisplayName("Validation Promoteur applique un nom d'organisation fallback si inexistant partout")
    void testValiderOpportunite_PromoteurFallbackOrganisation() {
        promoteur.setStatutModeration(StatutModeration.EN_ATTENTE_VALIDATION);
        promoteur.setNomOrganisation(null);
        promoteur.setNom("Coulibaly");

        utilisateurModel userEntity = new utilisateurModel();
        userEntity.setIdUsers(2);
        userEntity.setNom("Coulibaly");

        when(promoteurRepository.findById(2)).thenReturn(Optional.of(promoteur));
        when(userRepository.findById(2)).thenReturn(Optional.of(userEntity));
        when(promoteurRepository.save(any(PromoteurModel.class))).thenAnswer(i -> i.getArgument(0));
        when(userRepository.save(any(utilisateurModel.class))).thenAnswer(i -> i.getArgument(0));

        OpportuniteResponseDTO res = opportuniteService.validerOpportunite(2);

        assertNotNull(res);
        assertEquals(StatutModeration.VALIDE, res.getStatutModeration());
        assertNotNull(res.getNomOrganisation());
        assertFalse(res.getNomOrganisation().isBlank());
        assertTrue(res.getNomOrganisation().contains("Coulibaly"));
    }

    @Test
    @DisplayName("PromoteurModel respecte les contraintes Jakarta Bean Validation lorsqu'il est renseigné")
    void testPromoteurModel_BeanValidation() {
        jakarta.validation.Validator validator = jakarta.validation.Validation.buildDefaultValidatorFactory().getValidator();

        PromoteurModel valid = new PromoteurModel();
        valid.setNomOrganisation("Festival sur le Niger");
        valid.setPrenom("Bakary");
        valid.setNom("Diarra");
        valid.setEmail("bakary@niger.ml");

        var violations = validator.validate(valid);
        assertTrue(violations.isEmpty(), "Un promoteur valide avec organisation ne doit avoir aucune violation");
    }
}
