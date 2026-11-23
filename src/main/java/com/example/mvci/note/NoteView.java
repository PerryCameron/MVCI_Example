package com.example.mvci.note;

import com.example.mvci.framework.ViewBase;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.*;

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
        // if this is used it should always be called first in build()
    }
        // this is an example of why using Builder style is nice. You can break it into smaller groups for clarity
    @Override
    public Region build() {
        return getContentBox();
    }

    private Region getContentBox() {
        Label heading = new Label("Work Note");
        heading.getStyleClass().add("feature-title");
        Label noteLabel = new Label("Service Notes");
        Control noteArea = getNoteArea();
        VBox.setVgrow(noteArea, Priority.ALWAYS);
        VBox content = new VBox(8, heading, noteLabel, noteArea, buttonBar());
        content.setPadding(new Insets(20));
        content.getStyleClass().add("feature-card");
        return content;
    }

    private Region buttonBar() {
        HBox buttonBar = new HBox(clearButton());
        buttonBar.setAlignment(Pos.CENTER_RIGHT);
        return buttonBar;
    }

    private Control clearButton() {
        Button clearButton = new Button("Clear Note");
        clearButton.getStyleClass().add("secondary-button");
        clearButton.setOnAction(event -> action.accept(NoteMessage.CLEAR_NOTE));
        return clearButton;
    }

    private Control getNoteArea() {
        TextArea noteArea = new TextArea();
        noteArea.setPromptText("Enter service notes...");
        noteArea.setWrapText(true);
        noteArea.textProperty()
                .bindBidirectional(model.noteProperty());
        return noteArea;
    }
}
