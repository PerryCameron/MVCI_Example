package com.example.mvci.note;

import com.example.mvci.framework.ViewBase;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.util.function.Consumer;

public class NoteView
        extends ViewBase<Region, NoteModel, NoteMessage> {

    protected NoteView(
            NoteModel model,
            Consumer<NoteMessage> action) {

        super(model, action);
    }

    @Override
    protected void viewListener() {

    }

    @Override
    public Region build() {
        Label heading = new Label("Work Note");
        heading.getStyleClass().add("feature-title");

        Label noteLabel = new Label("Service Notes");

        TextArea noteArea = new TextArea();
        noteArea.setPromptText("Enter service notes...");
        noteArea.setWrapText(true);

        noteArea.textProperty()
                .bindBidirectional(model.noteProperty());

        VBox.setVgrow(noteArea, Priority.ALWAYS);

        Button clearButton = new Button("Clear Note");
        clearButton.getStyleClass().add("secondary-button");

        clearButton.setOnAction(
                event -> action.accept(NoteMessage.CLEAR_NOTE)
        );

        HBox buttonBar = new HBox(clearButton);
        buttonBar.setAlignment(Pos.CENTER_RIGHT);

        VBox content = new VBox(
                8,
                heading,
                noteLabel,
                noteArea,
                buttonBar
        );

        content.setPadding(new Insets(20));
        content.getStyleClass().add("feature-card");

        return content;
    }
}
