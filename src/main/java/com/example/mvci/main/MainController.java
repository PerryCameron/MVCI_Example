package com.example.mvci.main;

import com.example.mvci.framework.ControllerBase;
import com.example.mvci.part.PartController;

public class MainController extends ControllerBase<MainModel, MainInteractor, MainView, MainMessage> {

    public MainController() {
        initializeMVCI();
        openPartMVCI();
    }

    private PartController partController;

    @Override
    protected MainModel createModel() {
        return new MainModel();
    }

    @Override
    protected MainInteractor createInteractor(MainModel model) {
        return new MainInteractor(model);
    }

    @Override
    protected MainView createView(MainModel model) {
        return new MainView(model, this::action);
    }

    @Override
    public void action(MainMessage actionEnum) {

    }

    // we have not called this
    private void openPartMVCI() {
        partController = new PartController();
        interactor.showContent(partController.getView());
    }
}
