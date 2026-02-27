package com.settings.ui;

import com.settings.model.Settings;
import com.settings.model.SettingsCaretaker;
import com.settings.model.SettingsMemento;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class SettingsManagerUI extends JFrame {

    private static final Color DARK_BG        = new Color(40, 44, 52);
    private static final Color DARK_BG_ALT    = new Color(50, 54, 62);
    private static final Color DARK_PANEL     = new Color(55, 60, 70);
    private static final Color DARK_FG        = new Color(220, 220, 220);
    private static final Color DARK_BORDER    = new Color(80, 90, 105);
    private static final Color DARK_ACCENT    = new Color(100, 160, 220);
    private static final Color DARK_SELECTION = new Color(70, 120, 180);
    private static final Color DARK_GRID      = new Color(70, 75, 85);

    private static final Color LIGHT_BG        = UIManager.getColor("Panel.background") != null
            ? UIManager.getColor("Panel.background") : new Color(240, 240, 240);
    private static final Color LIGHT_FG        = Color.BLACK;
    private static final Color LIGHT_ACCENT    = new Color(70, 130, 180);

    private final Settings settings;
    private final SettingsCaretaker caretaker;

    private int restoreIndex = -1;
    private boolean isDark = false;

    private JPanel mainPanel;
    private JSlider volumeSlider;
    private JSlider brightnessSlider;
    private JCheckBox darkModeCheckBox;
    private JLabel volumeValueLabel;
    private JLabel brightnessValueLabel;
    private JButton restorePreviousButton;
    private JButton restoreSelectedButton;
    private JTable historyTable;
    private DefaultTableModel tableModel;
    private JScrollPane historyScrollPane;
    private JLabel statusLabel;
    private JPanel statusPanel;

    public SettingsManagerUI() {
        this.settings = new Settings();
        this.caretaker = new SettingsCaretaker();

        initializeUI();
        updateUIFromSettings();
        updateRestorePreviousButton();
    }

    private void initializeUI() {
        setTitle("Gestionnaire de Paramètres");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(750, 620));
        setLocationRelativeTo(null);

        mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        mainPanel.add(createSettingsPanel(), BorderLayout.NORTH);
        mainPanel.add(createHistoryPanel(), BorderLayout.CENTER);
        mainPanel.add(createStatusBar(), BorderLayout.SOUTH);

        setContentPane(mainPanel);
        pack();
    }

    private JPanel createSettingsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        TitledBorder border = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(70, 130, 180), 1),
                " Current Settings ");
        border.setTitleFont(border.getTitleFont().deriveFont(Font.BOLD, 14f));
        border.setTitleColor(new Color(70, 130, 180));
        panel.setBorder(BorderFactory.createCompoundBorder(border, new EmptyBorder(10, 10, 10, 10)));

        JPanel controlsPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        JLabel volumeLabel = new JLabel("Volume:");
        volumeLabel.setFont(volumeLabel.getFont().deriveFont(Font.BOLD, 13f));
        controlsPanel.add(volumeLabel, gbc);

        gbc.gridx = 1; gbc.weightx = 1.0;
        volumeSlider = new JSlider(0, 100, 50);
        volumeSlider.setMajorTickSpacing(25);
        volumeSlider.setMinorTickSpacing(5);
        volumeSlider.setPaintTicks(true);
        volumeSlider.setPaintLabels(true);
        volumeSlider.addChangeListener(e -> {
            volumeValueLabel.setText(String.valueOf(volumeSlider.getValue()));
            settings.setVolume(volumeSlider.getValue());
        });
        controlsPanel.add(volumeSlider, gbc);

        gbc.gridx = 2; gbc.weightx = 0;
        volumeValueLabel = new JLabel("50", SwingConstants.RIGHT);
        volumeValueLabel.setPreferredSize(new Dimension(35, 20));
        volumeValueLabel.setFont(volumeValueLabel.getFont().deriveFont(Font.BOLD));
        controlsPanel.add(volumeValueLabel, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        JLabel brightnessLabel = new JLabel("Brightness:");
        brightnessLabel.setFont(brightnessLabel.getFont().deriveFont(Font.BOLD, 13f));
        controlsPanel.add(brightnessLabel, gbc);

        gbc.gridx = 1; gbc.weightx = 1.0;
        brightnessSlider = new JSlider(0, 100, 70);
        brightnessSlider.setMajorTickSpacing(25);
        brightnessSlider.setMinorTickSpacing(5);
        brightnessSlider.setPaintTicks(true);
        brightnessSlider.setPaintLabels(true);
        brightnessSlider.addChangeListener(e -> {
            brightnessValueLabel.setText(String.valueOf(brightnessSlider.getValue()));
            settings.setBrightness(brightnessSlider.getValue());
        });
        controlsPanel.add(brightnessSlider, gbc);

        gbc.gridx = 2; gbc.weightx = 0;
        brightnessValueLabel = new JLabel("70", SwingConstants.RIGHT);
        brightnessValueLabel.setPreferredSize(new Dimension(35, 20));
        brightnessValueLabel.setFont(brightnessValueLabel.getFont().deriveFont(Font.BOLD));
        controlsPanel.add(brightnessValueLabel, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        JLabel darkModeLabel = new JLabel("Dark Mode:");
        darkModeLabel.setFont(darkModeLabel.getFont().deriveFont(Font.BOLD, 13f));
        controlsPanel.add(darkModeLabel, gbc);

        gbc.gridx = 1; gbc.weightx = 1.0; gbc.gridwidth = 2;
        darkModeCheckBox = new JCheckBox("Enable Dark Mode");
        darkModeCheckBox.setFont(darkModeCheckBox.getFont().deriveFont(13f));
        darkModeCheckBox.addActionListener(e -> {
            settings.setDarkMode(darkModeCheckBox.isSelected());
            applyTheme(darkModeCheckBox.isSelected());
        });
        controlsPanel.add(darkModeCheckBox, gbc);
        gbc.gridwidth = 1;

        panel.add(controlsPanel, BorderLayout.CENTER);

        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));

        JButton saveButton = createStyledButton("Save Configuration", new Color(46, 139, 87));
        saveButton.addActionListener(e -> saveConfiguration());
        buttonsPanel.add(saveButton);

        restorePreviousButton = createStyledButton("\u25C0  Restore Previous Version", new Color(204, 102, 0));
        restorePreviousButton.addActionListener(e -> restorePreviousVersion());
        buttonsPanel.add(restorePreviousButton);

        panel.add(buttonsPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createHistoryPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        TitledBorder border = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(70, 130, 180), 1),
                " Version History ");
        border.setTitleFont(border.getTitleFont().deriveFont(Font.BOLD, 14f));
        border.setTitleColor(new Color(70, 130, 180));
        panel.setBorder(BorderFactory.createCompoundBorder(border, new EmptyBorder(10, 10, 10, 10)));

        String[] columns = {"#", "Volume", "Brightness", "Dark Mode", "Date & Time"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        historyTable = new JTable(tableModel);
        historyTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        historyTable.setRowHeight(28);
        historyTable.getTableHeader().setFont(historyTable.getFont().deriveFont(Font.BOLD, 13f));
        historyTable.getTableHeader().setBackground(new Color(70, 130, 180));
        historyTable.getTableHeader().setForeground(Color.WHITE);
        historyTable.setFont(historyTable.getFont().deriveFont(13f));
        historyTable.setGridColor(new Color(220, 220, 220));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < historyTable.getColumnCount(); i++) {
            historyTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        historyTable.getColumnModel().getColumn(0).setPreferredWidth(40);
        historyTable.getColumnModel().getColumn(1).setPreferredWidth(80);
        historyTable.getColumnModel().getColumn(2).setPreferredWidth(80);
        historyTable.getColumnModel().getColumn(3).setPreferredWidth(90);
        historyTable.getColumnModel().getColumn(4).setPreferredWidth(180);

        historyTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(SwingConstants.CENTER);
                if (isSelected) {
                    c.setBackground(isDark ? DARK_SELECTION : table.getSelectionBackground());
                    c.setForeground(Color.WHITE);
                } else if (isDark) {
                    c.setBackground(row % 2 == 0 ? DARK_BG : DARK_BG_ALT);
                    c.setForeground(DARK_FG);
                } else {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(240, 248, 255));
                    c.setForeground(LIGHT_FG);
                }
                return c;
            }
        });

        historyScrollPane = new JScrollPane(historyTable);
        historyScrollPane.setPreferredSize(new Dimension(700, 200));
        panel.add(historyScrollPane, BorderLayout.CENTER);

        JPanel historyButtonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 8));
        restoreSelectedButton = createStyledButton("Restore Selected Version", new Color(70, 130, 180));
        restoreSelectedButton.addActionListener(e -> restoreSelectedVersion());
        historyButtonsPanel.add(restoreSelectedButton);
        panel.add(historyButtonsPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createStatusBar() {
        statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(200, 200, 200)),
                new EmptyBorder(5, 10, 5, 10)
        ));

        statusLabel = new JLabel("Ready. Modify settings and click 'Save Configuration' to begin.");
        statusLabel.setFont(statusLabel.getFont().deriveFont(12f));
        statusLabel.setForeground(new Color(100, 100, 100));
        statusPanel.add(statusLabel, BorderLayout.WEST);

        return statusPanel;
    }

    private JButton createStyledButton(String text, Color backgroundColor) {
        JButton button = new JButton(text) {
            private boolean hovering = false;

            {
                setContentAreaFilled(false);
                addMouseListener(new java.awt.event.MouseAdapter() {
                    @Override
                    public void mouseEntered(java.awt.event.MouseEvent e) {
                        if (isEnabled()) { hovering = true; repaint(); }
                    }
                    @Override
                    public void mouseExited(java.awt.event.MouseEvent e) {
                        hovering = false; repaint();
                    }
                });
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (!isEnabled()) {
                    g2.setColor(new Color(180, 180, 180));
                } else if (hovering) {
                    g2.setColor(backgroundColor.brighter());
                } else {
                    g2.setColor(backgroundColor);
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        button.setFont(button.getFont().deriveFont(Font.BOLD, 13f));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setOpaque(false);
        button.setBorder(new EmptyBorder(10, 24, 10, 24));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void saveConfiguration() {
        SettingsMemento memento = settings.save();
        caretaker.addMemento(memento);
        restoreIndex = caretaker.size() - 1;
        refreshHistoryTable();
        updateRestorePreviousButton();
        setStatus("Configuration saved! Version #" + caretaker.size()
                + " \u2014 Volume: " + memento.getVolume()
                + ", Brightness: " + memento.getBrightness()
                + ", Dark Mode: " + (memento.isDarkMode() ? "On" : "Off"));
    }

    private void restorePreviousVersion() {
        if (restoreIndex < 0 || caretaker.size() == 0) {
            setStatus("No previous version available to restore.");
            return;
        }

        SettingsMemento memento = caretaker.getMemento(restoreIndex);
        settings.restore(memento);
        updateUIFromSettings();

        historyTable.setRowSelectionInterval(restoreIndex, restoreIndex);

        setStatus("Restored to Version #" + (restoreIndex + 1)
                + " \u2014 Volume: " + settings.getVolume()
                + ", Brightness: " + settings.getBrightness()
                + ", Dark Mode: " + (settings.isDarkMode() ? "On" : "Off"));

        restoreIndex--;
        updateRestorePreviousButton();
    }

    private void restoreSelectedVersion() {
        int selectedRow = historyTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this,
                    "Please select a version from the history table first.",
                    "No Version Selected",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        SettingsMemento memento = caretaker.getMemento(selectedRow);
        settings.restore(memento);
        updateUIFromSettings();

        restoreIndex = selectedRow - 1;
        updateRestorePreviousButton();

        setStatus("Restored to Version #" + (selectedRow + 1)
                + " \u2014 Volume: " + settings.getVolume()
                + ", Brightness: " + settings.getBrightness()
                + ", Dark Mode: " + (settings.isDarkMode() ? "On" : "Off"));
    }

    private void updateRestorePreviousButton() {
        boolean hasOlderVersion = restoreIndex >= 0 && caretaker.size() > 0;
        restorePreviousButton.setEnabled(hasOlderVersion);
        if (!hasOlderVersion && caretaker.size() > 0) {
            restorePreviousButton.setToolTipText("No older versions available");
        } else if (caretaker.size() == 0) {
            restorePreviousButton.setToolTipText("Save a configuration first");
        } else {
            restorePreviousButton.setToolTipText("Restore Version #" + (restoreIndex + 1));
        }
    }

    private void updateUIFromSettings() {
        volumeSlider.setValue(settings.getVolume());
        brightnessSlider.setValue(settings.getBrightness());
        darkModeCheckBox.setSelected(settings.isDarkMode());
        volumeValueLabel.setText(String.valueOf(settings.getVolume()));
        brightnessValueLabel.setText(String.valueOf(settings.getBrightness()));
        applyTheme(settings.isDarkMode());
    }

    private void applyTheme(boolean dark) {
        this.isDark = dark;
        Color bg = dark ? DARK_BG : LIGHT_BG;
        Color fg = dark ? DARK_FG : LIGHT_FG;
        Color borderColor = dark ? DARK_BORDER : LIGHT_ACCENT;
        Color accentColor = dark ? DARK_ACCENT : LIGHT_ACCENT;

        getContentPane().setBackground(bg);
        applyToContainer(getContentPane(), bg, fg);

        styleSlider(volumeSlider, bg, fg);
        styleSlider(brightnessSlider, bg, fg);

        darkModeCheckBox.setBackground(bg);
        darkModeCheckBox.setForeground(fg);

        historyTable.setBackground(dark ? DARK_BG : Color.WHITE);
        historyTable.setForeground(fg);
        historyTable.setGridColor(dark ? DARK_GRID : new Color(220, 220, 220));
        historyTable.getTableHeader().setBackground(dark ? DARK_BORDER : LIGHT_ACCENT);
        historyTable.getTableHeader().setForeground(Color.WHITE);
        historyTable.setSelectionBackground(dark ? DARK_SELECTION : UIManager.getColor("Table.selectionBackground"));
        historyTable.setSelectionForeground(Color.WHITE);
        historyScrollPane.getViewport().setBackground(dark ? DARK_BG : Color.WHITE);

        statusPanel.setBackground(dark ? DARK_PANEL : LIGHT_BG);
        statusPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, dark ? DARK_BORDER : new Color(200, 200, 200)),
                new EmptyBorder(5, 10, 5, 10)
        ));
        statusLabel.setForeground(dark ? new Color(160, 165, 175) : new Color(100, 100, 100));

        updateTitledBorders(getContentPane(), borderColor, accentColor);

        SwingUtilities.invokeLater(() -> {
            repaint();
            revalidate();
        });
    }

    private void applyToContainer(Container container, Color bg, Color fg) {
        for (Component comp : container.getComponents()) {
            if (comp instanceof JButton || comp instanceof JScrollPane
                    || comp instanceof JTable || comp instanceof JSlider) {
                if (comp instanceof JScrollPane) {
                    JScrollPane sp = (JScrollPane) comp;
                    sp.setBackground(bg);
                    sp.getViewport().setBackground(bg);
                }
                continue;
            }
            comp.setBackground(bg);
            comp.setForeground(fg);
            if (comp instanceof Container) {
                applyToContainer((Container) comp, bg, fg);
            }
        }
    }

    private void styleSlider(JSlider slider, Color bg, Color fg) {
        slider.setBackground(bg);
        slider.setForeground(fg);
    }

    private void updateTitledBorders(Container container, Color borderColor, Color titleColor) {
        for (Component comp : container.getComponents()) {
            if (comp instanceof JComponent) {
                JComponent jc = (JComponent) comp;
                if (jc.getBorder() instanceof javax.swing.border.CompoundBorder) {
                    javax.swing.border.CompoundBorder cb = (javax.swing.border.CompoundBorder) jc.getBorder();
                    if (cb.getOutsideBorder() instanceof TitledBorder) {
                        TitledBorder tb = (TitledBorder) cb.getOutsideBorder();
                        tb.setBorder(BorderFactory.createLineBorder(borderColor, 1));
                        tb.setTitleColor(titleColor);
                    }
                }
            }
            if (comp instanceof Container) {
                updateTitledBorders((Container) comp, borderColor, titleColor);
            }
        }
    }

    private void refreshHistoryTable() {
        tableModel.setRowCount(0);
        List<SettingsMemento> history = caretaker.getHistory();
        for (int i = 0; i < history.size(); i++) {
            SettingsMemento m = history.get(i);
            tableModel.addRow(new Object[]{
                    i + 1,
                    m.getVolume(),
                    m.getBrightness(),
                    m.isDarkMode() ? "On" : "Off",
                    m.getFormattedTimestamp()
            });
        }
    }

    private void setStatus(String message) {
        statusLabel.setText(message);
    }
}
