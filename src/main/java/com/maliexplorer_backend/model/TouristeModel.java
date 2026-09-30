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
public class TouristeModel extends utilisateurModel {

    private Long idTouriste;

    @ManyToMany
    @JoinTable(name = "touriste_article", joinColumns = @JoinColumn(name = "id_touriste"), inverseJoinColumns = @JoinColumn(name = "id_article"))
    private List<ArticleModel> articlesLus;

    @ManyToMany
    @JoinTable(name = "touriste_lieu_historique", joinColumns = @JoinColumn(name = "id_touriste"), inverseJoinColumns = @JoinColumn(name = "id_lieu"))
    private List<LieuHistoriqueModel> lieuxVisites;

    @ManyToMany
    @JoinTable(name = "touriste_ville", joinColumns = @JoinColumn(name = "id_touriste"), inverseJoinColumns = @JoinColumn(name = "id_ville"))
    private List<VilleModel> villesVisitees;

    @ManyToMany
    @JoinTable(name = "touriste_ethnie", joinColumns = @JoinColumn(name = "id_touriste"), inverseJoinColumns = @JoinColumn(name = "id_ethnie"))
    private List<EthnieModel> ethniesVues;

    @ManyToMany
    @JoinTable(name = "touriste_plat", joinColumns = @JoinColumn(name = "id_touriste"), inverseJoinColumns = @JoinColumn(name = "id_plat"))
    private List<PlatModel> platsVus;

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
