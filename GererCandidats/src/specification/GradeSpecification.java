package specification;

import model.Candidate;

/**
 * Spécification : la note du candidat doit être >= seuil configurable.
 */
public class GradeSpecification implements Specification {
    private double threshold = 12.0;

    public GradeSpecification() {}

    public GradeSpecification(double threshold) {
        this.threshold = threshold;
    }

    @Override
    public boolean isSatisfiedBy(Candidate candidate) {
        return candidate.getGrade() >= threshold;
    }

    @Override
    public String getDescription() {
        return "Note >= " + threshold;
    }

    @Override
    public String getLabel() {
        return "Note minimum";
    }

    @Override
    public boolean hasConfigurableThreshold() { return true; }

    @Override
    public double getThreshold() { return threshold; }

    @Override
    public void setThreshold(double value) { this.threshold = value; }

    @Override
    public double getThresholdMin() { return 0; }

    @Override
    public double getThresholdMax() { return 20; }

    @Override
    public double getThresholdStep() { return 0.5; }
}
