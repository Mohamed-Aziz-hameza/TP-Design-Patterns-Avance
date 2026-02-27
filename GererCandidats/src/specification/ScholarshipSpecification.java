package specification;

import model.Candidate;

/**
 * Spécification : le candidat doit être boursier.
 */
public class ScholarshipSpecification implements Specification {
    @Override
    public boolean isSatisfiedBy(Candidate candidate) {
        return candidate.isHasScholarship();
    }

    @Override
    public String getDescription() {
        return "Boursier uniquement";
    }
}
