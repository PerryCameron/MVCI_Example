package com.example.mvci.part;

import com.example.mvci.framework.ViewBase;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.util.function.Consumer;

public class PartView
        extends ViewBase<Region, PartModel, PartMessage> {

    private TextField partNumberField;
    private TextField descriptionField;
    private ListView<String> partsList;

    protected PartView(
            PartModel model,
            Consumer<PartMessage> action) {

        super(model, action);
    }

    @Override
    protected void viewListener() {

    }

    @Override
    public Region build() {
        System.out.println("Building part view");
        Label heading = new Label("Parts");
        heading.getStyleClass().add("feature-title");

        Label partNumberLabel = new Label("Part Number");

        partNumberField = new TextField();
        partNumberField.setPromptText("PM123");
        partNumberField.textProperty()
                .bindBidirectional(model.partNumberProperty());

        Label descriptionLabel = new Label("Description");

        descriptionField = new TextField();
        descriptionField.setPromptText("Power Module");
        descriptionField.textProperty()
                .bindBidirectional(model.descriptionProperty());

        Button addPartButton = new Button("Add Part");
        addPartButton.getStyleClass().add("primary-button");

        addPartButton.setOnAction(
                event -> action.accept(PartMessage.ADD_PART)
        );

        Label availablePartsLabel = new Label("Available Parts");
        availablePartsLabel.getStyleClass().add("section-label");

        partsList = new ListView<>(model.getParts());

        VBox content = new VBox(
                8,
                heading,
                partNumberLabel,
                partNumberField,
                descriptionLabel,
                descriptionField,
                addPartButton,
                availablePartsLabel,
                partsList
        );

        content.setPadding(new Insets(20));
        content.getStyleClass().add("feature-card");

        return content;
    }
}