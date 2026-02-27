package specification;

import model.Candidate;

/**
 * Spécification composite : ET logique entre deux spécifications.
 */
public class AndSpecification implements Specification {
    private final Specification left;
    private final Specification right;

    public AndSpecification(Specification left, Specification right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public boolean isSatisfiedBy(Candidate candidate) {
        return left.isSatisfiedBy(candidate) && right.isSatisfiedBy(candidate);
    }

    @Override
    public String getDescription() {
        return "(" + left.getDescription() + " ET " + right.getDescription() + ")";
    }
}
