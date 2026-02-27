package com.settings.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SettingsCaretaker {

    private final List<SettingsMemento> history;

    public SettingsCaretaker() {
        this.history = new ArrayList<>();
    }

    public void addMemento(SettingsMemento memento) {
        history.add(memento);
    }

    public List<SettingsMemento> getHistory() {
        return Collections.unmodifiableList(history);
    }

    public SettingsMemento getMemento(int index) {
        if (index < 0 || index >= history.size()) {
            throw new IndexOutOfBoundsException("Invalid version index: " + index);
        }
        return history.get(index);
    }

    public int size() {
        return history.size();
    }
}
