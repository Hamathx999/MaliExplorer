package com.maliexplorer_backend.serviceimpl;

import com.maliexplorer_backend.model.PropositionModel;
import com.maliexplorer_backend.model.QuestionModel;
import com.maliexplorer_backend.repository.PropositionRepository;
import com.maliexplorer_backend.repository.QuestionRepository;
import com.maliexplorer_backend.service.PropositionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PropositionServiceImpl implements PropositionService {

    private final PropositionRepository propositionRepository;
    private final QuestionRepository questionRepository;

    @Override
    public PropositionModel creerProposition(PropositionModel proposition, Long idQuestion) {
        QuestionModel question = questionRepository.findById(idQuestion)
                .orElseThrow(() -> new RuntimeException("Question non trouvée avec l'ID : " + idQuestion));

        proposition.setQuestion(question);
        return propositionRepository.save(proposition);
    }

    @Override
    public List<PropositionModel> ObtenirPropositionsParQuestion(Long idQuestion) {
        return propositionRepository.findByQuestionIdQuestion(idQuestion);
    }

    @Override
    public PropositionModel obtenirPropositionParId(Long id) {
        return propositionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Proposition non trouvée avec l'ID : " + id));
    }

    @Override
    public PropositionModel modifierProposition(Long id, PropositionModel propositionDetails) {
        PropositionModel proposition = obtenirPropositionParId(id);
        proposition.setNomProposition(propositionDetails.getNomProposition());
        return propositionRepository.save(proposition);
    }

    @Override
    public void supprimerProposition(Long id) {
        PropositionModel proposition = obtenirPropositionParId(id);
        propositionRepository.delete(proposition);
    }
}