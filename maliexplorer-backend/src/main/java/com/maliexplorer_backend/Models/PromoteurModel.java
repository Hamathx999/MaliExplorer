package com.maliexplorer_backend.Models;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "promoteurs")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class PromoteurModel extends utilisateurModel {

    @NotBlank(message = "Le nom de l'organisation est obligatoire !")
    private String nomOrganisation;

    private String pieceIdentite;
}
