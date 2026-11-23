# JavaFX MVCI AI Development Prompt

## Instructions for the AI

You are assisting with a JavaFX application that uses a strict **MVCI (Model-View-Controller-Interactor)** architecture.

The rules in this document define the architecture of the application.

Do not replace this architecture with conventional MVC, MVVM, MVP, FXML-based JavaFX, dependency injection frameworks, event buses, or another architectural pattern unless the user explicitly requests an architectural change.

When modifying an existing project, inspect the existing classes before proposing changes. Preserve the project's established behavior, naming conventions, and MVCI boundaries.

---

# Core Principle

MVCI provides:

> **Consistent separation of concerns that allows greater complexity without creating greater confusion.**

Each MVCI contains four components:

```text
Model
View
Controller
Interactor
```

The normal flow is:

```text
User
  ↓
View
  ↓
Controller
  ↓
Interactor
  ↓
Model
  ↓
View
```

Each component has a specific responsibility.

Do not move responsibilities between components merely to reduce the number of classes or lines of code.

---

# Model

The Model represents the observable state of an MVCI.

Models primarily contain:

- JavaFX Properties
- ObservableLists
- Observable application state
- State needed by the View
- State needed by the Interactor
- Message properties for explicit Model-to-View requests

Example:

```java
public class ExampleModel {

    private final StringProperty name =
            new SimpleStringProperty("");

    public StringProperty nameProperty() {
        return name;
    }
}
```

## Model Rules

The Model:

- Holds state.
- Does not contain business logic.
- Does not manipulate JavaFX controls.
- Does not access repositories.
- Does not coordinate with other MVCIs.
- Should normally use its default constructor.
- Exposes JavaFX Properties when observation or binding is appropriate.

Do not move Interactor responsibilities into the Model.

---

# View

The View constructs and manages the JavaFX user interface.

Views are written directly in Java.

**This architecture does not use FXML.**

Do not introduce:

```text
FXML
FXMLLoader
@FXML
FXML Controllers
```

The absence of FXML is intentional.

Views extend the project's `ViewBase` and use JavaFX's `Builder` pattern.

A typical View has this form:

```java
public class ExampleView
        extends ViewBase<Region, ExampleModel, ExampleMessage> {

    protected ExampleView(
            ExampleModel model,
            Consumer<ExampleMessage> action) {

        super(model, action);
    }

    @Override
    protected void viewListener() {

    }

    @Override
    public Region build() {
        // Build the JavaFX scene graph.
    }
}
```

## View Responsibilities

The View may:

- Create JavaFX controls.
- Arrange controls.
- Construct the scene graph.
- Bind controls to its own Model.
- Observe its own Model.
- Respond to Model-to-View messages.
- Apply semantic CSS classes.
- Translate user interaction into messages sent to its Controller.

Example:

```java
button.setOnAction(
        event -> action.accept(ExampleMessage.SAVE)
);
```

The View communicates **what the user requested**.

The View does not perform the requested business operation.

## View Rules

The View must not:

- Perform business logic.
- Access repositories.
- Access the Interactor.
- Access another MVCI.
- Access another MVCI's Model.
- Access another MVCI's View.
- Coordinate application-level operations.

---

# Controller

The Controller coordinates one MVCI.

Controllers extend the project's `ControllerBase`.

A Controller owns its:

```text
Model
Interactor
View
```

The standard initialization sequence is:

```text
Controller
    ↓
Model
    ↓
Interactor
    ↓
View
```

A typical Controller resembles:

```java
public class ExampleController
        extends ControllerBase<
                ExampleModel,
                ExampleInteractor,
                ExampleView,
                ExampleMessage> {

    private final MainController mainController;

    public ExampleController(MainController mainController) {
        this.mainController = mainController;
        initializeMVCI();
    }

    @Override
    protected ExampleModel createModel() {
        return new ExampleModel();
    }

    @Override
    protected ExampleInteractor createInteractor(
            ExampleModel model) {

        return new ExampleInteractor(model);
    }

    @Override
    protected ExampleView createView(
            ExampleModel model) {

        return new ExampleView(model, this::action);
    }

    @Override
    public void action(ExampleMessage actionEnum) {
        // Route the request.
    }
}
```

## Controller Responsibilities

The Controller may:

- Create its MVCI components.
- Receive actions from its View.
- Delegate work to its Interactor.
- Coordinate requests within its MVCI.
- Communicate outward through MainController.

## Controller Rules

The Controller must not:

- Perform business logic.
- Manipulate JavaFX controls.
- Replace an Interactor operation with direct Model mutation.
- Access another feature MVCI directly.

Controllers coordinate.

Interactors perform the work.

---

# MainController

`MainController` has a special role in this architecture.

MainController is the central coordination point for the application.

Every feature Controller receives a reference to MainController when the feature Controller is created.

For example:

```java
private final MainController mainController;

public ExampleController(MainController mainController) {
    this.mainController = mainController;
    initializeMVCI();
}
```

MainController creates the feature Controller using:

```java
exampleController =
        new ExampleController(this);
```

This relationship is deliberate.

---

# All External MVCI Communication Goes Through MainController

This is a fundamental architectural rule:

> **All communication leaving a feature MVCI goes through MainController.**

Feature MVCIs do not communicate directly with each other.

This is prohibited:

```text
MVCI A Controller
       ↓
MVCI B Controller
```

This is prohibited:

```text
MVCI A Interactor
       ↓
MVCI B Interactor
```

This is prohibited:

```text
MVCI A View
       ↓
MVCI B Model
```

The correct structure is:

```text
MVCI A
   ↓
MainController
   ↓
MVCI B
```

More generally:

```text
                    MainController
                  /       |        \
                 /        |         \
                ▼         ▼          ▼
             MVCI A    MVCI B     MVCI C
```

The feature Controllers know MainController.

Feature Controllers do **not** know each other.

---

# Cross-MVCI Requests

When an action needs to leave an MVCI, the feature Controller sends the request to MainController.

For example:

```java
@Override
public void action(ExampleMessage actionEnum) {

    switch (actionEnum) {
        case SAVE ->
                interactor.save();

        case SEND_TO_OTHER_FEATURE ->
                interactor.getSelectedItem()
                        .ifPresent(
                                mainController::sendToOtherFeature
                        );
    }
}
```

MainController determines the destination:

```java
public void sendToOtherFeature(
        DomainObject domainObject) {

    destinationController.receive(domainObject);
}
```

The destination Controller then delegates the operation to its own Interactor:

```java
public void receive(DomainObject domainObject) {
    interactor.receive(domainObject);
}
```

Therefore:

```text
Source View
    ↓
Source Controller
    ↓
MainController
    ↓
Destination Controller
    ↓
Destination Interactor
    ↓
Destination Model
    ↓
Destination View
```

Do not bypass this path.

---

# Do Not Replace MainController Communication With Indirection

Do not replace the explicit MainController relationship with:

- `Consumer` callbacks
- Event buses
- Global events
- Static communication
- Shared feature Models
- Direct feature Controller references
- Direct feature Interactor references
- Service locators
- Hidden callback chains

For this MVCI system, explicit communication through MainController is intentional.

Do not "decouple" a feature Controller from MainController unless the user explicitly requests an architectural change.

---

# Interactor

The Interactor contains application and business logic.

Interactors extend `InteractorBase`.

Example:

```java
public class ExampleInteractor
        extends InteractorBase<ExampleModel> {

    protected ExampleInteractor(ExampleModel model) {
        super(model);
    }

    public void save() {
        String value =
                model.valueProperty().get().trim();

        if (value.isEmpty()) {
            return;
        }

        // Perform the operation.

        model.valueProperty().set("");
    }
}
```

## Interactor Responsibilities

The Interactor may:

- Read its 