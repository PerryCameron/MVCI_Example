package com.example.mvci.part;

import com.example.mvci.framework.InteractorBase;

public class PartInteractor extends InteractorBase<PartModel> {

    protected PartInteractor(PartModel model) {
        super(model);
    }

    public void addPart() {
        String partNumber = model.partNumberProperty().get().trim();
        String description = model.descriptionProperty().get().trim();

        if (partNumber.isEmpty() || description.isEmpty()) {
            return;
        }

        model.getParts().add(
                partNumber + "  " + description
        );

        model.partNumberProperty().set("");
        model.descriptionProperty().set("");
    }
}