package com.maliexplorer_backend.repository;

import com.maliexplorer_backend.model.ImageModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImageRepository extends JpaRepository<ImageModel, Long> {

    List<ImageModel> findByEntiteTypeAndEntiteId(String entiteType, Long entiteId);

    List<ImageModel> findByEntiteType(String entiteType);
}

