package service;

import model.Candidate;

import java.awt.*;
import java.awt.print.*;
import java.io.*;
import java.util.List;
import javax.print.*;
import javax.print.attribute.*;
import javax.print.attribute.standard.*;

/**
 * Service d'export PDF des candidats.
 * Utilise javax.print pour générer un fichier PDF sans bibliothèque externe.
 * Respecte SRP : uniquement responsable de la génération PDF.
 */
public class PdfExportService {

    /**
     * Exporte une liste de candidats dans un fichier PDF.
     *
     * @param candidates la liste des candidats à exporter
     * @param title      le titre affiché dans le PDF
     * @param outputFile le chemin du fichier PDF de sortie
     */
    public void exportToPdf(List<Candidate> candidates, String title, String outputFile) throws PrinterException, IOException {
        // Créer un Printable qui dessine le tableau des candidats
        Printable printable = new CandidatePrintable(candidates, title);

        // Configuration du format de page A4
        PrinterJob printerJob = PrinterJob.getPrinterJob();
        PageFormat pageFormat = printerJob.defaultPage();
        Paper paper = new Paper();
        double a4Width = 595.0;  // A4 en points (72 dpi)
        double a4Height = 842.0;
        paper.setSize(a4Width, a4Height);
        paper.setImageableArea(36, 36, a4Width - 72, a4Height - 72); // marges 0.5 inch
        pageFormat.setPaper(paper);

        // Calculer le nombre de pages nécessaires
        int candidatesPerPage = 25;
        int totalPages = Math.max(1, (int) Math.ceil((double) candidates.size() / candidatesPerPage));

        Book book = new Book();
        book.append(printable, pageFormat, totalPages);
        printerJob.setPageable(book);

        // Chercher le service d'impression PDF
        PrintService[] services = PrintServiceLookup.lookupPrintServices(null, null);
        PrintService pdfService = null;

        // Utiliser un StreamPrintService pour écrire en fichier
        File file = new File(outputFile);
        FileOutputStream fos = new FileOutputStream(file);

        StreamPrintServiceFactory[] factories = StreamPrintServiceFactory.lookupStreamPrintServiceFactories(
                DocFlavor.SERVICE_FORMATTED.PRINTABLE, "application/pdf");

        if (factories.length > 0) {
            StreamPrintService sps = factories[0].getPrintService(fos);
            printerJob.setPrintService(sps);
            printerJob.print();
            fos.close();
        } else {
            fos.close();
            // Fallback : générer un PDF manuellement (format PDF brut)
            generateRawPdf(candidates, title, file);
        }
    }

    /**
     * Génère un PDF manuellement en écrivant la structure PDF brute.
     * Solution de secours si aucun service d'impression PDF n'est disponible.
     */
    private void generateRawPdf(List<Candidate> candidates, String title, File outputFile) throws IOException {
        try (RandomAccessFile raf = new RandomAccessFile(outputFile, "rw")) {
            // On va construire le PDF en mémoire puis l'écrire
            StringBuilder pdf = new StringBuilder();
            java.util.List<Long> objectOffsets = new java.util.ArrayList<>();

            // Header
            pdf.append("%PDF-1.4\n");

            // Object 1 : Catalogue
            objectOffsets.add((long) pdf.length());
            pdf.append("1 0 obj\n<< /Type /Catalog /Pages 2 0 R >>\nendobj\n");

            // Object 2 : Pages
            objectOffsets.add((long) pdf.length());

            // Calculer le nombre de pages
            int linesPerPage = 30;
            int totalPages = Math.max(1, (int) Math.ceil((double) (candidates.size() + 3) / linesPerPage));
            StringBuilder pageRefs = new StringBuilder();
            for (int p = 0; p < totalPages; p++) {
                if (p > 0) pageRefs.append(" ");
                pageRefs.append((5 + p) + " 0 R");
            }

            pdf.append("2 0 obj\n<< /Type /Pages /Kids [" + pageRefs + "] /Count " + totalPages + " >>\nendobj\n");

            // Object 3 : Font
            objectOffsets.add((long) pdf.length());
            pdf.append("3 0 obj\n<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica >>\nendobj\n");

            // Object 4 : Font Bold
            objectOffsets.add((long) pdf.length());
            pdf.append("4 0 obj\n<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica-Bold >>\nendobj\n");

            // Générer chaque page
            for (int pageNum = 0; pageNum < totalPages; pageNum++) {
                int startIdx = pageNum * linesPerPage;
                int endIdx = Math.min(startIdx + linesPerPage, candidates.size());

                // Construire le contenu de la page
                StringBuilder stream = new StringBuilder();

                // Titre (seulement première page)
                double yPos = 780;
                if (pageNum == 0) {
                    stream.append("BT /F2 18 Tf 36 " + yPos + " Td (" + escapePdfString(title) + ") Tj ET\n");
                    yPos -= 30;

                    // Date
                    stream.append("BT /F1 9 Tf 36 " + yPos + " Td (Date d'export : " +
                            new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm").format(new java.util.Date()) +
                            ") Tj ET\n");
                    yPos -= 20;

                    // Ligne de séparation
                    stream.append("0.53 0.71 0.98 RG 1 w 36 " + yPos + " m 559 " + yPos + " l S\n");
                    yPos -= 20;

                    // En-têtes du tableau
                    stream.append("BT /F2 10 Tf 36 " + yPos + " Td (Nom) Tj ET\n");
                    stream.append("BT /F2 10 Tf 180 " + yPos + " Td (Age) Tj ET\n");
                    stream.append("BT /F2 10 Tf 250 " + yPos + " Td (Note) Tj ET\n");
                    stream.append("BT /F2 10 Tf 330 " + yPos + " Td (Experience) Tj ET\n");
                    stream.append("BT /F2 10 Tf 450 " + yPos + " Td (Boursier) Tj ET\n");
                    yPos -= 5;

                    // Ligne sous en-têtes
                    stream.append("0.3 0.3 0.3 RG 0.5 w 36 " + yPos + " m 559 " + yPos + " l S\n");
                    yPos -= 15;
                } else {
                    // En-têtes pour les pages suivantes
                    stream.append("BT /F2 10 Tf 36 " + yPos + " Td (Nom) Tj ET\n");
                    stream.append("BT /F2 10 Tf 180 " + yPos + " Td (Age) Tj ET\n");
                    stream.append("BT /F2 10 Tf 250 " + yPos + " Td (Note) Tj ET\n");
                    stream.append("BT /F2 10 Tf 330 " + yPos + " Td (Experience) Tj ET\n");
                    stream.append("BT /F2 10 Tf 450 " + yPos + " Td (Boursier) Tj ET\n");
                    yPos -= 5;
                    stream.append("0.3 0.3 0.3 RG 0.5 w 36 " + yPos + " m 559 " + yPos + " l S\n");
                    yPos -= 15;
                }

                // Lignes de candidats
                for (int i = startIdx; i < endIdx && i < candidates.size(); i++) {
                    Candidate c = candidates.get(i);

                    // Alternance de fond (gris clair)
                    if (i % 2 == 0) {
                        stream.append("0.95 0.95 0.97 rg 34 " + (yPos - 3) + " 527 16 re f\n");
                    }

                    stream.append("0 0 0 rg ");
                    stream.append("BT /F1 9 Tf 36 " + yPos + " Td (" + escapePdfString(c.getName()) + ") Tj ET\n");
                    stream.append("BT /F1 9 Tf 180 " + yPos + " Td (" + c.getAge() + " ans) Tj ET\n");
                    stream.append("BT /F1 9 Tf 250 " + yPos + " Td (" + String.format("%.1f", c.getGrade()) + "/20) Tj ET\n");
                    stream.append("BT /F1 9 Tf 330 " + yPos + " Td (" + c.getExperienceYears() + " ans) Tj ET\n");
                    stream.append("BT /F1 9 Tf 450 " + yPos + " Td (" + (c.isHasScholarship() ? "Oui" : "Non") + ") Tj ET\n");

                    yPos -= 18;
                }

                // Pied de page
                double footY = 40;
                stream.append("0.5 0.5 0.5 rg BT /F1 8 Tf 36 " + footY + " Td (Page " + (pageNum + 1) + "/" + totalPages +
                        "  -  Total candidats : " + candidates.size() + ") Tj ET\n");

                String streamContent = stream.toString();

                // Object page
                int pageObjNum = 5 + pageNum;
                int streamObjNum = 5 + totalPages + pageNum;

                objectOffsets.add((long) pdf.length());
                pdf.append(pageObjNum + " 0 obj\n<< /Type /Page /Parent 2 0 R /MediaBox [0 0 595 842] ");
                pdf.append("/Contents " + streamObjNum + " 0 R ");
                pdf.append("/Resources << /Font << /F1 3 0 R /F2 4 0 R >> >> >>\nendobj\n");
            }

            // Stream objects for each page
            for (int pageNum = 0; pageNum < totalPages; pageNum++) {
                int startIdx = pageNum * linesPerPage;
                int endIdx = Math.min(startIdx + linesPerPage, candidates.size());

                StringBuilder stream = new StringBuilder();
                double yPos = 780;

                if (pageNum == 0) {
                    stream.append("BT /F2 18 Tf 36 " + yPos + " Td (" + escapePdfString(title) + ") Tj ET\n");
                    yPos -= 30;
                    stream.append("BT /F1 9 Tf 36 " + yPos + " Td (Date d'export : " +
                            new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm").format(new java.util.Date()) +
                            ") Tj ET\n");
                    yPos -= 20;
                    stream.append("0.53 0.71 0.98 RG 1 w 36 " + yPos + " m 559 " + yPos + " l S\n");
                    yPos -= 20;
                }

                stream.append("BT /F2 10 Tf 36 " + yPos + " Td (Nom) Tj ET\n");
                stream.append("BT /F2 10 Tf 180 " + yPos + " Td (Age) Tj ET\n");
                stream.append("BT /F2 10 Tf 250 " + yPos + " Td (Note) Tj ET\n");
                stream.append("BT /F2 10 Tf 330 " + yPos + " Td (Experience) Tj ET\n");
                stream.append("BT /F2 10 Tf 450 " + yPos + " Td (Boursier) Tj ET\n");
                yPos -= 5;
                stream.append("0.3 0.3 0.3 RG 0.5 w 36 " + yPos + " m 559 " + yPos + " l S\n");
                yPos -= 15;

                for (int i = startIdx; i < endIdx && i < candidates.size(); i++) {
                    Candidate c = candidates.get(i);
                    if (i % 2 == 0) {
                        stream.append("0.95 0.95 0.97 rg 34 " + (yPos - 3) + " 527 16 re f\n");
                    }
                    stream.append("0 0 0 rg ");
                    stream.append("BT /F1 9 Tf 36 " + yPos + " Td (" + escapePdfString(c.getName()) + ") Tj ET\n");
                    stream.append("BT /F1 9 Tf 180 " + yPos + " Td (" + c.getAge() + " ans) Tj ET\n");
                    stream.append("BT /F1 9 Tf 250 " + yPos + " Td (" + String.format("%.1f", c.getGrade()) + "/20) Tj ET\n");
                    stream.append("BT /F1 9 Tf 330 " + yPos + " Td (" + c.getExperienceYears() + " ans) Tj ET\n");
                    stream.append("BT /F1 9 Tf 450 " + yPos + " Td (" + (c.isHasScholarship() ? "Oui" : "Non") + ") Tj ET\n");
                    yPos -= 18;
                }

                double footY = 40;
                stream.append("0.5 0.5 0.5 rg BT /F1 8 Tf 36 " + footY + " Td (Page " + (pageNum + 1) + "/" + totalPages +
                        "  -  Total candidats : " + candidates.size() + ") Tj ET\n");

                String streamContent = stream.toString();
                byte[] streamBytes = streamContent.getBytes("ISO-8859-1");
                int streamObjNum = 5 + totalPages + pageNum;

                objectOffsets.add((long) pdf.length());
                pdf.append(streamObjNum + " 0 obj\n<< /Length " + streamBytes.length + " >>\nstream\n");
                pdf.append(streamContent);
                pdf.append("endstream\nendobj\n");
            }

            // Cross-reference table
            int xrefOffset = pdf.length();
            int totalObjects = objectOffsets.size() + 1;
            pdf.append("xref\n0 " + totalObjects + "\n");
            pdf.append("0000000000 65535 f \n");
            for (Long offset : objectOffsets) {
                pdf.append(String.format("%010d 00000 n \n", offset));
            }

            // Trailer
            pdf.append("trailer\n<< /Size " + totalObjects + " /Root 1 0 R >>\n");
            pdf.append("startxref\n" + xrefOffset + "\n%%EOF\n");

            raf.setLength(0);
            raf.write(pdf.toString().getBytes("ISO-8859-1"));
        }
    }

    /**
     * Échappe les caractères spéciaux pour les chaînes PDF.
     */
    private String escapePdfString(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\")
                .replace("(", "\\(")
                .replace(")", "\\)")
                // Remplacer les caractères accentués par leurs équivalents ASCII pour compatibilité PDF base
                .replace("\u00e9", "e")    // é
                .replace("\u00e8", "e")    // è
                .replace("\u00ea", "e")    // ê
                .replace("\u00eb", "e")    // ë
                .replace("\u00e0", "a")    // à
                .replace("\u00e2", "a")    // â
                .replace("\u00e7", "c")    // ç
                .replace("\u00f4", "o")    // ô
                .replace("\u00f9", "u")    // ù
                .replace("\u00fb", "u")    // û
                .replace("\u00ee", "i")    // î
                .replace("\u00ef", "i")    // ï
                .replace("\u00c9", "E")    // É
                .replace("\u00c8", "E")    // È
                .replace("\u00c0", "A")    // À
                ;
    }

    /**
     * Inner Printable class for java.awt.print approach.
     */
    private static class CandidatePrintable implements Printable {
        private final List<Candidate> candidates;
        private final String title;
        private static final int LINES_PER_PAGE = 25;

        CandidatePrintable(List<Candidate> candidates, String title) {
            this.candidates = candidates;
            this.title = title;
        }

        @Override
        public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) {
            int totalPages = Math.max(1, (int) Math.ceil((double) candidates.size() / LINES_PER_PAGE));
            if (pageIndex >= totalPages) return NO_SUCH_PAGE;

            Graphics2D g2 = (Graphics2D) graphics;
            g2.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            double width = pageFormat.getImageableWidth();
            int y = 30;

            // Titre
            if (pageIndex == 0) {
                g2.setFont(new Font("SansSerif", Font.BOLD, 16));
                g2.drawString(title, 10, y);
                y += 25;
                g2.setFont(new Font("SansSerif", Font.PLAIN, 9));
                g2.drawString("Date : " + new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm").format(new java.util.Date()), 10, y);
                y += 20;
            }

            // En-têtes
            g2.setFont(new Font("SansSerif", Font.BOLD, 10));
            g2.drawString("Nom", 10, y);
            g2.drawString("Age", 150, y);
            g2.drawString("Note", 210, y);
            g2.drawString("Exp.", 290, y);
            g2.drawString("Boursier", 370, y);
            y += 5;
            g2.drawLine(10, y, (int) width - 10, y);
            y += 15;

            // Candidats de cette page
            g2.setFont(new Font("SansSerif", Font.PLAIN, 9));
            int start = pageIndex * LINES_PER_PAGE;
            int end = Math.min(start + LINES_PER_PAGE, candidates.size());
            for (int i = start; i < end; i++) {
                Candidate c = candidates.get(i);
                g2.drawString(c.getName(), 10, y);
                g2.drawString(c.getAge() + " ans", 150, y);
                g2.drawString(String.format("%.1f/20", c.getGrade()), 210, y);
                g2.drawString(c.getExperienceYears() + " ans", 290, y);
                g2.drawString(c.isHasScholarship() ? "Oui" : "Non", 370, y);
                y += 16;
            }

            return PAGE_EXISTS;
        }
    }
}
