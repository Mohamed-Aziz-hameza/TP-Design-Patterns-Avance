package specification;

import model.Candidate;

/**
 * Interface générique du pattern Specification.
 * Respecte ISP : une seule méthode à implémenter.
 * Respecte DIP : le service et l'UI dépendent de cette abstraction, pas des implémentations concrètes.
 */
public interface Specification {
    boolean isSatisfiedBy(Candidate candidate);

    /**
     * Combine cette spécification avec une autre via un ET logique.
     */
    default Specification and(Specification other) {
        return new AndSpecification(this, other);
    }

    /**
     * Combine cette spécification avec une autre via un OU logique.
     */
    default Specification or(Specification other) {
        return new OrSpecification(this, other);
    }

    /**
     * Inverse cette spécification (NON logique).
     */
    default Specification not() {
        return new NotSpecification(this);
    }

    /**
     * Retourne le nom lisible de la spécification (pour l'affichage UI).
     */
    String getDescription();

    /**
     * Retourne le libellé fixe (sans la valeur numérique) pour l'UI.
     */
    default String getLabel() {
        return getDescription();
    }

    /**
     * Indique si cette spécification a un seuil configurable.
     */
    default boolean hasConfigurableThreshold() {
        return false;
    }

    /**
     * Retourne la valeur du seuil actuel.
     */
    default double getThreshold() {
        return 0;
    }

    /**
     * Modifie la valeur du seuil.
     */
    default void setThreshold(double value) {
    }

    /**
     * Retourne la valeur minimale autorisée pour le seuil.
     */
    default double getThresholdMin() {
        return 0;
    }

    /**
     * Retourne la valeur maximale autorisée pour le seuil.
     */
    default double getThresholdMax() {
        return 100;
    }

    /**
     * Retourne le pas d'incrémentation du seuil.
     */
    default double getThresholdStep() {
        return 1;
    }
}
