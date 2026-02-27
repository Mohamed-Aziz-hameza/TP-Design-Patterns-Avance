package specification;

import model.Candidate;

/**
 * Spécification : le candidat doit avoir un âge >= seuil configurable.
 */
public class AgeSpecification implements Specification {
    private double threshold = 18;

    public AgeSpecification() {}

    public AgeSpecification(double threshold) {
        this.threshold = threshold;
    }

    @Override
    public boolean isSatisfiedBy(Candidate candidate) {
        return candidate.getAge() >= threshold;
    }

    @Override
    public String getDescription() {
        return "Âge >= " + (int) threshold;
    }

    @Override
    public String getLabel() {
        return "Âge minimum";
    }

    @Override
    public boolean hasConfigurableThreshold() { return true; }

    @Override
    public double getThreshold() { return threshold; }

    @Override
    public void setThreshold(double value) { this.threshold = value; }

    @Override
    public double getThresholdMin() { return 10; }

    @Override
    public double getThresholdMax() { return 99; }

    @Override
    public double getThresholdStep() { return 1; }
}
