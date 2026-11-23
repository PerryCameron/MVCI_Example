package com.example.mvci.part;

import com.example.domain.Part;
import com.example.mvci.framework.InteractorBase;

import java.util.Optional;

public class PartInteractor extends InteractorBase<PartModel> {

    protected PartInteractor(PartModel model) {
        super(model);
    }

    public void addPart() {
        String partNumber =
                model.partNumberProperty().get().trim();

        String description =
                model.descriptionProperty().get().trim();

        if (partNumber.isEmpty() || description.isEmpty()) {
            return;
        }

        Part part = new Part(partNumber, description);

        model.getParts().add(part);

        model.partNumberProperty().set("");
        model.descriptionProperty().set("");
    }

    public Optional<Part> getSelectedPart() {
        return Optional.ofNullable(
                model.selectedPartProperty().get()
        );
    }
}