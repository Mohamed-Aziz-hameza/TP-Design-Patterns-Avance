package specification;

import model.Candidate;

/**
 * Spécification : le candidat doit avoir une expérience >= seuil configurable.
 */
public class ExperienceSpecification implements Specification {
    private double threshold = 2;

    public ExperienceSpecification() {}

    public ExperienceSpecification(double threshold) {
        this.threshold = threshold;
    }

    @Override
    public boolean isSatisfiedBy(Candidate candidate) {
        return candidate.getExperienceYears() >= threshold;
    }

    @Override
    public String getDescription() {
        return "Expérience >= " + (int) threshold + " ans";
    }

    @Override
    public String getLabel() {
        return "Expérience minimum (ans)";
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
    public double getThresholdMax() { return 50; }

    @Override
    public double getThresholdStep() { return 1; }
}
