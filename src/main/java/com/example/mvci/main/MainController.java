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

    // All controllers are built here, and thier views in the mainView.
    private void openPartMVCI() {
        partController = new PartController(this);

        interactor.showPartContent(
                partController.getView()
        );
    }

    private void openNoteMVCI() {
        noteController = new NoteController(this);
        // if you need to inject a reference or an Action Enum the mvci structure is built and this is the place to do it.
        // ie.. noteController.SetVariable(someVariable);
        interactor.showNoteContent(
                noteController.getView()
        );
    }

    public void addPartToWorkNote(Part part) {
        noteController.addPartToWorkNote(part);
    }
}