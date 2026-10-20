package com.maliexplorer_backend.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.maliexplorer_backend.config.FirebaseAuthenticationFilter;
import com.maliexplorer_backend.config.SecurityConfig;
import com.maliexplorer_backend.controller.InfoPratiqueController;
import com.maliexplorer_backend.controller.LangueNationaleController;
import com.maliexplorer_backend.dto.InfoPratiqueRequestDTO;
import com.maliexplorer_backend.dto.InfoPratiqueResponseDTO;
import com.maliexplorer_backend.dto.LangueRequestDTO;
import com.maliexplorer_backend.dto.LangueResponseDTO;
import com.maliexplorer_backend.service.InfoPratiqueService;
import com.maliexplorer_backend.service.LangueNationaleService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Suite de tests de sécurité empiriques pour le Jalon 2 (Langues Nationales & Informations Pratiques).
 * Vérifie rigoureusement :
 * - L'accès public anonyme (GET -> 200 OK)
 * - Le rejet des mutations anonymes (POST, PUT, DELETE, PATCH -> 401 Unauthorized ou 403 Forbidden)
 * - Le rejet des mutations par des utilisateurs non administrateurs (ex: ROLE_TOURISTE -> 403 Forbidden)
 * - L'autorisation des mutations par l'administrateur (ROLE_ADMIN -> 201/200/204)
 * - La validation des entrées et la robustesse face aux requêtes malformées
 */
@WebMvcTest(controllers = {LangueNationaleController.class, InfoPratiqueController.class})
@Import({SecurityConfig.class, FirebaseAuthenticationFilter.class})
public class LanguesAndInfosPratiquesSecurityEmpiricalTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private LangueNationaleService langueService;

    @MockBean
    private InfoPratiqueService infoPratiqueService;

    @MockBean
    private com.maliexplorer_backend.repository.utilisateurRepository userRepository;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    // =========================================================================
    // 1. TESTS LANGUES NATIONALES - ACCÈS PUBLIC ANONYME (HTTP 200)
    // =========================================================================

    @Test
    @DisplayName("1.1 GET /api/langues anonyme retourne HTTP 200 OK")
    void testAnonymousGetPublishedLanguesReturns200() throws Exception {
        LangueResponseDTO dto = LangueResponseDTO.builder()
                .id(1L)
                .nom("Bamanankan (Bambara)")
                .ordreAffichage(1)
                .publie(true)
                .build();
        when(langueService.getPublishedLangues()).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/langues")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nom").value("Bamanankan (Bambara)"))
                .andExpect(jsonPath("$[0].publie").value(true));

        verify(langueService, times(1)).getPublishedLangues();
    }

    @Test
    @DisplayName("1.2 GET /api/langues/{id} anonyme retourne HTTP 200 OK")
    void testAnonymousGetLangueByIdReturns200() throws Exception {
        LangueResponseDTO dto = LangueResponseDTO.builder()
                .id(2L)
                .nom("Fulfulde (Peul)")
                .build();
        when(langueService.getLangueById(2L)).thenReturn(dto);

        mockMvc.perform(get("/api/langues/2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2L))
                .andExpect(jsonPath("$.nom").value("Fulfulde (Peul)"));

        verify(langueService, times(1)).getLangueById(2L);
    }

    // =========================================================================
    // 2. TESTS LANGUES NATIONALES - REJET ANONYME (HTTP 401 ou 403)
    // =========================================================================

    @Test
    @DisplayName("2.1 POST /api/langues anonyme DOIT être rejeté (HTTP 401)")
    void testAnonymousPostLangueReturns401() throws Exception {
        LangueRequestDTO req = LangueRequestDTO.builder()
                .nom("Soninké")
                .ordreAffichage(4)
                .build();

        mockMvc.perform(post("/api/langues")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.error").value("Non autorisé"));

        verify(langueService, never()).createLangue(any());
    }

    @Test
    @DisplayName("2.2 PUT /api/langues/{id} anonyme DOIT être rejeté (HTTP 401)")
    void testAnonymousPutLangueReturns401() throws Exception {
        LangueRequestDTO req = LangueRequestDTO.builder()
                .nom("Soninké Modifié")
                .build();

        mockMvc.perform(put("/api/langues/4")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));

        verify(langueService, never()).updateLangue(any(), any());
    }

    @Test
    @DisplayName("2.3 DELETE /api/langues/{id} anonyme DOIT être rejeté (HTTP 401)")
    void testAnonymousDeleteLangueReturns401() throws Exception {
        mockMvc.perform(delete("/api/langues/4")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));

        verify(langueService, never()).deleteLangue(any());
    }

    @Test
    @DisplayName("2.4 PATCH /api/langues/{id}/toggle-publication anonyme DOIT être rejeté (HTTP 401)")
    void testAnonymousTogglePublicationLangueReturns401() throws Exception {
        mockMvc.perform(patch("/api/langues/4/toggle-publication")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));

        verify(langueService, never()).togglePublication(any());
    }

    @Test
    @DisplayName("2.5 GET /api/langues/all anonyme DOIT être rejeté (HTTP 401 ou 403)")
    void testAnonymousGetAllLanguesReturnsForbiddenOrUnauthorized() throws Exception {
        mockMvc.perform(get("/api/langues/all")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(result -> {
                    int status = result.getResponse().getStatus();
                    org.junit.jupiter.api.Assertions.assertTrue(status == 401 || status == 403,
                            "GET /api/langues/all anonyme doit être rejeté avec 401 ou 403, reçu: " + status);
                });

        verify(langueService, never()).getAllLangues();
    }

    // =========================================================================
    // 3. TESTS LANGUES NATIONALES - REJET TOURISTE NON ADMIN (HTTP 403)
    // =========================================================================

    @Test
    @WithMockUser(username = "touriste", roles = {"TOURISTE"})
    @DisplayName("3.1 POST /api/langues avec ROLE_TOURISTE DOIT retourner HTTP 403 Forbidden")
    void testTouristePostLangueReturns403() throws Exception {
        LangueRequestDTO req = LangueRequestDTO.builder().nom("Tamasheq").build();

        mockMvc.perform(post("/api/langues")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403));

        verify(langueService, never()).createLangue(any());
    }

    @Test
    @WithMockUser(username = "touriste", roles = {"TOURISTE"})
    @DisplayName("3.2 DELETE /api/langues/{id} avec ROLE_TOURISTE DOIT retourner HTTP 403 Forbidden")
    void testTouristeDeleteLangueReturns403() throws Exception {
        mockMvc.perform(delete("/api/langues/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403));

        verify(langueService, never()).deleteLangue(any());
    }

    // =========================================================================
    // 4. TESTS LANGUES NATIONALES - AUTORISATION ADMIN (HTTP 201, 200, 204)
    // =========================================================================

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("4.1 POST /api/langues avec ROLE_ADMIN réussit (HTTP 201 CREATED)")
    void testAdminPostLangueSucceeds() throws Exception {
        LangueRequestDTO req = LangueRequestDTO.builder().nom("Dogon").build();
        LangueResponseDTO res = LangueResponseDTO.builder().id(7L).nom("Dogon").build();
        when(langueService.createLangue(any())).thenReturn(res);

        mockMvc.perform(post("/api/langues")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(7L))
                .andExpect(jsonPath("$.nom").value("Dogon"));

        verify(langueService, times(1)).createLangue(any());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("4.2 PUT /api/langues/{id} avec ROLE_ADMIN réussit (HTTP 200 OK)")
    void testAdminPutLangueSucceeds() throws Exception {
        LangueRequestDTO req = LangueRequestDTO.builder().nom("Dogon Modifié").build();
        LangueResponseDTO res = LangueResponseDTO.builder().id(7L).nom("Dogon Modifié").build();
        when(langueService.updateLangue(eq(7L), any())).thenReturn(res);

        mockMvc.perform(put("/api/langues/7")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nom").value("Dogon Modifié"));

        verify(langueService, times(1)).updateLangue(eq(7L), any());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("4.3 DELETE /api/langues/{id} avec ROLE_ADMIN réussit (HTTP 204 NO_CONTENT)")
    void testAdminDeleteLangueSucceeds() throws Exception {
        doNothing().when(langueService).deleteLangue(7L);

        mockMvc.perform(delete("/api/langues/7")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(langueService, times(1)).deleteLangue(7L);
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("4.4 GET /api/langues/all avec ROLE_ADMIN réussit (HTTP 200 OK)")
    void testAdminGetAllLanguesSucceeds() throws Exception {
        when(langueService.getAllLangues()).thenReturn(List.of());

        mockMvc.perform(get("/api/langues/all")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(langueService, times(1)).getAllLangues();
    }

    // =========================================================================
    // 5. TESTS INFOS PRATIQUES - ACCÈS PUBLIC ANONYME (HTTP 200)
    // =========================================================================

    @Test
    @DisplayName("5.1 GET /api/infos-pratiques anonyme retourne HTTP 200 OK")
    void testAnonymousGetPublishedInfosPratiquesReturns200() throws Exception {
        InfoPratiqueResponseDTO dto = InfoPratiqueResponseDTO.builder()
                .id(1L)
                .titre("Visas et Formalités")
                .categorie("VISAS")
                .publie(true)
                .build();
        when(infoPratiqueService.getPublishedInfosPratiques()).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/infos-pratiques")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].titre").value("Visas et Formalités"))
                .andExpect(jsonPath("$[0].categorie").value("VISAS"));

        verify(infoPratiqueService, times(1)).getPublishedInfosPratiques();
    }

    @Test
    @DisplayName("5.2 GET /api/infos-pratiques/categorie/{cat} anonyme retourne HTTP 200 OK")
    void testAnonymousGetInfosPratiquesByCategorieReturns200() throws Exception {
        when(infoPratiqueService.getInfosPratiquesByCategorie("SANTE")).thenReturn(List.of());

        mockMvc.perform(get("/api/infos-pratiques/categorie/SANTE")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(infoPratiqueService, times(1)).getInfosPratiquesByCategorie("SANTE");
    }

    @Test
    @DisplayName("5.3 GET /api/infos-pratiques/{id} anonyme retourne HTTP 200 OK")
    void testAnonymousGetInfoPratiqueByIdReturns200() throws Exception {
        InfoPratiqueResponseDTO dto = InfoPratiqueResponseDTO.builder()
                .id(3L)
                .titre("Monnaie & Devises")
                .build();
        when(infoPratiqueService.getInfoPratiqueById(3L)).thenReturn(dto);

        mockMvc.perform(get("/api/infos-pratiques/3")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(3L))
                .andExpect(jsonPath("$.titre").value("Monnaie & Devises"));

        verify(infoPratiqueService, times(1)).getInfoPratiqueById(3L);
    }

    // =========================================================================
    // 6. TESTS INFOS PRATIQUES - REJET ANONYME (HTTP 401 ou 403)
    // =========================================================================

    @Test
    @DisplayName("6.1 POST /api/infos-pratiques anonyme DOIT être rejeté (HTTP 401)")
    void testAnonymousPostInfoPratiqueReturns401() throws Exception {
        InfoPratiqueRequestDTO req = InfoPratiqueRequestDTO.builder()
                .titre("Urgences")
                .categorie("URGENCES")
                .build();

        mockMvc.perform(post("/api/infos-pratiques")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));

        verify(infoPratiqueService, never()).createInfoPratique(any());
    }

    @Test
    @DisplayName("6.2 PUT /api/infos-pratiques/{id} anonyme DOIT être rejeté (HTTP 401)")
    void testAnonymousPutInfoPratiqueReturns401() throws Exception {
        InfoPratiqueRequestDTO req = InfoPratiqueRequestDTO.builder()
                .titre("Urgences Modifié")
                .categorie("URGENCES")
                .build();

        mockMvc.perform(put("/api/infos-pratiques/5")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));

        verify(infoPratiqueService, never()).updateInfoPratique(any(), any());
    }

    @Test
    @DisplayName("6.3 DELETE /api/infos-pratiques/{id} anonyme DOIT être rejeté (HTTP 401)")
    void testAnonymousDeleteInfoPratiqueReturns401() throws Exception {
        mockMvc.perform(delete("/api/infos-pratiques/5")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));

        verify(infoPratiqueService, never()).deleteInfoPratique(any());
    }

    @Test
    @DisplayName("6.4 PATCH /api/infos-pratiques/{id}/toggle-publication anonyme DOIT être rejeté (HTTP 401)")
    void testAnonymousTogglePublicationInfoPratiqueReturns401() throws Exception {
        mockMvc.perform(patch("/api/infos-pratiques/5/toggle-publication")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));

        verify(infoPratiqueService, never()).togglePublication(any());
    }

    @Test
    @DisplayName("6.5 GET /api/infos-pratiques/all anonyme DOIT être rejeté (HTTP 401 ou 403)")
    void testAnonymousGetAllInfosPratiquesReturnsForbiddenOrUnauthorized() throws Exception {
        mockMvc.perform(get("/api/infos-pratiques/all")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(result -> {
                    int status = result.getResponse().getStatus();
                    org.junit.jupiter.api.Assertions.assertTrue(status == 401 || status == 403,
                            "GET /api/infos-pratiques/all anonyme doit être rejeté avec 401 ou 403, reçu: " + status);
                });

        verify(infoPratiqueService, never()).getAllInfosPratiques();
    }

    // =========================================================================
    // 7. TESTS INFOS PRATIQUES - REJET TOURISTE NON ADMIN (HTTP 403)
    // =========================================================================

    @Test
    @WithMockUser(username = "touriste", roles = {"TOURISTE"})
    @DisplayName("7.1 POST /api/infos-pratiques avec ROLE_TOURISTE DOIT retourner HTTP 403 Forbidden")
    void testTouristePostInfoPratiqueReturns403() throws Exception {
        InfoPratiqueRequestDTO req = InfoPratiqueRequestDTO.builder()
                .titre("Climat")
                .categorie("CLIMAT")
                .build();

        mockMvc.perform(post("/api/infos-pratiques")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403));

        verify(infoPratiqueService, never()).createInfoPratique(any());
    }

    // =========================================================================
    // 8. TESTS INFOS PRATIQUES - AUTORISATION ADMIN (HTTP 201, 200, 204)
    // =========================================================================

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("8.1 POST /api/infos-pratiques avec ROLE_ADMIN réussit (HTTP 201 CREATED)")
    void testAdminPostInfoPratiqueSucceeds() throws Exception {
        InfoPratiqueRequestDTO req = InfoPratiqueRequestDTO.builder()
                .titre("Urgences Médicales")
                .categorie("URGENCES")
                .build();
        InfoPratiqueResponseDTO res = InfoPratiqueResponseDTO.builder()
                .id(8L)
                .titre("Urgences Médicales")
                .categorie("URGENCES")
                .build();
        when(infoPratiqueService.createInfoPratique(any())).thenReturn(res);

        mockMvc.perform(post("/api/infos-pratiques")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(8L))
                .andExpect(jsonPath("$.titre").value("Urgences Médicales"));

        verify(infoPratiqueService, times(1)).createInfoPratique(any());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("8.2 DELETE /api/infos-pratiques/{id} avec ROLE_ADMIN réussit (HTTP 204 NO_CONTENT)")
    void testAdminDeleteInfoPratiqueSucceeds() throws Exception {
        doNothing().when(infoPratiqueService).deleteInfoPratique(8L);

        mockMvc.perform(delete("/api/infos-pratiques/8")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(infoPratiqueService, times(1)).deleteInfoPratique(8L);
    }

    // =========================================================================
    // 9. TESTS ROBUSTESSE & VALIDATION DTO (@Valid -> HTTP 400)
    // =========================================================================

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("9.1 POST /api/langues avec payload invalide (nom vide) DOIT retourner HTTP 400 Bad Request")
    void testPostLangueWithInvalidPayloadReturns400() throws Exception {
        LangueRequestDTO req = LangueRequestDTO.builder()
                .nom("") // @NotBlank enfreint
                .build();

        mockMvc.perform(post("/api/langues")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());

        verify(langueService, never()).createLangue(any());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("9.2 POST /api/infos-pratiques avec payload invalide (titre/catégorie vides) DOIT retourner HTTP 400 Bad Request")
    void testPostInfoPratiqueWithInvalidPayloadReturns400() throws Exception {
        InfoPratiqueRequestDTO req = InfoPratiqueRequestDTO.builder()
                .titre("") // @NotBlank enfreint
                .categorie("") // @NotBlank enfreint
                .build();

        mockMvc.perform(post("/api/infos-pratiques")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());

        verify(infoPratiqueService, never()).createInfoPratique(any());
    }
}
