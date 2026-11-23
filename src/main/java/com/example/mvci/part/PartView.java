package com.example.mvci.part;

import com.example.domain.Part;
import com.example.mvci.framework.ViewBase;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.util.function.Consumer;

public class PartView extends ViewBase<Region, PartModel, PartMessage> {

    private ListView<Part> partsList;

    protected PartView(PartModel model, Consumer<PartMessage> action) {
        super(model, action);
    }

    @Override
    protected void viewListener() {
        // if this is used it should always be called first in build()
    }

    @Override
    public Region build() {
        Label heading = new Label("Parts");
        heading.getStyleClass().add("feature-title");
        Label partNumberLabel = new Label("Part Number");
        Label descriptionLabel = new Label("Description");
        Label availablePartsLabel = new Label("Available Parts");
        availablePartsLabel.getStyleClass().add("section-label");
        partsList = new ListView<>(model.getParts());
        model.selectedPartProperty().bind(partsList.getSelectionModel().selectedItemProperty());
        VBox content = new VBox(
                8,
                heading,
                partNumberLabel,
                partNumberField(),
                descriptionLabel,
                descriptionField(),
                partButton(),
                availablePartsLabel,
                partsList,
                addToWorkNoteButton()
        );
        content.setPadding(new Insets(20));
        content.getStyleClass().add("feature-card");
        return content;
    }

    private Control addToWorkNoteButton() {
        Button button = new Button("Add to Work Note");
        button.getStyleClass().add("secondary-button");
        button.disableProperty().bind(model.selectedPartProperty().isNull());
        button.setOnAction(event -> action.accept(PartMessage.ADD_TO_WORK_NOTE));
        return button;
    }

    private Control partButton() {
        Button button = new Button("Add Part");
        button.getStyleClass().add("primary-button");
        button.setOnAction(event -> action.accept(PartMessage.ADD_PART));
        return button;
    }

    private Node descriptionField() {
        TextField textField = new TextField();
        textField.setPromptText("Power Module");
        textField.textProperty().bindBidirectional(model.descriptionProperty());
        return textField;
    }

    private Node partNumberField() {
        TextField textField = new TextField();
        textField.setPromptText("PM123");
        textField.textProperty().bindBidirectional(model.partNumberProperty());
        return textField;
    }
}