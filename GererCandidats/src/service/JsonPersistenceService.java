package service;

import model.Candidate;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Service de persistance JSON (Bonus).
 * Sauvegarde et charge les candidats dans un fichier JSON simple.
 * Respecte SRP : uniquement responsable de la persistance.
 */
public class JsonPersistenceService {

    private static final String DEFAULT_FILE = "candidats.json";
    private static final String SAVES_DIR = "saves";

    /**
     * Assure que le dossier de sauvegardes existe.
     */
    private void ensureSavesDir() {
        File dir = new File(SAVES_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    /**
     * Sauvegarde avec un nom personnalisé dans le dossier saves/.
     */
    public void saveNamed(List<Candidate> candidates, String saveName) throws IOException {
        ensureSavesDir();
        String fileName = SAVES_DIR + File.separator + saveName + ".json";
        save(candidates, fileName);
    }

    /**
     * Charge depuis un fichier nommé dans le dossier saves/.
     */
    public List<Candidate> loadNamed(String saveName) throws IOException {
        String fileName = SAVES_DIR + File.separator + saveName + ".json";
        return load(fileName);
    }

    /**
     * Retourne la liste des noms de sauvegardes disponibles (sans extension).
     */
    public List<String> listSaves() {
        ensureSavesDir();
        List<String> names = new ArrayList<>();
        File dir = new File(SAVES_DIR);
        File[] files = dir.listFiles((d, name) -> name.endsWith(".json"));
        if (files != null) {
            for (File f : files) {
                String name = f.getName();
                names.add(name.substring(0, name.length() - 5)); // enlever .json
            }
        }
        return names;
    }

    /**
     * Sauvegarde la liste des candidats en JSON.
     */
    public void save(List<Candidate> candidates, String filePath) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            writer.println("[");
            for (int i = 0; i < candidates.size(); i++) {
                Candidate c = candidates.get(i);
                writer.println("  {");
                writer.println("    \"name\": \"" + escapeJson(c.getName()) + "\",");
                writer.println("    \"age\": " + c.getAge() + ",");
                writer.println("    \"grade\": " + c.getGrade() + ",");
                writer.println("    \"experienceYears\": " + c.getExperienceYears() + ",");
                writer.println("    \"hasScholarship\": " + c.isHasScholarship());
                writer.print("  }");
                if (i < candidates.size() - 1) {
                    writer.println(",");
                } else {
                    writer.println();
                }
            }
            writer.println("]");
        }
    }

    /**
     * Sauvegarde dans le fichier par défaut.
     */
    public void save(List<Candidate> candidates) throws IOException {
        save(candidates, DEFAULT_FILE);
    }

    /**
     * Charge les candidats depuis un fichier JSON.
     */
    public List<Candidate> load(String filePath) throws IOException {
        List<Candidate> candidates = new ArrayList<>();
        File file = new File(filePath);
        if (!file.exists()) {
            return candidates;
        }

        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line.trim());
            }
        }

        String json = content.toString();
        // Parsing JSON simple (sans bibliothèque externe)
        json = json.trim();
        if (json.startsWith("[")) json = json.substring(1);
        if (json.endsWith("]")) json = json.substring(0, json.length() - 1);

        // Découper par objets
        String[] objects = json.split("\\},\\s*\\{");
        for (String obj : objects) {
            obj = obj.replace("{", "").replace("}", "").trim();
            if (obj.isEmpty()) continue;

            Candidate c = parseCandidate(obj);
            if (c != null) {
                candidates.add(c);
            }
        }
        return candidates;
    }

    /**
     * Charge depuis le fichier par défaut.
     */
    public List<Candidate> load() throws IOException {
        return load(DEFAULT_FILE);
    }

    private Candidate parseCandidate(String jsonObject) {
        try {
            String name = extractStringValue(jsonObject, "name");
            int age = Integer.parseInt(extractValue(jsonObject, "age").trim());
            double grade = Double.parseDouble(extractValue(jsonObject, "grade").trim());
            int exp = Integer.parseInt(extractValue(jsonObject, "experienceYears").trim());
            boolean scholarship = Boolean.parseBoolean(extractValue(jsonObject, "hasScholarship").trim());
            return new Candidate(name, age, grade, exp, scholarship);
        } catch (Exception e) {
            System.err.println("Erreur de parsing JSON : " + e.getMessage());
            return null;
        }
    }

    private String extractStringValue(String json, String key) {
        String pattern = "\"" + key + "\"\\s*:\\s*\"";
        int start = json.indexOf(pattern.replace("\\s*", ""));
        // Approche simplifiée
        String search = "\"" + key + "\":";
        int idx = json.indexOf(search);
        if (idx == -1) {
            search = "\"" + key + "\" :";
            idx = json.indexOf(search);
        }
        if (idx == -1) return "";

        int valueStart = json.indexOf("\"", idx + search.length());
        if (valueStart == -1) return "";
        int valueEnd = json.indexOf("\"", valueStart + 1);
        if (valueEnd == -1) return "";
        return json.substring(valueStart + 1, valueEnd);
    }

    private String extractValue(String json, String key) {
        String search = "\"" + key + "\":";
        int idx = json.indexOf(search);
        if (idx == -1) {
            search = "\"" + key + "\" :";
            idx = json.indexOf(search);
        }
        if (idx == -1) return "0";

        int valueStart = idx + search.length();
        // Trouver la fin de la valeur (virgule, fin de chaîne, ou })
        int comma = json.indexOf(",", valueStart);
        int brace = json.indexOf("}", valueStart);
        int end = json.length();
        if (comma != -1 && comma < end) end = comma;
        if (brace != -1 && brace < end) end = brace;
        return json.substring(valueStart, end).trim().replace("\"", "");
    }

    private String escapeJson(String value) {
        if (value == null) return "";
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
