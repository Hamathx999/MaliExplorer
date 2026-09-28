package com.maliexplorer_backend.Models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "presidents")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class President {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPresident;

    @Column(nullable = false, length = 100)
    private String prenom;

    @Column(nullable = false, length = 100)
    private String nom;

    private LocalDate dateNaissance;

    private LocalDate dateDeces;

    @Column(length = 100)
    private String periodeMandat;

    @Column(columnDefinition = "TEXT")
    private String biographie;

    @Column(length = 500)
    private String photoUrl;

    private Long idUsers;

    public Long getId() {
        return this.idPresident;
    }

    public void setId(Long id) {
        this.idPresident = id;
    }
}
