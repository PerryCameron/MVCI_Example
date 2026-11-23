package com.example.mvci.note;

import com.example.mvci.framework.InteractorBase;

public class NoteInteractor extends InteractorBase<NoteModel> {

    protected NoteInteractor(NoteModel model) {
        super(model);
    }

    public void clearNote() {
        model.noteProperty().set("");
    }
}