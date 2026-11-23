package com.example.mvci.main;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.layout.Region;

public class MainModel {

    private final ObjectProperty<Region> partContent =
            new SimpleObjectProperty<>();

    private final ObjectProperty<Region> noteContent =
            new SimpleObjectProperty<>();

    private final ObjectProperty<MainMessage> message =
            new SimpleObjectProperty<>(MainMessage.NO_REQUEST);

    public ObjectProperty<Region> partContentProperty() {
        return partContent;
    }

    public ObjectProperty<Region> noteContentProperty() {
        return noteContent;
    }

    public ObjectProperty<MainMessage> messageProperty() {
        return message;
    }
}