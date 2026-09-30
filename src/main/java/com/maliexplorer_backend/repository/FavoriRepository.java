package com.maliexplorer_backend.repository;

import com.maliexplorer_backend.model.FavoriModel;
import com.maliexplorer_backend.model.TypeFavori;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriRepository extends JpaRepository<FavoriModel, Long> {

    List<FavoriModel> findByUtilisateurIdUsersOrderByDateAjoutDesc(Integer idUsers);

    List<FavoriModel> findByUtilisateurIdUsersAndTypeContenuOrderByDateAjoutDesc(Integer idUsers, TypeFavori typeContenu);

    Optional<FavoriModel> findByUtilisateurIdUsersAndTypeContenuAndLieuIdLieu(Integer idUsers, TypeFavori typeContenu, Long idLieu);

    Optional<FavoriModel> findByUtilisateurIdUsersAndTypeContenuAndArticleIdArticle(Integer idUsers, TypeFavori typeContenu, Long idArticle);

    boolean existsByUtilisateurIdUsersAndTypeContenuAndLieuIdLieu(Integer idUsers, TypeFavori typeContenu, Long idLieu);

    boolean existsByUtilisateurIdUsersAndTypeContenuAndArticleIdArticle(Integer idUsers, TypeFavori typeContenu, Long idArticle);

    void deleteByUtilisateurIdUsersAndTypeContenuAndLieuIdLieu(Integer idUsers, TypeFavori typeContenu, Long idLieu);

    void deleteByUtilisateurIdUsersAndTypeContenuAndArticleIdArticle(Integer idUsers, TypeFavori typeContenu, Long idArticle);
}
