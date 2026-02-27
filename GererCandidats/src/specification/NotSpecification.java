package specification;

import model.Candidate;

/**
 * Spécification composite : NON logique (inverse).
 */
public class NotSpecification implements Specification {
    private final Specification spec;

    public NotSpecification(Specification spec) {
        this.spec = spec;
    }

    @Override
    public boolean isSatisfiedBy(Candidate candidate) {
        return !spec.isSatisfiedBy(candidate);
    }

    @Override
    public String getDescription() {
        return "NON(" + spec.getDescription() + ")";
    }
}
