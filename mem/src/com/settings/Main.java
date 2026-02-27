package com.settings;

import com.settings.ui.SettingsManagerUI;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
        }

        SwingUtilities.invokeLater(() -> {
            SettingsManagerUI ui = new SettingsManagerUI();
            ui.setVisible(true);
        });
    }
}
