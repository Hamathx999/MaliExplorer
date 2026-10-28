package com.maliexplorer_backend.model;


import java.util.List;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.JoinTable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "touristes")
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class TouristeModel extends utilisateurModel {

    public TouristeModel() {
        super();
    }

    private Long idTouriste;

    @com.fasterxml.jackson.annotation.JsonIgnore
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @ManyToMany
    @JoinTable(name = "touriste_article", joinColumns = @JoinColumn(name = "id_touriste"), inverseJoinColumns = @JoinColumn(name = "id_article"))
    private List<ArticleModel> articlesLus;

    @com.fasterxml.jackson.annotation.JsonIgnore
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @ManyToMany
    @JoinTable(name = "touriste_lieu_historique", joinColumns = @JoinColumn(name = "id_touriste"), inverseJoinColumns = @JoinColumn(name = "id_lieu"))
    private List<LieuHistoriqueModel> lieuxVisites;

    @com.fasterxml.jackson.annotation.JsonIgnore
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @ManyToMany
    @JoinTable(name = "touriste_ville", joinColumns = @JoinColumn(name = "id_touriste"), inverseJoinColumns = @JoinColumn(name = "id_ville"))
    private List<VilleModel> villesVisitees;

    @com.fasterxml.jackson.annotation.JsonIgnore
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @ManyToMany
    @JoinTable(name = "touriste_ethnie", joinColumns = @JoinColumn(name = "id_touriste"), inverseJoinColumns = @JoinColumn(name = "id_ethnie"))
    private List<EthnieModel> ethniesVues;

    @com.fasterxml.jackson.annotation.JsonIgnore
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @ManyToMany
    @JoinTable(name = "touriste_plat", joinColumns = @JoinColumn(name = "id_touriste"), inverseJoinColumns = @JoinColumn(name = "id_plat"))
    private List<PlatModel> platsVus;

    @com.fasterxml.jackson.annotation.JsonIgnore
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @ManyToMany
    @JoinTable(name = "touriste_quiz", joinColumns = @JoinColumn(name = "id_touriste"), inverseJoinColumns = @JoinColumn(name = "id_quiz"))
    private List<QuizModel> quizJoues;


    public TouristeModel(String prenom, String nom, String email, String adresse, String photoUrl,
            int points) {
        setPrenom(prenom);
        setNom(nom);
        setEmail(email);
        setAdresse(adresse);
        setPhotoUrl(photoUrl);
        setRole(RoleModel.touriste);
        setPoints(points);
    }
}
