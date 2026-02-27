package com.settings.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class SettingsMemento {

    private final int volume;
    private final int brightness;
    private final boolean darkMode;
    private final LocalDateTime timestamp;

    public SettingsMemento(int volume, int brightness, boolean darkMode, LocalDateTime timestamp) {
        this.volume = volume;
        this.brightness = brightness;
        this.darkMode = darkMode;
        this.timestamp = timestamp;
    }

    public int getVolume() {
        return volume;
    }

    public int getBrightness() {
        return brightness;
    }

    public boolean isDarkMode() {
        return darkMode;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getFormattedTimestamp() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return timestamp.format(formatter);
    }

    @Override
    public String toString() {
        return String.format("Volume: %d | Brightness: %d | Dark Mode: %s | Saved: %s",
                volume, brightness, darkMode ? "On" : "Off", getFormattedTimestamp());
    }
}
