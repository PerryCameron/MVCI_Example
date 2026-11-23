package com.example.mvci.main;

import com.example.mvci.framework.ViewBase;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;

import java.util.function.Consumer;

public class MainView extends ViewBase<Region, MainModel, MainMessage> {

    protected MainView(MainModel model, Consumer<MainMessage> action) {
        super(model, action);
    }

    private BorderPane borderPane;

    @Override
    protected void viewListener() {
        model.messageProperty().addListener((obs, oldValue, newValue) -> {

            switch (newValue) {
                case SHOW_PARTS, SHOW_NOTE -> updateContent();

                case NO_REQUEST -> {
                }
            }
        });
    }

    @Override
    public Region build() {
        borderPane = new BorderPane();

        borderPane.getStyleClass().add("app-root");
        borderPane.setTop(buildHeader());

        updateContent();

        viewListener();

        return borderPane;
    }

    private void updateContent() {
        Region partContent = model.partContentProperty().get();
        Region noteContent = model.noteContentProperty().get();

        if (partContent == null && noteContent == null) {
            return;
        }

        HBox content = new HBox(16);
        content.getStyleClass().add("main-content");
        content.setAlignment(Pos.CENTER);

        if (partContent != null) {
            HBox.setHgrow(partContent, Priority.ALWAYS);
            partContent.setMaxWidth(Double.MAX_VALUE);
            content.getChildren().add(partContent);
        }

        if (noteContent != null) {
            HBox.setHgrow(noteContent, Priority.ALWAYS);
            noteContent.setMaxWidth(Double.MAX_VALUE);
            content.getChildren().add(noteContent);
        }

        borderPane.setCenter(content);
    }

    private Region buildHeader() {
        Label title = new Label("MVCI Service Example");
        title.getStyleClass().add("app-title");

        Label subtitle = new Label("Parts and Work Notes");
        subtitle.getStyleClass().add("app-subtitle");

        VBox header = new VBox(4, title, subtitle);
        header.getStyleClass().add("app-header");
        header.setPadding(new Insets(20, 24, 20, 24));

        return header;
    }
}