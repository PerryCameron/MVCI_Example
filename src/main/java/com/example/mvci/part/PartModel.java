package com.example.mvci.part;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class PartModel {

    private final StringProperty partNumber = new SimpleStringProperty("");
    private final StringProperty description = new SimpleStringProperty("");

    private final ObservableList<String> parts =
            FXCollections.observableArrayList();

    public StringProperty partNumberProperty() {
        return partNumber;
    }

    public StringProperty descriptionProperty() {
        return description;
    }

    public ObservableList<String> getParts() {
        return parts;
    }
}