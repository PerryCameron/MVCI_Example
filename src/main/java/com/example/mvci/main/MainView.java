package com.example.mvci.main;

import com.example.mvci.framework.ViewBase;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.util.function.Consumer;

public class MainView extends ViewBase<Region, MainModel, MainMessage> {

    protected MainView(MainModel model, Consumer<MainMessage> action) {
        super(model, action);
    }

    private BorderPane borderPane;

    @Override
    protected void viewListener() {
        model.messageProperty().addListener((obs, oldValue, newValue) -> {
            System.out.println("Message: " + newValue);
            if (newValue == MainMessage.SHOW_CONTENT) {
                borderPane.setCenter(model.contentProperty().get());
            }
        });
    }

    @Override
    public Region build() {
        System.out.println("Building main view");
        viewListener();
        borderPane = new BorderPane();

        borderPane.getStyleClass().add("app-root");
        borderPane.setTop(buildHeader());

        if (model.contentProperty().get() != null) {
            borderPane.setCenter(model.contentProperty().get());
        }
        return borderPane;
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