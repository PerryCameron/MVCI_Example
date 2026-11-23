package com.example.mvci.main;

import com.example.domain.Part;
import com.example.mvci.framework.ControllerBase;
import com.example.mvci.note.NoteController;
import com.example.mvci.part.PartController;

public class MainController
        extends ControllerBase<MainModel, MainInteractor, MainView, MainMessage> {

    private PartController partController;
    private NoteController noteController;

    public MainController() {
        initializeMVCI();
        openNoteMVCI();
        openPartMVCI();

    }

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


    private void openPartMVCI() {
        partController = new PartController(
                this::addPartToWorkNote
        );

        interactor.showPartContent(
                partController.getView()
        );
    }

    private void openNoteMVCI() {
        noteController = new NoteController();

        interactor.showNoteContent(
                noteController.getView()
        );
    }

    private void addPartToWorkNote(Part part) {
        noteController.addPartToWorkNote(part);
    }
}