package com.maliexplorer_backend.serviceimpl;

import com.maliexplorer_backend.exception.ResourceNotFoundException;
import com.maliexplorer_backend.model.EvenementModel;
import com.maliexplorer_backend.repository.EvenementRepository;
import com.maliexplorer_backend.service.EvenementService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class EvenementServiceImpl implements EvenementService {

    private final EvenementRepository repository;

    @Override
    @Transactional(readOnly = true)
    public List<EvenementModel> getAll() {
        return repository.findAllByOrderByDateSoumissionDesc();
    }

    @Override
    @Transactional(readOnly = true)
    public List<EvenementModel> getByStatut(EvenementModel.Statut statut) {
        return repository.findByStatutOrderByDateSoumissionDesc(statut);
    }

    @Override
    @Transactional(readOnly = true)
    public EvenementModel getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Événement introuvable avec l'id " + id));
    }

    @Override
    public EvenementModel create(EvenementModel evenement) {
        evenement.setId(null);
        evenement.setMotifRejet(null);
        if (evenement.getStatut() == null) evenement.setStatut(EvenementModel.Statut.EN_ATTENTE);
        return repository.save(evenement);
    }

    @Override
    public EvenementModel update(Long id, EvenementModel data) {
        EvenementModel e = getById(id);
        e.setTitre(data.getTitre());
        e.setDescription(data.getDescription());
        e.setNomOrganisateur(data.getNomOrganisateur());
        e.setEmailOrganisateur(data.getEmailOrganisateur());
        e.setTelephoneOrganisateur(data.getTelephoneOrganisateur());
        e.setDateDebut(data.getDateDebut());
        e.setDateFin(data.getDateFin());
        e.setHeureDebut(data.getHeureDebut());
        e.setHeureFin(data.getHeureFin());
        e.setLieu(data.getLieu());
        e.setVille(data.getVille());
        e.setRegion(data.getRegion());
        e.setCategorie(data.getCategorie());
        e.setPrix(data.getPrix());
        e.setImageUrl(data.getImageUrl());
        e.setAfficheUrl(data.getAfficheUrl());
        if (data.getStatut() != null) e.setStatut(data.getStatut());
        return repository.save(e);
    }

    @Override
    public EvenementModel approuver(Long id) {
        EvenementModel e = getById(id);
        e.setStatut(EvenementModel.Statut.APPROUVE);
        e.setMotifRejet(null);
        return repository.save(e);
    }

    @Override
    public EvenementModel rejeter(Long id, String motif) {
        EvenementModel e = getById(id);
        e.setStatut(EvenementModel.Statut.REFUSE);
        e.setMotifRejet(motif);
        return repository.save(e);
    }

    @Override
    public void delete(Long id) {
        repository.delete(getById(id));
    }
}

