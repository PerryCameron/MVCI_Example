package com.example.mvci.main;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.layout.Region;

public class MainModel {

    private final ObjectProperty<Region> content =
            new SimpleObjectProperty<>();

    private final ObjectProperty<MainMessage> message =
            new SimpleObjectProperty<>(MainMessage.NO_REQUEST);

    public ObjectProperty<Region> contentProperty() {
        return content;
    }

    public ObjectProperty<MainMessage> messageProperty() {
        return message;
    }
}