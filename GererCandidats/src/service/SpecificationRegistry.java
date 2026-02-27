package service;

import specification.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Registre de spécifications disponibles.
 * Respecte OCP : pour ajouter une nouvelle règle, il suffit de l'enregistrer ici
 * sans modifier Candidate, CandidateService, ni les autres spécifications.
 * Bonus : mécanisme de règles configurables.
 */
public class SpecificationRegistry {

    private final Map<String, Specification> availableSpecifications = new LinkedHashMap<>();

    public SpecificationRegistry() {
        // Enregistrement des 5 spécifications
        register(new AgeSpecification());
        register(new GradeSpecification());
        register(new ExperienceSpecification());
        register(new ScholarshipSpecification());
        register(new HighGradeSpecification());
    }

    /**
     * Enregistre une nouvelle spécification dans le registre.
     * Permet d'ajouter des règles sans modifier le code existant (OCP).
     */
    public void register(Specification spec) {
        availableSpecifications.put(spec.getDescription(), spec);
    }

    /**
     * Retourne toutes les spécifications disponibles.
     */
    public List<Specification> getAllSpecifications() {
        return new ArrayList<>(availableSpecifications.values());
    }

    /**
     * Retourne une spécification par sa description.
     */
    public Specification getByDescription(String description) {
        return availableSpecifications.get(description);
    }
}
