package ui;

import model.Candidate;
import service.CandidateService;
import service.JsonPersistenceService;
import service.PdfExportService;
import service.SpecificationRegistry;
import specification.Specification;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Interface graphique principale de l'application de gestion des candidats.
 * Respecte ISP : l'UI implémente uniquement ce dont elle a besoin.
 * Respecte DIP : dépend des abstractions (CandidateService, Specification).
 * Le contrôleur UI ne contient AUCUNE logique métier.
 */
public class MainWindow extends JFrame {

    // --- Thème actif ---
    private Theme theme = Theme.dark();
    private boolean isDark = true;

    // --- Services (DIP) ---
    private final CandidateService candidateService;
    private final SpecificationRegistry specificationRegistry;
    private final JsonPersistenceService persistenceService;
    private final PdfExportService pdfExportService;

    // --- Données ---
    private final List<Candidate> allCandidates = new ArrayList<>();

    // --- Composants UI ---
    private JTextField txtName;
    private JSpinner spinAge;
    private JSpinner spinGrade;
    private JSpinner spinExperience;
    private JCheckBox chkScholarship;
    private DefaultTableModel tableModelAll;
    private DefaultTableModel tableModelFiltered;
    private JTable tableAll;
    private JTable tableFiltered;
    private JLabel lblTotalCount;
    private JLabel lblFilteredCount;
    private JButton btnThemeToggle;

    // --- Critères dynamiques ---
    private final List<JCheckBox> criteriaCheckBoxes = new ArrayList<>();
    private final List<JSpinner> criteriaSpinners = new ArrayList<>();

    public MainWindow(CandidateService candidateService, SpecificationRegistry specificationRegistry,
                      JsonPersistenceService persistenceService, PdfExportService pdfExportService) {
        this.candidateService = candidateService;
        this.specificationRegistry = specificationRegistry;
        this.persistenceService = persistenceService;
        this.pdfExportService = pdfExportService;

        initWindow();
        buildUI();
    }

    private void initWindow() {
        setTitle("Gestion des Candidats \u2014 Crit\u00e8res d'\u00c9ligibilit\u00e9");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1250, 820));
        setPreferredSize(new Dimension(1350, 870));
        setLocationRelativeTo(null);
    }

    // ================================================================
    // CONSTRUCTION DE L'UI
    // ================================================================

    private void buildUI() {
        getContentPane().removeAll();
        getContentPane().setBackground(theme.bgDark);
        setLayout(new BorderLayout(0, 0));

        criteriaCheckBoxes.clear();
        criteriaSpinners.clear();

        add(createHeader(), BorderLayout.NORTH);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setBorder(null);
        splitPane.setDividerSize(2);
        splitPane.setBackground(theme.bgDark);
        splitPane.setDividerLocation(440);

        splitPane.setLeftComponent(createLeftPanel());
        splitPane.setRightComponent(createRightPanel());
        add(splitPane, BorderLayout.CENTER);

        add(createStatusBar(), BorderLayout.SOUTH);

        pack();
        revalidate();
        repaint();

        // Restaurer les données dans les tableaux
        refreshAllTable();
        applyFilter();
    }

    // ================================================================
    // EN-TÊTE
    // ================================================================

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(theme.bgPanel);
        header.setBorder(new EmptyBorder(16, 22, 16, 22));

        JLabel title = new JLabel("\ud83d\udccb  Gestion des Candidats");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(theme.accent);

        JLabel subtitle = new JLabel("Syst\u00e8me de filtrage par sp\u00e9cifications \u2014 Pattern Specification");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitle.setForeground(theme.textSecondary);

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setBackground(theme.bgPanel);
        titlePanel.add(title);
        titlePanel.add(Box.createVerticalStrut(3));
        titlePanel.add(subtitle);

        header.add(titlePanel, BorderLayout.WEST);

        // Boutons à droite
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnPanel.setBackground(theme.bgPanel);

        btnThemeToggle = createStyledButton(
                isDark ? "\u2600\ufe0f Mode Clair" : "\ud83c\udf19 Mode Sombre",
                theme.textSecondary);
        btnThemeToggle.addActionListener(e -> toggleTheme());

        JButton btnSave = createStyledButton("\ud83d\udcbe Sauvegarder", theme.accent);
        btnSave.addActionListener(e -> saveToFile());

        JButton btnLoad = createStyledButton("\ud83d\udcc2 Charger (PDF)", theme.success);
        btnLoad.addActionListener(e -> loadAndExportPdf());

        btnPanel.add(btnThemeToggle);
        btnPanel.add(btnSave);
        btnPanel.add(btnLoad);
        header.add(btnPanel, BorderLayout.EAST);

        return header;
    }

    // ================================================================
    // PANNEAU GAUCHE
    // ================================================================

    private JPanel createLeftPanel() {
        JPanel left = new JPanel();
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.setBackground(theme.bgDark);
        left.setBorder(new EmptyBorder(12, 12, 12, 8));

        left.add(createFormPanel());
        left.add(Box.createVerticalStrut(10));
        left.add(createCriteriaPanel());
        left.add(Box.createVerticalGlue());

        return left;
    }

    private JPanel createFormPanel() {
        JPanel form = createSection("Ajouter un Candidat");

        JPanel grid = new JPanel(new GridBagLayout());
        grid.setBackground(theme.bgPanel);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 8, 5, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Nom
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        grid.add(createLabel("Nom :"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        txtName = createTextField();
        grid.add(txtName, gbc);

        // Âge
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        grid.add(createLabel("\u00c2ge :"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        spinAge = createSpinner(18, 10, 99, 1);
        grid.add(spinAge, gbc);

        // Note
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        grid.add(createLabel("Note (/20) :"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        spinGrade = createSpinner(10.0, 0.0, 20.0, 0.5);
        grid.add(spinGrade, gbc);

        // Expérience
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0;
        grid.add(createLabel("Exp\u00e9rience (ans) :"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        spinExperience = createSpinner(0, 0, 50, 1);
        grid.add(spinExperience, gbc);

        // Boursier
        gbc.gridx = 0; gbc.gridy = 4; gbc.weightx = 0;
        grid.add(createLabel("Boursier :"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        chkScholarship = new JCheckBox("Oui");
        chkScholarship.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        chkScholarship.setForeground(theme.textPrimary);
        chkScholarship.setBackground(theme.bgPanel);
        chkScholarship.setFocusPainted(false);
        grid.add(chkScholarship, gbc);

        form.add(grid);
        form.add(Box.createVerticalStrut(10));

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        btnRow.setBackground(theme.bgPanel);

        JButton btnAdd = createStyledButton("\u2795 Ajouter", theme.success);
        btnAdd.addActionListener(e -> addCandidate());

        JButton btnClear = createStyledButton("\ud83d\uddd1 Effacer tout", theme.danger);
        btnClear.addActionListener(e -> clearAll());

        btnRow.add(btnAdd);
        btnRow.add(btnClear);
        form.add(btnRow);
        form.add(Box.createVerticalStrut(4));

        return form;
    }

    private JPanel createCriteriaPanel() {
        JPanel panel = createSection("Crit\u00e8res d'\u00c9ligibilit\u00e9 (AND)");

        List<Specification> specs = specificationRegistry.getAllSpecifications();
        for (Specification spec : specs) {
            JPanel row = new JPanel(new BorderLayout(8, 0));
            row.setBackground(theme.bgPanel);
            row.setBorder(new EmptyBorder(3, 4, 3, 4));
            row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));

            JCheckBox cb = new JCheckBox(spec.getLabel());
            cb.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            cb.setForeground(theme.textPrimary);
            cb.setBackground(theme.bgPanel);
            cb.setFocusPainted(false);
            cb.addActionListener(e -> applyFilter());
            criteriaCheckBoxes.add(cb);
            row.add(cb, BorderLayout.CENTER);

            if (spec.hasConfigurableThreshold()) {
                JSpinner spinner = createSpinner(
                        spec.getThreshold(),
                        spec.getThresholdMin(),
                        spec.getThresholdMax(),
                        spec.getThresholdStep());
                spinner.setPreferredSize(new Dimension(75, 28));
                spinner.addChangeListener(e -> {
                    double val = ((Number) spinner.getValue()).doubleValue();
                    spec.setThreshold(val);
                    cb.setText(spec.getLabel());
                    applyFilter();
                });
                criteriaSpinners.add(spinner);
                row.add(spinner, BorderLayout.EAST);
            } else {
                criteriaSpinners.add(null);
            }

            panel.add(row);
        }

        panel.add(Box.createVerticalStrut(8));

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        btnRow.setBackground(theme.bgPanel);

        JButton btnAll = createStyledButton("\u2705 Tout cocher", theme.accent);
        btnAll.addActionListener(e -> {
            criteriaCheckBoxes.forEach(cb -> cb.setSelected(true));
            applyFilter();
        });

        JButton btnNone = createStyledButton("\u274c Tout d\u00e9cocher", theme.textSecondary);
        btnNone.addActionListener(e -> {
            criteriaCheckBoxes.forEach(cb -> cb.setSelected(false));
            applyFilter();
        });

        btnRow.add(btnAll);
        btnRow.add(btnNone);
        panel.add(btnRow);
        panel.add(Box.createVerticalStrut(4));

        return panel;
    }

    // ================================================================
    // PANNEAU DROIT (TABLEAUX)
    // ================================================================

    private JPanel createRightPanel() {
        JPanel right = new JPanel();
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
        right.setBackground(theme.bgDark);
        right.setBorder(new EmptyBorder(12, 8, 12, 12));

        String[] columns = {"Nom", "\u00c2ge", "Note", "Exp\u00e9rience", "Boursier"};

        // Tous les candidats
        tableModelAll = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        tableAll = createStyledTable(tableModelAll);
        JScrollPane scrollAll = new JScrollPane(tableAll);
        scrollAll.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(theme.bgInput, 1), " Tous les Candidats ",
                TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 13), theme.accent));
        scrollAll.getViewport().setBackground(theme.bgPanel);
        scrollAll.setBackground(theme.bgDark);

        lblTotalCount = new JLabel("Total : 0");
        lblTotalCount.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblTotalCount.setForeground(theme.textSecondary);
        lblTotalCount.setBorder(new EmptyBorder(4, 8, 4, 8));

        JPanel topTable = new JPanel(new BorderLayout());
        topTable.setBackground(theme.bgDark);
        topTable.add(scrollAll, BorderLayout.CENTER);
        topTable.add(lblTotalCount, BorderLayout.SOUTH);

        // Menu contextuel suppression
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem deleteItem = new JMenuItem("Supprimer ce candidat");
        deleteItem.addActionListener(e -> deleteSelectedCandidate());
        popupMenu.add(deleteItem);
        tableAll.addMouseListener(new MouseAdapter() {
            @Override public void mousePressed(MouseEvent e) { showPopup(e); }
            @Override public void mouseReleased(MouseEvent e) { showPopup(e); }
            private void showPopup(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    int row = tableAll.rowAtPoint(e.getPoint());
                    if (row >= 0) {
                        tableAll.setRowSelectionInterval(row, row);
                        popupMenu.show(e.getComponent(), e.getX(), e.getY());
                    }
                }
            }
        });

        // Candidats retenus
        tableModelFiltered = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        tableFiltered = createStyledTable(tableModelFiltered);
        JScrollPane scrollFiltered = new JScrollPane(tableFiltered);
        scrollFiltered.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(theme.success, 1), " Candidats Retenus ",
                TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 13), theme.success));
        scrollFiltered.getViewport().setBackground(theme.bgPanel);
        scrollFiltered.setBackground(theme.bgDark);

        lblFilteredCount = new JLabel("Retenus : 0");
        lblFilteredCount.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblFilteredCount.setForeground(theme.success);
        lblFilteredCount.setBorder(new EmptyBorder(4, 8, 4, 8));

        JPanel bottomTable = new JPanel(new BorderLayout());
        bottomTable.setBackground(theme.bgDark);
        bottomTable.add(scrollFiltered, BorderLayout.CENTER);
        bottomTable.add(lblFilteredCount, BorderLayout.SOUTH);

        JSplitPane tableSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, topTable, bottomTable);
        tableSplit.setDividerLocation(350);
        tableSplit.setDividerSize(3);
        tableSplit.setBorder(null);
        tableSplit.setBackground(theme.bgDark);

        right.add(tableSplit);
        return right;
    }

    private JPanel createStatusBar() {
        JPanel status = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 6));
        status.setBackground(theme.bgPanel);
        status.setBorder(new EmptyBorder(2, 15, 2, 15));

        JLabel info = new JLabel("Exercice 2 \u2014 Pattern Specification \u2022 DPA 2025/2026 \u2022 Th\u00e8me : " + theme.name);
        info.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        info.setForeground(theme.textSecondary);
        status.add(info);

        return status;
    }

    // ================================================================
    // THEME
    // ================================================================

    private void toggleTheme() {
        isDark = !isDark;
        theme = isDark ? Theme.dark() : Theme.light();
        buildUI();
    }

    // ================================================================
    // ACTIONS (pas de logique métier — tout délégué au service)
    // ================================================================

    private void addCandidate() {
        String name = txtName.getText().trim();
        if (name.isEmpty()) {
            showError("Veuillez saisir le nom du candidat.");
            return;
        }

        int age = (int) spinAge.getValue();
        double grade = ((Number) spinGrade.getValue()).doubleValue();
        int exp = (int) spinExperience.getValue();
        boolean scholarship = chkScholarship.isSelected();

        Candidate c = new Candidate(name, age, grade, exp, scholarship);
        allCandidates.add(c);

        refreshAllTable();
        applyFilter();
        clearForm();

        List<Specification> selectedSpecs = getSelectedSpecifications();
        if (!selectedSpecs.isEmpty()) {
            boolean eligible = candidateService.validateCandidate(c, selectedSpecs);
            if (eligible) {
                showInfo("\u2705 \u00ab " + name + " \u00bb ajout\u00e9 \u2014 \u00c9LIGIBLE selon les crit\u00e8res s\u00e9lectionn\u00e9s.");
            } else {
                showInfo("\u26a0\ufe0f \u00ab " + name + " \u00bb ajout\u00e9 \u2014 NON \u00c9LIGIBLE selon les crit\u00e8res s\u00e9lectionn\u00e9s.");
            }
        }
    }

    private void applyFilter() {
        List<Specification> selectedSpecs = getSelectedSpecifications();
        List<Candidate> filtered = candidateService.filterCandidates(allCandidates, selectedSpecs);
        refreshFilteredTable(filtered);
    }

    private List<Specification> getSelectedSpecifications() {
        List<Specification> selected = new ArrayList<>();
        List<Specification> allSpecs = specificationRegistry.getAllSpecifications();
        for (int i = 0; i < criteriaCheckBoxes.size(); i++) {
            if (criteriaCheckBoxes.get(i).isSelected()) {
                selected.add(allSpecs.get(i));
            }
        }
        return selected;
    }

    private void deleteSelectedCandidate() {
        int row = tableAll.getSelectedRow();
        if (row >= 0 && row < allCandidates.size()) {
            String name = allCandidates.get(row).getName();
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Supprimer le candidat \u00ab " + name + " \u00bb ?",
                    "Confirmation", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                allCandidates.remove(row);
                refreshAllTable();
                applyFilter();
            }
        }
    }

    private void clearAll() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "\u00cates-vous s\u00fbr de vouloir supprimer tous les candidats ?",
                "Confirmation", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            allCandidates.clear();
            refreshAllTable();
            applyFilter();
        }
    }

    private void saveToFile() {
        if (allCandidates.isEmpty()) {
            showError("Aucun candidat \u00e0 sauvegarder.");
            return;
        }
        String saveName = JOptionPane.showInputDialog(this,
                "Entrez un nom pour cette sauvegarde :",
                "Sauvegarder les candidats", JOptionPane.PLAIN_MESSAGE);
        if (saveName == null || saveName.trim().isEmpty()) return;
        saveName = saveName.trim().replaceAll("[^a-zA-Z0-9_\\- ]", "_");
        try {
            persistenceService.saveNamed(allCandidates, saveName);
            showInfo("\ud83d\udcbe " + allCandidates.size() + " candidat(s) sauvegard\u00e9(s) dans : saves/" + saveName + ".json");
        } catch (IOException ex) {
            showError("Erreur de sauvegarde : " + ex.getMessage());
        }
    }

    private void loadAndExportPdf() {
        List<String> saves = persistenceService.listSaves();
        if (saves.isEmpty()) {
            showError("Aucune sauvegarde trouv\u00e9e dans le dossier saves/.");
            return;
        }

        String[] options = saves.toArray(new String[0]);
        String selected = (String) JOptionPane.showInputDialog(this,
                "Choisissez une sauvegarde \u00e0 exporter en PDF :",
                "Charger et exporter en PDF",
                JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
        if (selected == null) return;

        try {
            List<Candidate> loaded = persistenceService.loadNamed(selected);
            if (loaded.isEmpty()) {
                showError("La sauvegarde \"" + selected + "\" est vide.");
                return;
            }

            allCandidates.clear();
            allCandidates.addAll(loaded);
            refreshAllTable();
            applyFilter();

            String pdfPath = "saves" + File.separator + selected + ".pdf";
            pdfExportService.exportToPdf(loaded, "Candidats \u2014 " + selected, pdfPath);
            showInfo("\ud83d\udcc2 " + loaded.size() + " candidat(s) charg\u00e9(s) et export\u00e9(s) en PDF :\n" + pdfPath);

            File pdfFile = new File(pdfPath);
            if (pdfFile.exists() && Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(pdfFile);
            }
        } catch (IOException ex) {
            showError("Erreur de chargement : " + ex.getMessage());
        } catch (Exception ex) {
            showError("Erreur d'export PDF : " + ex.getMessage());
        }
    }

    // ================================================================
    // HELPERS UI
    // ================================================================

    private void refreshAllTable() {
        if (tableModelAll == null) return;
        tableModelAll.setRowCount(0);
        for (Candidate c : allCandidates) {
            tableModelAll.addRow(new Object[]{
                    c.getName(), c.getAge(), c.getGrade(),
                    c.getExperienceYears() + " ans",
                    c.isHasScholarship() ? "Oui" : "Non"
            });
        }
        if (lblTotalCount != null)
            lblTotalCount.setText("Total : " + allCandidates.size() + " candidat(s)");
    }

    private void refreshFilteredTable(List<Candidate> filtered) {
        if (tableModelFiltered == null) return;
        tableModelFiltered.setRowCount(0);
        for (Candidate c : filtered) {
            tableModelFiltered.addRow(new Object[]{
                    c.getName(), c.getAge(), c.getGrade(),
                    c.getExperienceYears() + " ans",
                    c.isHasScholarship() ? "Oui" : "Non"
            });
        }
        if (lblFilteredCount != null)
            lblFilteredCount.setText("Retenus : " + filtered.size() + " / " + allCandidates.size() + " candidat(s)");
    }

    private void clearForm() {
        txtName.setText("");
        spinAge.setValue(18);
        spinGrade.setValue(10.0);
        spinExperience.setValue(0);
        chkScholarship.setSelected(false);
        txtName.requestFocusInWindow();
    }

    private void showInfo(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Information", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Erreur", JOptionPane.ERROR_MESSAGE);
    }

    // ================================================================
    // COMPOSANTS STYLISÉS
    // ================================================================

    private JPanel createSection(String title) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(theme.bgPanel);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(theme.bgInput, 1), " " + title + " ",
                        TitledBorder.LEFT, TitledBorder.TOP,
                        new Font("Segoe UI", Font.BOLD, 14), theme.accent),
                new EmptyBorder(8, 10, 8, 10)));
        return panel;
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        label.setForeground(theme.textPrimary);
        return label;
    }

    private JTextField createTextField() {
        JTextField field = new JTextField(20);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        field.setBackground(theme.bgInput);
        field.setForeground(theme.textPrimary);
        field.setCaretColor(theme.accent);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(theme.bgInput, 1),
                new EmptyBorder(6, 10, 6, 10)));
        return field;
    }

    private JSpinner createSpinner(Number value, Comparable min, Comparable max, Number step) {
        SpinnerNumberModel model = new SpinnerNumberModel(value, min, max, step);
        JSpinner spinner = new JSpinner(model);
        spinner.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        JComponent editor = spinner.getEditor();
        if (editor instanceof JSpinner.DefaultEditor) {
            JFormattedTextField tf = ((JSpinner.DefaultEditor) editor).getTextField();
            tf.setBackground(theme.bgInput);
            tf.setForeground(theme.textPrimary);
            tf.setCaretColor(theme.accent);
        }
        return spinner;
    }

    private JButton createStyledButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setForeground(theme.buttonText);
        btn.setBackground(color);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(8, 16, 8, 16));

        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { btn.setBackground(color.brighter()); }
            @Override public void mouseExited(MouseEvent e) { btn.setBackground(color); }
        });
        return btn;
    }

    private JTable createStyledTable(DefaultTableModel model) {
        final Theme t = theme;
        JTable table = new JTable(model) {
            @Override
            public Component prepareRenderer(javax.swing.table.TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                if (isRowSelected(row)) {
                    c.setBackground(t.tableSelected);
                    c.setForeground(t.accent);
                } else {
                    c.setBackground(row % 2 == 0 ? t.bgPanel : t.tableAlt);
                    c.setForeground(t.textPrimary);
                }
                return c;
            }
        };
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(30);
        table.setGridColor(theme.bgInput);
        table.setBackground(theme.bgPanel);
        table.setForeground(theme.textPrimary);
        table.setSelectionBackground(theme.tableSelected);
        table.setSelectionForeground(theme.accent);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 1));

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBackground(theme.bgInput);
        header.setForeground(theme.accent);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, theme.accent));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        return table;
    }
}
