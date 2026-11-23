# JavaFX MVCI Example

A working JavaFX example demonstrating the **MVCI (Model-View-Controller-Interactor)** architectural pattern.

This project accompanies the JavaFX MVCI Guide and demonstrates how multiple independent MVCI units can work together while maintaining strict separation of concerns.

Unlike the companion **MVCI Stub Project**, this repository is a complete working example rather than a starting template.

---

## What This Example Demonstrates

The application contains three MVCI units:

```text
Main MVCI
Part MVCI
Note MVCI
```

The application allows the user to:

1. Enter a part number and description.
2. Add a part to the available parts list.
3. Select a part.
4. Add the selected part to a work note.
5. Continue building the work note with additional parts.
6. Clear the work note.

The functionality is intentionally simple. The purpose is to demonstrate how application behavior moves through MVCI and, most importantly, how separate MVCIs communicate without becoming directly coupled.

---

## Requirements

- Java 21
- JavaFX
- Gradle
- A Java IDE such as IntelliJ IDEA

### Recommended JDK

For simplicity, use an OpenJDK distribution that already includes JavaFX. This project was developed using **BellSoft Liberica JDK 21 Full**.

The exact distribution is not required. Other Java 21 OpenJDK distributions that include JavaFX may also be used. A JDK with JavaFX bundled avoids requiring a separate JavaFX SDK and keeps setup focused on the application and its architecture rather than module-path configuration.

---

## Running the Project

The Gradle Wrapper is included, so a separate Gradle installation is not required.

On Windows:

```powershell
.\gradlew clean build
.\gradlew run
```

The application can also be imported into IntelliJ IDEA and run from the `Main` class.

---

## Project Structure

```text
src/
└── main/
    ├── java/
    │   └── com/
    │       └── example/
    │           ├── Main.java
    │           ├── domain/
    │           │   └── Part.java
    │           └── mvci/
    │               ├── framework/
    │               │   ├── ControllerBase.java
    │               │   ├── InteractorBase.java
    │               │   └── ViewBase.java
    │               ├── main/
    │               │   ├── MainController.java
    │               │   ├── MainInteractor.java
    │               │   ├── MainMessage.java
    │               │   ├── MainModel.java
    │               │   └── MainView.java
    │               ├── part/
    │               │   ├── PartController.java
    │               │   ├── PartInteractor.java
    │               │   ├── PartMessage.java
    │               │   ├── PartModel.java
    │               │   └── PartView.java
    │               └── note/
    │                   ├── NoteController.java
    │                   ├── NoteInteractor.java
    │                   ├── NoteMessage.java
    │                   ├── NoteModel.java
    │                   └── NoteView.java
    └── resources/
        ├── css/
        │   └── application.css
        └── docs/
            └── MVCI_AI_PROMPT.md
```

---

## The MVCI Pattern

MVCI separates application responsibilities into four components:

```text
Model
View
Controller
Interactor
```

A normal user operation follows:

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

Each component has a deliberately limited responsibility.

### Model

The **Model** represents observable application state. Models primarily contain JavaFX properties, observable collections, state observed by the View, state used by the Interactor, and message properties for explicit Model-to-View requests.

The Model represents state. It does not perform business logic. Models normally use the default constructor unless there is a specific architectural reason otherwise.

### View

The **View** constructs and manages the JavaFX user interface. Views create controls, arrange the scene graph, bind to their own Model, observe Model state, respond to Model-to-View messages, apply CSS classes, and send user intent to their Controller.

```java
button.setOnAction(
        event -> action.accept(PartMessage.ADD_PART)
);
```

The View reports what the user requested. It does not perform the requested business operation.

### Controller

The **Controller** coordinates its MVCI. Each Controller owns its Model, Interactor, and View. It receives action messages from the View and delegates application work to the Interactor.

```java
case ADD_PART ->
        interactor.addPart();
```

The Controller coordinates. It does not perform business logic.

### Interactor

The **Interactor** performs application and business logic. It may read Model state, validate input, transform data, update Model state, and communicate with repositories or services.

The Interactor does not manipulate JavaFX controls.

---

## No FXML

This implementation intentionally does **not** use FXML.

There are no FXML files, `FXMLLoader` instances, `@FXML` fields, or FXML Controllers. The JavaFX scene graph is constructed directly in Java by each View.

This is a deliberate architectural decision, not an unfinished part of the application.

---

## Controller Construction Rule

This MVCI system has a strict Controller construction rule.

`MainController` takes no constructor parameters:

```java
public MainController() {
    initializeMVCI();
}
```

Every other Controller takes `MainController` as its **only** constructor parameter:

```java
private final MainController mainController;

public PartController(MainController mainController) {
    this.mainController = mainController;
    initializeMVCI();
}
```

Likewise:

```java
public NoteController(MainController mainController) {
    this.mainController = mainController;
    initializeMVCI();
}
```

A feature Controller does not receive repositories, services, callbacks, sibling Controllers, or additional constructor dependencies. If communication must leave the MVCI, the feature Controller uses `MainController`.

---

## All Cross-MVCI Communication Goes Through MainController

A fundamental rule of this architecture is:

> **All communication between separate MVCIs goes through MainController.**

Feature MVCIs never communicate directly with one another.

This is prohibited:

```text
PartController
      ↓
NoteController
```

This is also prohibited:

```text
PartInteractor
      ↓
NoteInteractor
```

The correct path is:

```text
Part MVCI
    ↓
MainController
    ↓
Note MVCI
```

Passing `MainController` into each feature Controller makes the application's communication topology explicit:

```text
                    MainController
                  /                \
                 ▼                  ▼
        PartController       NoteController
```

The feature Controllers know `MainController`. They do not know each other.

---

## The Centerpiece: Add Part to Work Note

The primary architectural example is the **Add to Work Note** operation.

The complete path is:

```text
PartView
    ↓
PartController
    ↓
MainController
    ↓
NoteController
    ↓
NoteInteractor
    ↓
NoteModel
    ↓
NoteView
```

The user selects a Part in `PartView` and requests `ADD_TO_WORK_NOTE`. `PartController` obtains the selected Part from its own MVCI and sends the application-level request to `MainController`.

`MainController` understands the relationship between feature MVCIs and routes the Part to `NoteController`. `NoteController` delegates to `NoteInteractor`, which updates `NoteModel`. `NoteView` reflects the changed state.

At no point does the Part MVCI directly access `NoteController`, `NoteInteractor`, `NoteModel`, or `NoteView`.

---

## Main MVCI

The Main MVCI is both the application shell and the coordination point for other MVCIs.

### MainController

`MainController` creates and retains feature Controllers, passing itself to each one:

```java
private void openPartMVCI() {
    partController = new PartController(this);

    interactor.showPartContent(
            partController.getView()
    );
}

private void openNoteMVCI() {
    noteController = new NoteController(this);

    interactor.showNoteContent(
            noteController.getView()
    );
}
```

`MainController` is the one place that understands the application's MVCI topology. It remains a Controller, so it coordinates rather than accumulating business logic.

### MainView

`MainView` provides the application shell and visual layout for feature Regions. It does not construct `PartController`, `PartView`, `NoteController`, or `NoteView`.

Feature MVCIs are launched by `MainController`. Their root JavaFX `Region` objects become Main MVCI state and are displayed by `MainView` through the normal Main Controller → Main Interactor → Main Model → Main View path.

---

## Shared Domain Objects

The project contains a shared domain record:

```text
com.example.domain.Part
```

```java
public record Part(
        String partNumber,
        String description) {
}
```

`Part` belongs to neither the Part MVCI nor the Note MVCI. It represents application data and can travel through:

```text
PartController
    ↓
MainController
    ↓
NoteController
```

Sharing domain data is allowed. Direct access to another MVCI's internal components is not.

---

## JavaFX Binding

The example uses JavaFX properties and binding to synchronize Model and View state.

Editable state may use bidirectional binding:

```java
partNumberField.textProperty()
        .bindBidirectional(
                model.partNumberProperty()
        );
```

Selection may use one-way binding:

```java
model.selectedPartProperty().bind(
        partsList.getSelectionModel()
                .selectedItemProperty()
);
```

Here, the `ListView` owns the selection. Because `selectedPartProperty()` is bound, it must not also be set manually. Doing so causes:

```text
A bound value cannot be set.
```

State ownership should be determined before choosing binding direction.

---

## Model-to-View Messages

When a View must respond to an explicit request rather than ordinary property state, the Model may contain a message property.

Message enums may include values such as:

```text
NO_REQUEST
SHOW_PARTS
SHOW_NOTE
```

`NO_REQUEST` is used as an explicit idle state instead of `null`. This keeps communication type-safe and prevents one message from being reused for unrelated directions.

---

## CSS

Application styling is located at:

```text
src/main/resources/css/application.css
```

Views apply semantic style classes such as:

```text
app-header
feature-card
primary-button
secondary-button
```

The View determines what an element represents. CSS determines how the element looks.

---

## MVCI Framework

The example includes three shared framework classes:

```text
ControllerBase
InteractorBase
ViewBase
```

These classes standardize MVCI initialization and relationships without hiding the architecture. Individual MVCI classes remain intentionally explicit so the communication path can be followed directly in the source.

---

## General MVCI AI Prompt

This repository includes:

```text
src/main/resources/docs/MVCI_AI_PROMPT.md
```

The prompt defines the **general MVCI system**, not this example application specifically. It can be provided to an AI coding assistant before asking the AI to design, modify, debug, or extend any application built with this MVCI architecture.

The prompt establishes rules covering:

- Model, View, Controller, and Interactor responsibilities
- The strict Controller constructor rule
- MainController's application-wide coordination role
- Cross-MVCI communication
- View-to-Controller messages
- Model-to-View messages
- JavaFX properties and binding
- Repository placement
- Threading responsibilities
- CSS
- The deliberate absence of FXML

Most importantly:

> **MainController takes no constructor parameters. Every other Controller takes MainController as its only constructor parameter. All communication leaving a feature MVCI goes through MainController.**

The prompt prevents an AI assistant from replacing this architecture with callbacks, event buses, direct feature-to-feature communication, FXML, or another JavaFX pattern.

---

## Companion Resources

### MVCI Guide

The MVCI Guide explains the architecture and the reasoning behind its boundaries.

Use it to **learn MVCI**.

### MVCI Stub Project

[MVCI Stub Project](https://github.com/PerryCameron/MVCI_Stub)

A minimal, working JavaFX project containing the MVCI framework and a Main MVCI. Use it as the clean starting point for a new application.

Use it to **start an MVCI project**.

### MVCI Example Project

[MVCI Example Project](https://github.com/PerryCameron/MVCI_Example)

This repository. It demonstrates multiple MVCIs, MainController coordination, shared domain data, JavaFX binding, CSS, and a complete cross-MVCI workflow.

Use it to **see MVCI working**.

### MVCI AI Prompt

[MVCI AI Prompt](https://github.com/PerryCameron/MVCI_Example/blob/main/src/main/resources/docs/MVCI_AI_PROMPT.md)

A reusable context document that teaches an AI coding assistant how to work within the MVCI system without redesigning it.

Use it to **teach an AI the MVCI rules**.

---

## Responsibility Summary

| Responsibility | Owner |
|---|---|
| Create JavaFX controls | View |
| Arrange the interface | View |
| Apply semantic CSS classes | View |
| Report user intent | View → Controller |
| Coordinate an MVCI operation | Controller |
| Perform application logic | Interactor |
| Store observable state | Model |
| Retrieve or persist data | Repository through Interactor |
| Coordinate independent MVCIs | MainController |
| Communicate outside a feature MVCI | Feature Controller → MainController |
| Display changed state | View observing Model |

---

## Design Philosophy

The purpose of MVCI is not to create the fewest classes or the fewest lines of code.

The purpose is:

> **Consistent separation of concerns that allows greater complexity without creating greater confusion.**

Each component answers a different question:

```text
View
    What does the user see?
    What did the user request?

Controller
    Where should the request go?

Interactor
    What work needs to happen?

Model
    What is the current observable state?

MainController
    How do independent MVCIs communicate?
```

The core topology is:

```text
                         MainController
                       /       |        \
                      ▼        ▼         ▼
                   MVCI A   MVCI B    MVCI C
```

A feature MVCI communicates internally through its own Model, View, Controller, and Interactor. When communication must leave that MVCI:

```text
Feature MVCI
     ↓
MainController
     ↓
Destination MVCI
```

That boundary remains the same as the application grows.
