package ui;

import java.awt.Color;

/**
 * Classe représentant un thème de couleurs pour l'interface.
 * Permet le basculement entre Mode Sombre et Mode Clair.
 */
public class Theme {

    public final String name;
    public final Color bgDark;
    public final Color bgPanel;
    public final Color bgInput;
    public final Color accent;
    public final Color accentHover;
    public final Color success;
    public final Color danger;
    public final Color textPrimary;
    public final Color textSecondary;
    public final Color tableAlt;
    public final Color tableSelected;
    public final Color buttonText;

    private Theme(String name, Color bgDark, Color bgPanel, Color bgInput, Color accent, Color accentHover,
                  Color success, Color danger, Color textPrimary, Color textSecondary,
                  Color tableAlt, Color tableSelected, Color buttonText) {
        this.name = name;
        this.bgDark = bgDark;
        this.bgPanel = bgPanel;
        this.bgInput = bgInput;
        this.accent = accent;
        this.accentHover = accentHover;
        this.success = success;
        this.danger = danger;
        this.textPrimary = textPrimary;
        this.textSecondary = textSecondary;
        this.tableAlt = tableAlt;
        this.tableSelected = tableSelected;
        this.buttonText = buttonText;
    }

    public static Theme dark() {
        return new Theme(
            "Sombre",
            new Color(30, 30, 46),      // bgDark
            new Color(39, 39, 58),      // bgPanel
            new Color(50, 50, 72),      // bgInput
            new Color(137, 180, 250),   // accent
            new Color(166, 205, 255),   // accentHover
            new Color(166, 227, 161),   // success
            new Color(243, 139, 168),   // danger
            new Color(205, 214, 244),   // textPrimary
            new Color(147, 153, 178),   // textSecondary
            new Color(45, 45, 65),      // tableAlt
            new Color(60, 60, 90),      // tableSelected
            new Color(30, 30, 46)       // buttonText
        );
    }

    public static Theme light() {
        return new Theme(
            "Clair",
            new Color(240, 240, 245),   // bgDark
            new Color(255, 255, 255),   // bgPanel
            new Color(230, 232, 240),   // bgInput
            new Color(59, 130, 246),    // accent
            new Color(96, 165, 250),    // accentHover
            new Color(34, 158, 75),     // success
            new Color(220, 53, 69),     // danger
            new Color(30, 30, 46),      // textPrimary
            new Color(100, 110, 130),   // textSecondary
            new Color(245, 245, 250),   // tableAlt
            new Color(219, 234, 254),   // tableSelected
            new Color(255, 255, 255)    // buttonText
        );
    }

    public boolean isDark() {
        return "Sombre".equals(name);
    }
}
