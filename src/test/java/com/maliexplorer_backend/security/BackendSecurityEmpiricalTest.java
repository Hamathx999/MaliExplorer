package com.maliexplorer_backend.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.maliexplorer_backend.config.FirebaseAuthenticationFilter;
import com.maliexplorer_backend.config.SecurityConfig;
import com.maliexplorer_backend.controller.AuthController;
import com.maliexplorer_backend.controller.GuideController;
import com.maliexplorer_backend.controller.PromoteurController;
import com.maliexplorer_backend.dto.GuideResponseDTO;
import com.maliexplorer_backend.dto.PromoteurResponseDTO;
import com.maliexplorer_backend.model.GuideModel;
import com.maliexplorer_backend.model.PromoteurModel;
import com.maliexplorer_backend.model.RoleModel;
import com.maliexplorer_backend.model.StatutModeration;
import com.maliexplorer_backend.repository.GuideRepository;
import com.maliexplorer_backend.repository.PromoteurRepository;
import com.maliexplorer_backend.repository.utilisateurRepository;
import com.maliexplorer_backend.service.AuthService;
import com.maliexplorer_backend.service.GuideService;
import com.maliexplorer_backend.service.PromoteurService;
import com.maliexplorer_backend.serviceimpl.AuthServiceImpl;
import com.maliexplorer_backend.serviceimpl.GuideServiceImpl;
import com.maliexplorer_backend.serviceimpl.PromoteurServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = {AuthController.class, GuideController.class, PromoteurController.class})
@Import({SecurityConfig.class, FirebaseAuthenticationFilter.class})
public class BackendSecurityEmpiricalTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @MockBean
    private GuideService guideService;

    @MockBean
    private PromoteurService promoteurService;

    @MockBean
    private utilisateurRepository userRepository;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("1. /api/auth/me SANS token doit impérativement retourner HTTP 401 Unauthorized")
    void testGetMeWithoutTokenReturns401() throws Exception {
        mockMvc.perform(get("/api/auth/me")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.error").value("Non autorisé"))
                .andExpect(jsonPath("$.path").value("/api/auth/me"));
    }

    @Test
    @DisplayName("2. AuthServiceImpl.getCurrentUser() SANS token lève ResponseStatusException 401")
    void testAuthServiceGetCurrentUserThrowsUnauthorized() {
        SecurityContextHolder.clearContext();
        AuthServiceImpl serviceImpl = new AuthServiceImpl(userRepository);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, serviceImpl::getCurrentUser);
        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());
    }

    @Test
    @DisplayName("3. GET /api/guides public : Ne contient AUCUN champ sensible (pieceIdentite, firebaseUid, email, telephone, adresse)")
    void testPublicGetGuidesSerializationExcludesPII() throws Exception {
        GuideResponseDTO sanitizedGuide = GuideResponseDTO.builder()
                .idUsers(1)
                .prenom("Moussa")
                .nom("Traoré")
                .langue("Bambara, Français")
                .experience(5)
                .description("Guide touristique certifié du Pays Dogon")
                .statutModeration(StatutModeration.VALIDE)
                // PII fields are NULL for public calls
                .pieceIdentite(null)
                .firebaseUid(null)
                .email(null)
                .telephone(null)
                .adresse(null)
                .idAdministrateur(null)
                .motifRejet(null)
                .build();

        when(guideService.obtenirTousLesGuides()).thenReturn(List.of(sanitizedGuide));

        String jsonResponse = mockMvc.perform(get("/api/guides")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].prenom").value("Moussa"))
                .andExpect(jsonPath("$[0].nom").value("Traoré"))
                .andExpect(jsonPath("$[0].pieceIdentite").doesNotExist())
                .andExpect(jsonPath("$[0].firebaseUid").doesNotExist())
                .andExpect(jsonPath("$[0].email").doesNotExist())
                .andExpect(jsonPath("$[0].telephone").doesNotExist())
                .andExpect(jsonPath("$[0].adresse").doesNotExist())
                .andExpect(jsonPath("$[0].idAdministrateur").doesNotExist())
                .andExpect(jsonPath("$[0].motifRejet").doesNotExist())
                .andReturn().getResponse().getContentAsString();

        // Direct raw JSON assertion
        assertFalse(jsonResponse.contains("pieceIdentite"), "Le JSON public ne doit pas contenir 'pieceIdentite'");
        assertFalse(jsonResponse.contains("firebaseUid"), "Le JSON public ne doit pas contenir 'firebaseUid'");
        assertFalse(jsonResponse.contains("email"), "Le JSON public ne doit pas contenir 'email'");
        assertFalse(jsonResponse.contains("telephone"), "Le JSON public ne doit pas contenir 'telephone'");
        assertFalse(jsonResponse.contains("adresse"), "Le JSON public ne doit pas contenir 'adresse'");
    }

    @Test
    @DisplayName("4. GET /api/promoteurs public : Ne contient AUCUN champ sensible (pieceIdentite, firebaseUid, email, telephone, adresse, motifRejet)")
    void testPublicGetPromoteursSerializationExcludesPII() throws Exception {
        PromoteurResponseDTO sanitizedPromoteur = PromoteurResponseDTO.builder()
                .idUsers(2)
                .prenom("Fatoumata")
                .nom("Coulibaly")
                .nomOrganisation("Mali Arts & Culture")
                .statutModeration(StatutModeration.VALIDE)
                // PII fields are NULL for public calls
                .pieceIdentite(null)
                .firebaseUid(null)
                .email(null)
                .telephone(null)
                .adresse(null)
                .motifRejet(null)
                .build();

        when(promoteurService.obtenirTousLesPromoteurs()).thenReturn(List.of(sanitizedPromoteur));

        String jsonResponse = mockMvc.perform(get("/api/promoteurs")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].prenom").value("Fatoumata"))
                .andExpect(jsonPath("$[0].nomOrganisation").value("Mali Arts & Culture"))
                .andExpect(jsonPath("$[0].pieceIdentite").doesNotExist())
                .andExpect(jsonPath("$[0].firebaseUid").doesNotExist())
                .andExpect(jsonPath("$[0].email").doesNotExist())
                .andExpect(jsonPath("$[0].telephone").doesNotExist())
                .andExpect(jsonPath("$[0].adresse").doesNotExist())
                .andExpect(jsonPath("$[0].motifRejet").doesNotExist())
                .andReturn().getResponse().getContentAsString();

        // Direct raw JSON assertion
        assertFalse(jsonResponse.contains("pieceIdentite"), "Le JSON public ne doit pas contenir 'pieceIdentite'");
        assertFalse(jsonResponse.contains("firebaseUid"), "Le JSON public ne doit pas contenir 'firebaseUid'");
        assertFalse(jsonResponse.contains("email"), "Le JSON public ne doit pas contenir 'email'");
        assertFalse(jsonResponse.contains("telephone"), "Le JSON public ne doit pas contenir 'telephone'");
        assertFalse(jsonResponse.contains("adresse"), "Le JSON public ne doit pas contenir 'adresse'");
        assertFalse(jsonResponse.contains("motifRejet"), "Le JSON public ne doit pas contenir 'motifRejet'");
    }

    @Test
    @DisplayName("5. GuideServiceImpl : Masquage réel en profondeur des données sensibles pour utilisateur public")
    void testGuideServiceImplDeepSanitization() throws Exception {
        SecurityContextHolder.clearContext();

        GuideRepository mockRepo = mock(GuideRepository.class);
        GuideServiceImpl service = new GuideServiceImpl(mockRepo);

        GuideModel model = new GuideModel();
        model.setIdUsers(42);
        model.setPrenom("Ousmane");
        model.setNom("Keita");
        model.setEmail("secret.ousmane@guide.ml");
        model.setTelephone("+223 70 00 11 22");
        model.setAdresse("Rue Secrete, Bamako");
        model.setPieceIdentite("SECRET_PASSPORT_B12345");
        model.setFirebaseUid("FIREBASE_UID_SECRET_999");
        model.setIdAdministrateur(99);
        model.setMotifRejet("Motif confidentiel");
        model.setStatutModeration(StatutModeration.VALIDE);

        when(mockRepo.findById(42)).thenReturn(Optional.of(model));

        GuideResponseDTO responseDTO = service.obtenirGuideParId(42);

        // Verify strictly confidential fields are stripped
        assertNull(responseDTO.getPieceIdentite());
        assertNull(responseDTO.getFirebaseUid());
        assertNull(responseDTO.getAdresse());
        assertNull(responseDTO.getIdAdministrateur());
        assertNull(responseDTO.getMotifRejet());

        // Canaux de contact publics autorisés pour joindre le guide certifié
        assertEquals("secret.ousmane@guide.ml", responseDTO.getEmail());
        assertEquals("+223 70 00 11 22", responseDTO.getTelephone());

        // Verify JSON serialization omits null confidential fields (@JsonInclude(NON_NULL))
        String json = objectMapper.writeValueAsString(responseDTO);
        assertFalse(json.contains("pieceIdentite"));
        assertFalse(json.contains("firebaseUid"));
        assertFalse(json.contains("adresse"));
        assertFalse(json.contains("idAdministrateur"));
        assertFalse(json.contains("motifRejet"));
        assertTrue(json.contains("secret.ousmane@guide.ml"));
        assertTrue(json.contains("+223 70 00 11 22"));
        assertTrue(json.contains("Ousmane"));
        assertTrue(json.contains("Keita"));
    }

    @Test
    @DisplayName("6. PromoteurServiceImpl : Masquage réel en profondeur des données sensibles pour utilisateur public")
    void testPromoteurServiceImplDeepSanitization() throws Exception {
        SecurityContextHolder.clearContext();

        PromoteurRepository mockRepo = mock(PromoteurRepository.class);
        PromoteurServiceImpl service = new PromoteurServiceImpl(mockRepo);

        PromoteurModel model = new PromoteurModel();
        model.setIdUsers(55);
        model.setPrenom("Awa");
        model.setNom("Dembele");
        model.setEmail("awa.prive@festival.ml");
        model.setTelephone("+223 66 11 22 33");
        model.setAdresse("Quartier Reserve, Segou");
        model.setPieceIdentite("SECRET_ID_CARD_9999");
        model.setFirebaseUid("FIREBASE_UID_AWA_777");
        model.setMotifRejet("Dossier incomplet (rejet)");
        model.setNomOrganisation("Festival sur le Niger");
        model.setStatutModeration(StatutModeration.VALIDE);

        when(mockRepo.findById(55)).thenReturn(Optional.of(model));

        PromoteurResponseDTO responseDTO = service.obtenirPromoteurParId(55);

        // Verify DTO fields are stripped
        assertNull(responseDTO.getPieceIdentite());
        assertNull(responseDTO.getFirebaseUid());
        assertNull(responseDTO.getEmail());
        assertNull(responseDTO.getTelephone());
        assertNull(responseDTO.getAdresse());
        assertNull(responseDTO.getMotifRejet());

        // Verify JSON serialization omits null fields (@JsonInclude(NON_NULL))
        String json = objectMapper.writeValueAsString(responseDTO);
        assertFalse(json.contains("pieceIdentite"));
        assertFalse(json.contains("firebaseUid"));
        assertFalse(json.contains("email"));
        assertFalse(json.contains("telephone"));
        assertFalse(json.contains("adresse"));
        assertFalse(json.contains("motifRejet"));
        assertTrue(json.contains("Awa"));
        assertTrue(json.contains("Dembele"));
        assertTrue(json.contains("Festival sur le Niger"));
    }

    @Test
    @DisplayName("7. GuideServiceImpl & PromoteurServiceImpl : Privilégié ADMIN reçoit les données sensibles")
    void testPrivilegedAdminAccessIncludesPII() throws Exception {
        org.springframework.security.core.Authentication adminAuth = new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                "admin@maliexplorer.com",
                "dummy",
                List.of(new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_ADMIN"))
        );
        SecurityContextHolder.getContext().setAuthentication(adminAuth);

        GuideRepository mockGuideRepo = mock(GuideRepository.class);
        GuideServiceImpl guideService = new GuideServiceImpl(mockGuideRepo);

        GuideModel guide = new GuideModel();
        guide.setIdUsers(77);
        guide.setPrenom("Sékou");
        guide.setNom("Ballo");
        guide.setPieceIdentite("ADMIN_VISIBLE_PIECE");
        guide.setFirebaseUid("ADMIN_VISIBLE_UID");
        guide.setEmail("sekou@adminview.ml");
        guide.setTelephone("70112233");
        guide.setAdresse("Admin View Address");
        guide.setStatutModeration(StatutModeration.VALIDE);

        when(mockGuideRepo.findById(77)).thenReturn(Optional.of(guide));

        GuideResponseDTO guideDto = guideService.obtenirGuideParId(77);
        assertNotNull(guideDto.getPieceIdentite());
        assertEquals("ADMIN_VISIBLE_PIECE", guideDto.getPieceIdentite());
        assertEquals("ADMIN_VISIBLE_UID", guideDto.getFirebaseUid());

        String json = objectMapper.writeValueAsString(guideDto);
        assertTrue(json.contains("ADMIN_VISIBLE_PIECE"));
        assertTrue(json.contains("ADMIN_VISIBLE_UID"));
        assertTrue(json.contains("sekou@adminview.ml"));
    }
}
