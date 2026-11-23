package com.example.mvci.part;

import com.example.mvci.framework.ControllerBase;

public class PartController
        extends ControllerBase<PartModel, PartInteractor, PartView, PartMessage> {

    public PartController() {
        initializeMVCI();
    }

    @Override
    protected PartModel createModel() {
        return new PartModel();
    }

    @Override
    protected PartInteractor createInteractor(PartModel model) {
        return new PartInteractor(model);
    }

    @Override
    protected PartView createView(PartModel model) {
        return new PartView(model, this::action);
    }

    @Override
    public void action(PartMessage actionEnum) {

        switch (actionEnum) {
            case ADD_PART -> interactor.addPart();
        }
    }
}