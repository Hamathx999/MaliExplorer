package com.maliexplorer_backend.service;

import com.maliexplorer_backend.dto.AuthResponseDTO;
import com.maliexplorer_backend.dto.LoginRequestDTO;
import com.maliexplorer_backend.dto.RegisterRequestDTO;

public interface AuthService {

    AuthResponseDTO login(LoginRequestDTO requestDTO);

    AuthResponseDTO register(RegisterRequestDTO requestDTO);

    AuthResponseDTO getCurrentUser();
}
