package model;

/**
 * Classe métier représentant un candidat à une formation.
 * Respecte SRP : ne contient que les données du candidat, aucune logique de validation.
 */
public class Candidate {
    private String name;
    private int age;
    private double grade;
    private int experienceYears;
    private boolean hasScholarship;

    public Candidate() {}

    public Candidate(String name, int age, double grade, int experienceYears, boolean hasScholarship) {
        this.name = name;
        this.age = age;
        this.grade = grade;
        this.experienceYears = experienceYears;
        this.hasScholarship = hasScholarship;
    }

    // --- Getters & Setters ---

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public double getGrade() {
        return grade;
    }

    public void setGrade(double grade) {
        this.grade = grade;
    }

    public int getExperienceYears() {
        return experienceYears;
    }

    public void setExperienceYears(int experienceYears) {
        this.experienceYears = experienceYears;
    }

    public boolean isHasScholarship() {
        return hasScholarship;
    }

    public void setHasScholarship(boolean hasScholarship) {
        this.hasScholarship = hasScholarship;
    }

    @Override
    public String toString() {
        return name + " | Âge: " + age + " | Note: " + grade
                + " | Exp: " + experienceYears + " ans"
                + " | Boursier: " + (hasScholarship ? "Oui" : "Non");
    }
}
