package service;

import model.Candidate;
import specification.Specification;

import java.util.ArrayList;
import java.util.List;

/**
 * Service métier : filtrage des candidats selon les spécifications sélectionnées.
 * Respecte SRP : uniquement responsable du filtrage.
 * Respecte DIP : dépend de l'abstraction Specification, pas des implémentations concrètes.
 */
public class CandidateService {

    /**
     * Filtre une liste de candidats selon les spécifications fournies (combinaison AND).
     *
     * @param candidates     la liste complète des candidats
     * @param specifications les critères d'éligibilité sélectionnés
     * @return la liste des candidats satisfaisant TOUS les critères
     */
    public List<Candidate> filterCandidates(List<Candidate> candidates, List<Specification> specifications) {
        if (specifications == null || specifications.isEmpty()) {
            return new ArrayList<>(candidates);
        }

        // Combinaison dynamique AND de toutes les spécifications sélectionnées
        Specification combined = combineSpecifications(specifications);

        List<Candidate> result = new ArrayList<>();
        for (Candidate c : candidates) {
            if (combined.isSatisfiedBy(c)) {
                result.add(c);
            }
        }
        return result;
    }

    /**
     * Combine dynamiquement une liste de spécifications avec un ET logique.
     */
    private Specification combineSpecifications(List<Specification> specifications) {
        Specification combined = specifications.get(0);
        for (int i = 1; i < specifications.size(); i++) {
            combined = combined.and(specifications.get(i));
        }
        return combined;
    }

    /**
     * Valide un candidat individuellement (stratégie de validation runtime — Bonus).
     */
    public boolean validateCandidate(Candidate candidate, List<Specification> specifications) {
        if (specifications == null || specifications.isEmpty()) {
            return true;
        }
        Specification combined = combineSpecifications(specifications);
        return combined.isSatisfiedBy(candidate);
    }
}
