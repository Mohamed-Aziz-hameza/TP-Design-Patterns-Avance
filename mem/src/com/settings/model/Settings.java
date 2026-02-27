package com.settings.model;

import java.time.LocalDateTime;

public class Settings {

    private int volume;
    private int brightness;
    private boolean darkMode;

    public Settings() {
        this.volume = 50;
        this.brightness = 70;
        this.darkMode = false;
    }

    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        if (volume < 0 || volume > 100) {
            throw new IllegalArgumentException("Volume must be between 0 and 100.");
        }
        this.volume = volume;
    }

    public int getBrightness() {
        return brightness;
    }

    public void setBrightness(int brightness) {
        if (brightness < 0 || brightness > 100) {
            throw new IllegalArgumentException("Brightness must be between 0 and 100.");
        }
        this.brightness = brightness;
    }

    public boolean isDarkMode() {
        return darkMode;
    }

    public void setDarkMode(boolean darkMode) {
        this.darkMode = darkMode;
    }

    public SettingsMemento save() {
        return new SettingsMemento(volume, brightness, darkMode, LocalDateTime.now());
    }

    public void restore(SettingsMemento memento) {
        this.volume = memento.getVolume();
        this.brightness = memento.getBrightness();
        this.darkMode = memento.isDarkMode();
    }

    @Override
    public String toString() {
        return String.format("Settings [Volume=%d, Brightness=%d, DarkMode=%s]",
                volume, brightness, darkMode ? "On" : "Off");
    }
}
