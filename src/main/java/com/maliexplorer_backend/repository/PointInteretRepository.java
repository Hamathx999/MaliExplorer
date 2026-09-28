package com.maliexplorer_backend.repository;

import com.maliexplorer_backend.model.PointInteret;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PointInteretRepository extends JpaRepository<PointInteret, Long> {

    List<PointInteret> findByTypeIgnoreCase(String type);

    List<PointInteret> findByRegionId(Long regionId);
}
