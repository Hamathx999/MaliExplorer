package com.maliexplorer_backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "guides")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class GuideModel extends utilisateurModel {

    @Min(value = 0, message = "L'expérience ne peut pas être négative !")
    private int experience;

    @Column(columnDefinition = "TEXT")
    private String description;

    @NotBlank(message = "La langue parlée est obligatoire !")
    private String langue;

    private String pieceIdentite;

    private Integer idAdministrateur;
}
