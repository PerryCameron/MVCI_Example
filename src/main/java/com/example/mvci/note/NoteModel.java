package com.example.mvci.note;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class NoteModel {

    private final StringProperty note =
            new SimpleStringProperty("");

    public StringProperty noteProperty() {
        return note;
    }
}
