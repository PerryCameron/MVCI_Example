package com.example.mvci.part;

import com.example.domain.Part;
import com.example.mvci.framework.ControllerBase;

import java.util.function.Consumer;

public class PartController
        extends ControllerBase<PartModel, PartInteractor, PartView, PartMessage> {

    private final Consumer<Part> addToWorkNoteRequest;

    public PartController(Consumer<Part> addToWorkNoteRequest) {
        this.addToWorkNoteRequest = addToWorkNoteRequest;
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
            case ADD_PART ->
                    interactor.addPart();

            case ADD_TO_WORK_NOTE ->
                    interactor.getSelectedPart()
                            .ifPresent(addToWorkNoteRequest);
        }
    }
}