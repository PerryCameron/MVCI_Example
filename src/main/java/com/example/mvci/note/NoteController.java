package com.example.mvci.note;

import com.example.domain.Part;
import com.example.mvci.framework.ControllerBase;
import com.example.mvci.main.MainController;

public class NoteController
        extends ControllerBase<NoteModel, NoteInteractor, NoteView, NoteMessage> {

    private final MainController mainController;

    public NoteController(MainController mainController) {
        this.mainController = mainController;
        initializeMVCI();
    }

    @Override
    protected NoteModel createModel() {
        return new NoteModel();
    }

    @Override
    protected NoteInteractor createInteractor(NoteModel model) {
        return new NoteInteractor(model);
    }

    @Override
    protected NoteView createView(NoteModel model) {
        return new NoteView(model, this::action);
    }

    @Override
    public void action(NoteMessage actionEnum) {

        switch (actionEnum) {
            case CLEAR_NOTE -> interactor.clearNote();
        }
    }

    public void addPartToWorkNote(Part part) {
        interactor.addPart(part);
    }
}