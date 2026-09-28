package com.maliexplorer_backend.model;


import java.util.List;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.JoinTable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "touristes")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Touriste extends Utilisateur {

    private Long idTouriste;

    private int points = 0;

    @ManyToMany
    @JoinTable(name = "touriste_article", joinColumns = @JoinColumn(name = "id_touriste"), inverseJoinColumns = @JoinColumn(name = "id_article"))
    private List<Article> articlesLus;

    @ManyToMany
    @JoinTable(name = "touriste_lieu_historique", joinColumns = @JoinColumn(name = "id_touriste"), inverseJoinColumns = @JoinColumn(name = "id_lieu"))
    private List<LieuHistorique> lieuxVisites;

    @ManyToMany
    @JoinTable(name = "touriste_ville", joinColumns = @JoinColumn(name = "id_touriste"), inverseJoinColumns = @JoinColumn(name = "id_ville"))
    private List<Ville> villesVisitees;

    @ManyToMany
    @JoinTable(name = "touriste_ethnie", joinColumns = @JoinColumn(name = "id_touriste"), inverseJoinColumns = @JoinColumn(name = "id_ethnie"))
    private List<Ethnie> ethniesVues;

    @ManyToMany
    @JoinTable(name = "touriste_plat", joinColumns = @JoinColumn(name = "id_touriste"), inverseJoinColumns = @JoinColumn(name = "id_plat"))
    private List<Plat> platsVus;

    @ManyToMany
    @JoinTable(name = "touriste_quiz", joinColumns = @JoinColumn(name = "id_touriste"), inverseJoinColumns = @JoinColumn(name = "id_quiz"))
    private List<Quiz> quizJoues;


    public Touriste(String prenom, String nom, String email, String motDePasse, String adresse, String photoUrl,
            int points) {
        setPrenom(prenom);
        setNom(nom);
        setEmail(email);
        setMotDePasse(motDePasse);
        setAdresse(adresse);
        setPhotoUrl(photoUrl);
        setRole(Role.touriste);
        this.points = points;
    }
}
