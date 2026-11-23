package com.example.mvci.note;

import com.example.mvci.framework.ControllerBase;

public class NoteController
        extends ControllerBase<NoteModel, NoteInteractor, NoteView, NoteMessage> {

    public NoteController() {
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
}