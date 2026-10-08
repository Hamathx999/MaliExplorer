package com.maliexplorer_backend.service;

import com.maliexplorer_backend.model.PropositionModel;
import java.util.List;

public interface PropositionService {

    PropositionModel creerProposition(PropositionModel proposition, Long idQuestion);

    List<PropositionModel> obtenirPropositionsParQuestion(Long idQuestion);

    PropositionModel obtenirPropositionParId(Long id);

    PropositionModel modifierProposition(Long id, PropositionModel propositionDetails);

    void supprimerProposition(Long id);
}