package specification;

import model.Candidate;

/**
 * Spécification bonus : la note du candidat doit être >= seuil configurable (mention Bien par défaut).
 * Démontre l'OCP : ajout d'une nouvelle règle sans modifier Candidate, le filtre, ni les autres spécifications.
 */
public class HighGradeSpecification implements Specification {
    private double threshold = 14.0;

    public HighGradeSpecification() {}

    public HighGradeSpecification(double threshold) {
        this.threshold = threshold;
    }

    @Override
    public boolean isSatisfiedBy(Candidate candidate) {
        return candidate.getGrade() >= threshold;
    }

    @Override
    public String getDescription() {
        return "Note >= " + threshold + " (Mention)";
    }

    @Override
    public String getLabel() {
        return "Note mention minimum";
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
