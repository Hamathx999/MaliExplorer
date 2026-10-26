package com.maliexplorer_backend.service;

import com.maliexplorer_backend.dto.AuthResponseDTO;
import com.maliexplorer_backend.dto.RegisterRequestDTO;
import com.maliexplorer_backend.exception.BadRequestException;
import com.maliexplorer_backend.model.*;
import com.maliexplorer_backend.repository.EvenementRepository;
import com.maliexplorer_backend.repository.GuideRepository;
import com.maliexplorer_backend.repository.PromoteurRepository;
import com.maliexplorer_backend.repository.utilisateurRepository;
import com.maliexplorer_backend.serviceimpl.AuthServiceImpl;
import com.maliexplorer_backend.serviceimpl.EvenementServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ChallengerM1AuthAndEventEmpiricalTest {

    @Mock
    private utilisateurRepository userRepository;

    @Mock
    private PromoteurRepository promoteurRepository;

    @Mock
    private GuideRepository guideRepository;

    @Mock
    private EvenementRepository evenementRepository;

    private AuthServiceImpl authService;
    private EvenementServiceImpl evenementService;

    @BeforeEach
    void setUp() {
        authService = new AuthServiceImpl(userRepository, promoteurRepository, guideRepository);
        evenementService = new EvenementServiceImpl(evenementRepository, promoteurRepository);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    // ---------------------------------------------------------------------------------
    // MISSION 1 : Résolution de la course critique Auth & Enregistrement Promoteur
    // ---------------------------------------------------------------------------------

    @Test
    @DisplayName("1.1 Course critique : Reconcilier un utilisateur auto-provisionné en TOURISTE vers PROMOTEUR sans lever de BadRequest")
    void testReconciliationCourseCritiquePromoteur() {
        String email = "nouveau.promoteur@culture.ml";

        // Utilisateur existant auto-provisionné avec rôle Touriste par Firebase userChanges
        utilisateurModel existingTouriste = utilisateurModel.builder()
                .idUsers(42)
                .email(email)
                .prenom("Moussa")
                .nom("Kone")
                .role(RoleModel.touriste)
                .firebaseUid("uid-firebase-42")
                .build();

        when(userRepository.existsByEmail(email)).thenReturn(true);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(existingTouriste));

        when(promoteurRepository.save(any(PromoteurModel.class))).thenAnswer(invocation -> {
            PromoteurModel p = invocation.getArgument(0);
            p.setIdUsers(42);
            return p;
        });

        RegisterRequestDTO registerDTO = RegisterRequestDTO.builder()
                .email(email)
                .prenom("Moussa")
                .nom("Kone")
                .role(RoleModel.promoteur)
                .idToken("dummy_valid_jwt_id_token")
                .nomOrganisation("Festival des Masques et Marionnettes")
                .telephone("+223 70 88 99 00")
                .pieceIdentite("ID-MALI-2026-99")
                .build();

        AuthResponseDTO response = authService.register(registerDTO);

        assertNotNull(response);
        assertEquals(RoleModel.promoteur, response.getRole(), "Le rôle final doit être promoteur");
        assertEquals("Festival des Masques et Marionnettes", response.getNomOrganisation());
        assertEquals("+223 70 88 99 00", response.getTelephone());
        assertEquals("ID-MALI-2026-99", response.getPieceIdentite());
        assertEquals(StatutModeration.EN_ATTENTE_VALIDATION, response.getStatutModeration(),
                "Le statut de modération doit être EN_ATTENTE_VALIDATION");
        assertEquals("EN_ATTENTE", response.getStatut());

        verify(userRepository).delete(existingTouriste);
        verify(promoteurRepository).save(any(PromoteurModel.class));
    }

    @Test
    @DisplayName("1.2 Course critique : Un compte existant sans idToken doit être rejeté (protection contre squatting)")
    void testSquattingSansTokenRejete() {
        String email = "existant@culture.ml";
        when(userRepository.existsByEmail(email)).thenReturn(true);

        RegisterRequestDTO registerDTO = RegisterRequestDTO.builder()
                .email(email)
                .prenom("Moussa")
                .nom("Kone")
                .role(RoleModel.promoteur)
                .idToken(null) // Pas de token
                .build();

        BadRequestException ex = assertThrows(BadRequestException.class, () -> authService.register(registerDTO));
        assertTrue(ex.getMessage().contains("existe déjà"));
        verify(promoteurRepository, never()).save(any());
    }

    @Test
    @DisplayName("1.3 Course critique : Un compte ADMIN existant ne peut JAMAIS être écrasé lors d'une inscription")
    void testCompteAdminExistantProtege() {
        String adminEmail = "admin@maliexplorer.ml";
        utilisateurModel adminUser = utilisateurModel.builder()
                .idUsers(1)
                .email(adminEmail)
                .role(RoleModel.admin)
                .build();

        when(userRepository.existsByEmail(adminEmail)).thenReturn(true);
        when(userRepository.findByEmail(adminEmail)).thenReturn(Optional.of(adminUser));

        RegisterRequestDTO registerDTO = RegisterRequestDTO.builder()
                .email(adminEmail)
                .role(RoleModel.promoteur)
                .idToken("dummy_token")
                .build();

        BadRequestException ex = assertThrows(BadRequestException.class, () -> authService.register(registerDTO));
        assertTrue(ex.getMessage().contains("compte administrateur"));
        verify(userRepository, never()).delete(any());
    }

    // ---------------------------------------------------------------------------------
    // MISSION 3 : Publication directe d'événements
    // ---------------------------------------------------------------------------------

    @Test
    @DisplayName("3.1 Événements : Un promoteur VALIDÉ (statutModeration == VALIDE) obtient le statut VALIDE directement")
    void testPublicationDirectePromoteurValide() {
        String email = "promoteur.valide@culture.ml";
        PromoteurModel promoteurValide = new PromoteurModel();
        promoteurValide.setIdUsers(10);
        promoteurValide.setEmail(email);
        promoteurValide.setRole(RoleModel.promoteur);
        promoteurValide.setStatutModeration(StatutModeration.VALIDE);

        when(promoteurRepository.findByEmail(email)).thenReturn(Optional.of(promoteurValide));
        when(evenementRepository.save(any(EvenementModel.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Authentifier le promoteur validé
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                promoteurValide, null, List.of(new SimpleGrantedAuthority("ROLE_PROMOTEUR")));
        SecurityContextHolder.getContext().setAuthentication(auth);

        EvenementModel newEvent = EvenementModel.builder()
                .titre("Festival Biennale Artistique 2026")
                .lieu("Palais de la Culture")
                .ville("Bamako")
                .build();

        EvenementModel saved = evenementService.create(newEvent);

        assertNotNull(saved);
        assertEquals(EvenementModel.Statut.VALIDE, saved.getStatut(),
                "L'événement doit être directement publié avec le statut VALIDE");
    }

    @Test
    @DisplayName("3.2 Événements : Un promoteur EN ATTENTE (statutModeration == EN_ATTENTE_VALIDATION) prend le statut EN_ATTENTE par défaut")
    void testPublicationPromoteurEnAttente() {
        String email = "promoteur.attente@culture.ml";
        PromoteurModel promoteurAttente = new PromoteurModel();
        promoteurAttente.setIdUsers(11);
        promoteurAttente.setEmail(email);
        promoteurAttente.setRole(RoleModel.promoteur);
        promoteurAttente.setStatutModeration(StatutModeration.EN_ATTENTE_VALIDATION);

        when(promoteurRepository.findByEmail(email)).thenReturn(Optional.of(promoteurAttente));
        when(evenementRepository.save(any(EvenementModel.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                promoteurAttente, null, List.of(new SimpleGrantedAuthority("ROLE_PROMOTEUR")));
        SecurityContextHolder.getContext().setAuthentication(auth);

        EvenementModel newEvent = EvenementModel.builder()
                .titre("Concert Émergent")
                .statut(null)
                .build();

        EvenementModel saved = evenementService.create(newEvent);

        assertNotNull(saved);
        assertEquals(EvenementModel.Statut.EN_ATTENTE, saved.getStatut(),
                "L'événement d'un promoteur en attente sans statut doit être EN_ATTENTE");
    }

    @Test
    @DisplayName("3.3 ADVERSARIAL : Un promoteur EN ATTENTE qui injecte explicitement 'statut: VALIDE' dans sa requête")
    void testAdversarialInjectionStatutValideParPromoteurNonValide() {
        String email = "promoteur.malicieux@culture.ml";
        PromoteurModel promoteurAttente = new PromoteurModel();
        promoteurAttente.setIdUsers(12);
        promoteurAttente.setEmail(email);
        promoteurAttente.setRole(RoleModel.promoteur);
        promoteurAttente.setStatutModeration(StatutModeration.EN_ATTENTE_VALIDATION);

        when(promoteurRepository.findByEmail(email)).thenReturn(Optional.of(promoteurAttente));
        when(evenementRepository.save(any(EvenementModel.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                promoteurAttente, null, List.of(new SimpleGrantedAuthority("ROLE_PROMOTEUR")));
        SecurityContextHolder.getContext().setAuthentication(auth);

        // Attaque : l'utilisateur non validé passe 'statut: VALIDE' dans son payload JSON
        EvenementModel adversarialEvent = EvenementModel.builder()
                .titre("Tentative d'usurpation modération directe")
                .statut(EvenementModel.Statut.VALIDE)
                .build();

        EvenementModel saved = evenementService.create(adversarialEvent);

        System.out.println("[TEST EMPIRIQUE] Statut conservé pour un promoteur non validé injectant VALIDE : " + saved.getStatut());
        
        // La règle de sécurité exige que la publication directe ne soit accordée QUE si statutModeration == VALIDE.
        // Si le promoteur n'est pas validé, son statut DOIT être forcé à EN_ATTENTE.
        assertEquals(EvenementModel.Statut.EN_ATTENTE, saved.getStatut(),
                "VIOLATION DE SÉCURITÉ : Un promoteur non validé (EN_ATTENTE_VALIDATION) a pu publier directement son événement avec le statut VALIDE !");
    }
}
