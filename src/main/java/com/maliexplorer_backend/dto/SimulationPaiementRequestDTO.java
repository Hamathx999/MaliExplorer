package com.maliexplorer_backend.dto;

import com.maliexplorer_backend.model.CommandeBilletSimulationModel;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SimulationPaiementRequestDTO {

    @NotNull(message = "L'ID de l'événement est obligatoire")
    private Long idEvenement;

    @Min(value = 1, message = "La quantité minimale est de 1 billet")
    private int quantite;

    @NotNull(message = "Le canal de paiement est obligatoire (ORANGE_MONEY, WAVE, MASTERCARD)")
    private CommandeBilletSimulationModel.CanalPaiement canalPaiement;

    private String telephone;
    private String nomAcheteur;
    private String emailAcheteur;
}

