package com.example.mvci.note;

import com.example.domain.Part;
import com.example.mvci.framework.InteractorBase;

public class NoteInteractor extends InteractorBase<NoteModel> {

    protected NoteInteractor(NoteModel model) {
        super(model);
    }

    public void clearNote() {
        model.noteProperty().set("");
    }

    public void addPart(Part part) {
        String partEntry =
                "Installed part %s - %s."
                        .formatted(
                                part.partNumber(),
                                part.description()
                        );

        String currentNote =
                model.noteProperty().get();

        if (currentNote == null || currentNote.isBlank()) {
            model.noteProperty().set(partEntry);
            return;
        }

        model.noteProperty().set(
                currentNote.stripTrailing()
                        + System.lineSeparator()
                        + partEntry
        );
    }
}