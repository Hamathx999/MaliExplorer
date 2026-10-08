package com.maliexplorer_backend.service;

import com.maliexplorer_backend.model.EvenementModel;

import java.util.List;

public interface EvenementService {

    List<EvenementModel> getAll();

    List<EvenementModel> getByStatut(EvenementModel.Statut statut);

    EvenementModel getById(Long id);

    EvenementModel create(EvenementModel evenement);

    EvenementModel update(Long id, EvenementModel evenement);

    EvenementModel approuver(Long id);

    EvenementModel rejeter(Long id, String motif);

    void delete(Long id);
}

