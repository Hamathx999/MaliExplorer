package com.maliexplorer_backend.Models;

import com.maliexplorer_backend.Models.utilisateurModel;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "artisans")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Artisan extends Utilisateur {

    @NotBlank(message = "Le type d'artisanat est obligatoire !")
    private String typeArtisanat;
}
