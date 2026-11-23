package com.example.mvci.part;

import com.example.domain.Part;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class PartModel {

    private final StringProperty partNumber =
            new SimpleStringProperty("");

    private final StringProperty description =
            new SimpleStringProperty("");

    private final ObservableList<Part> parts =
            FXCollections.observableArrayList();

    private final ObjectProperty<Part> selectedPart =
            new SimpleObjectProperty<>();

    public StringProperty partNumberProperty() {
        return partNumber;
    }

    public StringProperty descriptionProperty() {
        return description;
    }

    public ObservableList<Part> getParts() {
        return parts;
    }

    public ObjectProperty<Part> selectedPartProperty() {
        return selectedPart;
    }
}