package specification;

import model.Candidate;

/**
 * Spécification composite : OU logique entre deux spécifications.
 */
public class OrSpecification implements Specification {
    private final Specification left;
    private final Specification right;

    public OrSpecification(Specification left, Specification right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public boolean isSatisfiedBy(Candidate candidate) {
        return left.isSatisfiedBy(candidate) || right.isSatisfiedBy(candidate);
    }

    @Override
    public String getDescription() {
        return "(" + left.getDescription() + " OU " + right.getDescription() + ")";
    }
}
