package com.example.mvci.main;

import com.example.mvci.framework.InteractorBase;

import javafx.scene.layout.Region;

public class MainInteractor extends InteractorBase<MainModel> {

    protected MainInteractor(MainModel model) {
        super(model);
    }

    public void showContent(Region region) {
        model.contentProperty().set(region);
        model.messageProperty().set(MainMessage.NO_REQUEST);
        model.messageProperty().set(MainMessage.SHOW_CONTENT);
    }
}

