package com.maliexplorer_backend.service;

import com.maliexplorer_backend.dto.AuthResponseDTO;
import com.maliexplorer_backend.dto.RegisterRequestDTO;
import com.maliexplorer_backend.exception.BadRequestException;
import com.maliexplorer_backend.model.RoleModel;
import com.maliexplorer_backend.model.utilisateurModel;
import com.maliexplorer_backend.repository.utilisateurRepository;
import com.maliexplorer_backend.serviceimpl.AuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceRegistrationTest {

    @Mock
    private utilisateurRepository userRepository;

    @InjectMocks
    private AuthServiceImpl authService;

    private RegisterRequestDTO baseRequest;

    @BeforeEach
    void setUp() {
        baseRequest = RegisterRequestDTO.builder()
                .prenom("Moussa")
                .nom("Diakité")
                .email("moussa@example.com")
                .build();
    }

    @Test
    @DisplayName("Devrait attribuer automatiquement le rôle TOURISTE si aucun rôle n'est spécifié")
    void testInscriptionDefautTouriste() {
        when(userRepository.existsByEmail("moussa@example.com")).thenReturn(false);
        when(userRepository.save(any(utilisateurModel.class))).thenAnswer(invocation -> {
            utilisateurModel u = invocation.getArgument(0);
            u.setIdUsers(10);
            return u;
        });

        baseRequest.setRole(null);
        AuthResponseDTO response = authService.register(baseRequest);

        assertNotNull(response);
        assertEquals(RoleModel.touriste, response.getRole());

        ArgumentCaptor<utilisateurModel> captor = ArgumentCaptor.forClass(utilisateurModel.class);
        verify(userRepository).save(captor.capture());
        assertEquals(RoleModel.touriste, captor.getValue().getRole());
    }

    @Test
    @DisplayName("Devrait accepter les rôles publics autorisés : guide, artisan, promoteur")
    void testInscriptionRolesPublicsAutorises() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.save(any(utilisateurModel.class))).thenAnswer(invocation -> {
            utilisateurModel u = invocation.getArgument(0);
            u.setIdUsers(11);
            return u;
        });

        RoleModel[] allowedRoles = {RoleModel.touriste, RoleModel.guide, RoleModel.artisan, RoleModel.promoteur};

        for (RoleModel role : allowedRoles) {
            baseRequest.setRole(role);
            baseRequest.setEmail("user_" + role.name() + "@example.com");

            AuthResponseDTO response = authService.register(baseRequest);
            assertNotNull(response);
            assertEquals(role, response.getRole());
        }
    }

    @Test
    @DisplayName("Devrait STRICTEMENT INTERDIRE l'inscription directe avec le rôle ADMIN")
    void testInscriptionRefuseRoleAdmin() {
        when(userRepository.existsByEmail("moussa@example.com")).thenReturn(false);
        baseRequest.setRole(RoleModel.admin);

        BadRequestException ex = assertThrows(BadRequestException.class, () -> authService.register(baseRequest));
        assertTrue(ex.getMessage().contains("strictement interdite"));
        verify(userRepository, never()).save(any(utilisateurModel.class));
    }

    @Test
    @DisplayName("Devrait STRICTEMENT INTERDIRE l'inscription directe avec le rôle SUPER_ADMIN")
    void testInscriptionRefuseRoleSuperAdmin() {
        when(userRepository.existsByEmail("moussa@example.com")).thenReturn(false);
        baseRequest.setRole(RoleModel.superAdmin);

        BadRequestException ex = assertThrows(BadRequestException.class, () -> authService.register(baseRequest));
        assertTrue(ex.getMessage().contains("strictement interdite"));
        verify(userRepository, never()).save(any(utilisateurModel.class));
    }

    @Test
    @DisplayName("Devrait refuser l'inscription si l'email existe déjà")
    void testInscriptionEmailExistant() {
        when(userRepository.existsByEmail("moussa@example.com")).thenReturn(true);

        BadRequestException ex = assertThrows(BadRequestException.class, () -> authService.register(baseRequest));
        assertTrue(ex.getMessage().contains("existe déjà"));
        verify(userRepository, never()).save(any(utilisateurModel.class));
    }
}
