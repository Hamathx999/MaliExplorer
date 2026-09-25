package com.maliexplorer_backend.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Gestion des erreurs de validation (@Valid : @NotBlank, @Email, etc.)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> erreurs = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            erreurs.put(error.getField(), error.getDefaultMessage());
        }
        return new ResponseEntity<>(erreurs, HttpStatus.BAD_REQUEST);
    }

    // Gestion des arguments invalides (ex: email déjà pris)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgumentException(IllegalArgumentException ex) {
        Map<String, String> erreur = new HashMap<>();
        erreur.put("erreur", ex.getMessage());
        return new ResponseEntity<>(erreur, HttpStatus.BAD_REQUEST);
    }

    // Gestion des éléments non trouvés ou erreurs courantes
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntimeException(RuntimeException ex) {
        Map<String, String> erreur = new HashMap<>();
        erreur.put("erreur", ex.getMessage());
        return new ResponseEntity<>(erreur, HttpStatus.NOT_FOUND);
    }
}
