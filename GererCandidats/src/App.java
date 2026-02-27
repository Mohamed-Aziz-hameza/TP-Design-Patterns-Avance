import service.CandidateService;
import service.JsonPersistenceService;
import service.PdfExportService;
import service.SpecificationRegistry;
import ui.MainWindow;

import javax.swing.*;

/**
 * Point d'entrée de l'application.
 * Respecte DIP : instancie les dépendances et les injecte dans l'UI.
 */
public class App {
    public static void main(String[] args) {
        // Injection des dépendances
        CandidateService candidateService = new CandidateService();
        SpecificationRegistry specificationRegistry = new SpecificationRegistry();
        JsonPersistenceService persistenceService = new JsonPersistenceService();
        PdfExportService pdfExportService = new PdfExportService();

        // Lancement de l'interface sur le thread EDT de Swing
        SwingUtilities.invokeLater(() -> {
            MainWindow window = new MainWindow(candidateService, specificationRegistry, persistenceService, pdfExportService);
            window.setVisible(true);
        });
    }
}
