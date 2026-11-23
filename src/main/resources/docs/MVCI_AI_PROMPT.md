# JavaFX MVCI AI Development Prompt

## Instructions for the AI

You are assisting with a JavaFX application that uses a strict **MVCI (Model-View-Controller-Interactor)** architecture.

The rules in this document define the architecture of the application.

Do not replace this architecture with conventional MVC, MVVM, MVP, FXML-based JavaFX, dependency-injection frameworks, event buses, or another architectural pattern unless the user explicitly requests an architectural change.

When modifying an existing project:

1. Inspect the existing classes before proposing changes.
2. Preserve existing behavior.
3. Preserve existing MVCI boundaries.
4. Do not invent project-specific methods, fields, constructors, or APIs.
5. Make the smallest change that correctly fits the architecture.

---

# 1. Core Principle

The purpose of MVCI is:

> **Consistent separation of concerns that allows greater complexity without creating greater confusion.**

Each MVCI unit contains four primary components:

```text
Model
View
Controller
Interactor
```

The normal application flow is:

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

Do not move responsibilities between components merely to reduce the number of classes, methods, messages, or lines of code.

Explicit boundaries are intentional.

---

# 2. No FXML

This architecture **does not use FXML**.

Views are constructed programmatically in Java.

Do not introduce:

```text
FXML
FXMLLoader
@FXML
FXML Controllers
```

Do not recommend converting Views to FXML.

Do not treat the absence of FXML as an unfinished part of the application.

The View classes and MVCI framework are deliberately designed around Java-based View construction.

---

# 3. The Model

The Model represents the observable state of one MVCI.

Models primarily contain:

- JavaFX properties
- Observable collections
- Application state
- State observed by the View
- State manipulated by the Interactor
- Message properties used for explicit Model-to-View requests

For example:

```java
public class ExampleModel {

    private final StringProperty value =
            new SimpleStringProperty("");

    private final ObjectProperty<ExampleMessage> message =
            new SimpleObjectProperty<>(
                    ExampleMessage.NO_REQUEST
            );

    public StringProperty valueProperty() {
        return value;
    }

    public ObjectProperty<ExampleMessage> messageProperty() {
        return message;
    }
}
```

## Model Rules

The Model:

- Holds state.
- Does not contain business logic.
- Does not manipulate JavaFX controls.
- Does not access repositories.
- Does not access Controllers.
- Does not access Interactors.
- Does not communicate with another MVCI.
- Should normally use Java's default constructor.
- Initializes properties where they are declared.

Unless there is a compelling architectural reason otherwise, **do not create a custom Model constructor**.

The Model represents state, not behavior.

---

# 4. The View

The View is the presentation layer of an MVCI.

The View constructs and manages its JavaFX scene graph.

The View may:

- Create JavaFX controls.
- Create layouts.
- Read its own Model.
- Write appropriate state to its own Model.
- Observe its own Model.
- Bind controls to its own Model.
- Listen for Model-to-View messages.
- Apply CSS style classes.
- Perform presentation-only operations.
- Send user intent to its Controller.

The View must not:

- Perform business logic.
- Access repositories.
- Access its Interactor.
- Access another Controller.
- Access another MVCI.
- Coordinate application-level operations.

---

# 5. View-to-Controller Communication

A View sends user intent to its Controller through a typed enum callback.

The callback is provided when the View is created:

```java
return new ExampleView(
        model,
        this::action
);
```

Inside the View:

```java
button.setOnAction(
        event ->
                action.accept(
                        ExampleMessage.SAVE
                )
);
```

The View reports:

> What did the user request?

The View does not determine how the requested work is performed.

This relationship is:

```text
User
  ↓
View
  ↓
Action Enum
  ↓
Controller
```

Do not call the Interactor directly from the View.

---

# 6. Builder-Style Views

Views use JavaFX's `Builder<T>` pattern.

A View's `build()` method creates and returns the root of its JavaFX scene graph.

For example:

```java
@Override
public Region build() {
    BorderPane root = new BorderPane();

    root.setTop(buildHeader());
    root.setCenter(buildContent());

    return root;
}
```

Complex Views should use small builder-style helper methods:

```java
private Region buildHeader() {
    // Build header.
}

private Region buildContent() {
    // Build content.
}
```

The Java source should make the JavaFX scene graph easy to understand.

Do not move UI construction into the Controller or Interactor for convenience.

---

# 7. The Controller

The Controller coordinates one MVCI unit.

The Controller owns:

```text
Model
Interactor
View
```

The Controller:

- Creates its MVCI components.
- Receives actions from its View.
- Routes requests.
- Delegates work to its Interactor.
- Communicates with `MainController` when a request must leave its MVCI.

The Controller coordinates.

The Controller does **not** perform business logic.

For example:

```java
@Override
public void action(ExampleMessage actionEnum) {

    switch (actionEnum) {
        case SAVE ->
                interactor.save();

        case DELETE ->
                interactor.delete();
    }
}
```

The Controller decides **where a request goes**.

The Interactor decides **how the work is performed**.

---

# 8. Critical Controller Constructor Rule

This is a fundamental rule of this MVCI system.

There are exactly two Controller-construction patterns.

## MainController

`MainController` takes **no constructor parameters**.

```java
public MainController() {
    initializeMVCI();
}
```

`MainController` is the root application Controller.

It does not have a parent Controller.

## Every Other Controller

Every Controller other than `MainController` takes:

```java
MainController
```

as its constructor parameter.

It takes **nothing else**.

For example:

```java
private final MainController mainController;

public ExampleController(
        MainController mainController) {

    this.mainController = mainController;
    initializeMVCI();
}
```

The constructor must not become:

```java
ExampleController(
        MainController mainController,
        Repository repository)
```

or:

```java
ExampleController(
        MainController mainController,
        ExecutorService executor)
```

or:

```java
ExampleController(
        OtherController controller)
```

or:

```java
ExampleController(
        Consumer<SomeData> callback)
```

or:

```java
ExampleController(
        ModelFromAnotherMVCI model)
```

The architectural rule is:

```text
MainController()
```

and:

```text
EveryOtherController(MainController mainController)
```

Nothing else should be added to a feature Controller's constructor.

If additional application-level information is required, coordinate its delivery through `MainController` after construction.

---

# 9. MainController

`MainController` has a special role.

It is the application's Controller hub.

It:

- Owns or creates the other Controllers.
- Launches MVCI units.
- Coordinates communication between MVCIs.
- Routes application-level requests.
- Owns application-wide infrastructure when appropriate.
- Remains the single communication path between otherwise independent MVCIs.

A feature MVCI may be created like this:

```java
private void openExampleMVCI() {
    exampleController =
            new ExampleController(this);

    // Additional references or initial data may
    // be handed to the MVCI through controlled
    // methods after construction.

    interactor.showContent(
            exampleController.getView()
    );
}
```

All feature Controllers therefore share one predictable relationship:

```text
                 MainController
                /      |       \
               /       |        \
              ▼        ▼         ▼
        Controller A Controller B Controller C
```

Feature Controllers do not need references to one another.

---

# 10. All Communication Leaving an MVCI Goes Through MainController

This is one of the most important architectural rules.

> **Every Controller other than MainController communicates with other MVCI units only through MainController.**

Feature MVCIs do not communicate directly with one another.

This is prohibited:

```text
Controller A
     ↓
Controller B
```

This is prohibited:

```text
Interactor A
     ↓
Interactor B
```

This is prohibited:

```text
View A
     ↓
Model B
```

This is prohibited:

```text
Model A
     ↓
Controller B
```

Instead:

```text
MVCI A
   ↓
MainController
   ↓
MVCI B
```

The complete application-level flow might therefore be:

```text
View A
   ↓
Controller A
   ↓
MainController
   ↓
Controller B
   ↓
Interactor B
   ↓
Model B
   ↓
View B
```

MVCI A does not need to know that MVCI B exists.

`MainController` owns that knowledge.

---

# 11. Do Not Replace MainController Communication With Hidden Indirection

Do not try to "decouple" a feature Controller from `MainController` by replacing the explicit relationship with:

- `Consumer` callbacks
- Event buses
- Global events
- Static event handlers
- Shared feature Models
- Direct Controller references
- Direct Interactor references
- Service locators
- Hidden callback chains
- Global mutable state

The explicit relationship:

```java
new ExampleController(this)
```

is intentional.

If a feature needs to communicate outside itself, its Controller communicates through its existing `MainController` reference.

Do not add another constructor dependency merely because the feature gains a new responsibility.

---

# 12. Cross-MVCI Data Transfer

When data must move from one MVCI to another, the data travels through `MainController`.

For example:

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

The source Controller may retrieve required data from its Interactor:

```java
interactor.getSelectedItem()
        .ifPresent(
                mainController::sendItem
        );
```

`MainController` may then route the data:

```java
public void sendItem(ItemDTO item) {
    destinationController.receiveItem(item);
}
```

The destination Controller delegates to its own Interactor:

```java
public void receiveItem(ItemDTO item) {
    interactor.receiveItem(item);
}
```

This preserves the MVCI boundary.

---

# 13. Shared Data Objects

Data being passed between MVCIs should be DTOs, records, domain objects, or another appropriate data representation.

Do not pass another MVCI's:

- View
- Model
- Interactor
- Controller

merely to obtain data from it.

For instance, prefer:

```java
mainController.openViewer(selectedItem);
```

over:

```java
mainController.openViewer(
        thisView,
        thisModel
);
```

unless the architecture explicitly requires a UI reference for a specific presentation responsibility.

The default assumption should be:

> Pass the data, not the internals of another MVCI.

---

# 14. The Interactor

The Interactor contains the application and business logic for one MVCI.

The Interactor may:

- Read its Model.
- Update its Model.
- Validate data.
- Transform data.
- Perform calculations.
- Implement business rules.
- Access repositories.
- Access application services.
- Perform background work.
- Return data to its Controller when that data must leave the MVCI.

The Interactor must not:

- Reference the View.
- Reference its Controller.
- Reference `MainController`.
- Reference another Controller.
- Reference another MVCI's Model.
- Manipulate JavaFX controls.
- Perform application navigation.
- Coordinate different MVCIs.

The Interactor's primary responsibility is:

```text
Request
   ↓
Business Logic
   ↓
Model State
```

---

# 15. Interactor Return Values

Normally, an Interactor communicates results by updating its own Model.

For example:

```java
public void refresh() {
    List<ItemDTO> items =
            repository.findAll();

    model.getItems().setAll(items);
}
```

When data must leave the MVCI and be sent to another MVCI through `MainController`, the Interactor may return that data to its Controller.

For synchronous operations:

```java
public ItemDTO getSelectedItem() {
    return model.selectedItemProperty().get();
}
```

The Controller can then pass it to `MainController`.

Do not use Interactor return values merely to bypass the Model for ordinary View updates.

---

# 16. Repositories

Repositories belong below the Interactor.

The normal relationship is:

```text
Controller
    ↓
Interactor
    ↓
Repository
```

and the result:

```text
Repository
    ↓
Interactor
    ↓
Model
    ↓
View
```

Repositories may:

- Query databases.
- Persist data.
- Retrieve records.
- Update records.
- Abstract storage mechanisms.

Repositories must not:

- Manipulate Views.
- Communicate with Controllers.
- Communicate with other MVCIs.
- Perform JavaFX UI operations.

The View never accesses a Repository.

The Controller should not bypass its Interactor to access a Repository.

---

# 17. Threading and Background Work

When background work is required, it belongs in the Interactor.

If the application has an application-wide `ExecutorService`, `MainController` owns it.

Feature Controllers may obtain access to the shared executor through `MainController` and make it available to their own Interactor according to the project's existing initialization pattern.

Do not move background work into the View.

Do not perform long-running repository operations on the JavaFX Application Thread.

When background work changes JavaFX observable Model state, the Model update must occur on the JavaFX Application Thread.

For example:

```java
Platform.runLater(() ->
        model.statusProperty().set(
                "Complete"
        )
);
```

That transition belongs inside the Interactor.

For asynchronous results that must leave the MVCI, use the project's established callback or `CompletableFuture` pattern and complete or consume UI-facing state on the JavaFX Application Thread.

Inspect the existing implementation before choosing an async mechanism.

---

# 18. Model-to-View Communication

The Interactor does not communicate directly with the View.

The normal return path is:

```text
Interactor
    ↓
Model
    ↓
View
```

Ordinary state changes should use JavaFX Properties and observable collections.

Explicit one-time View requests may use a Model message property.

For example:

```java
private final ObjectProperty<ExampleMessage> message =
        new SimpleObjectProperty<>(
                ExampleMessage.NO_REQUEST
        );
```

The Interactor can request:

```java
model.messageProperty()
        .set(ExampleMessage.NO_REQUEST);

model.messageProperty()
        .set(ExampleMessage.REFRESH);
```

The View listens:

```java
model.messageProperty().addListener(
        (obs, oldValue, newValue) -> {

            switch (newValue) {
                case REFRESH ->
                        refresh();

                case NO_REQUEST -> {
                }
            }
        }
);
```

Use an explicit:

```java
NO_REQUEST
```

state rather than `null` as a control signal.

---

# 19. Two Directional Message Paths

MVCI deliberately separates user requests from View updates.

## User Action

```text
User
  ↓
View
  ↓
Action Enum
  ↓
Controller
  ↓
Interactor
  ↓
Model
```

## Screen Update

```text
Interactor
    ↓
Model Property / Message
    ↓
View Listener / Binding
    ↓
JavaFX UI
```

These paths should remain separate.

The Controller should not directly call View-specific update methods.

The Interactor must never directly call the View.

---

# 20. Message Enums

Each MVCI should have its own message/action enum.

For example:

```java
public enum ExampleMessage {
    NO_REQUEST,
    SAVE,
    DELETE,
    REFRESH
}
```

Messages should express explicit intent.

Do not reuse one enum value for unrelated directions or operations merely to reduce the number of enum entries.

A message such as:

```java
NO_REQUEST
```

provides an explicit idle state.

Prefer explicit messages over magic values or `null`.

---

# 21. JavaFX Properties and Binding

Use JavaFX Properties where the state genuinely belongs in the Model.

Bidirectional binding is appropriate when both Model and control may legitimately change a value:

```java
textField.textProperty()
        .bindBidirectional(
                model.valueProperty()
        );
```

One-way binding is appropriate when one side clearly owns the state:

```java
model.selectedItemProperty().bind(
        listView.getSelectionModel()
                .selectedItemProperty()
);
```

Be careful:

A bound property cannot subsequently be manually assigned.

This is invalid while the property is bound:

```java
model.selectedItemProperty().set(item);
```

and produces:

```text
A bound value cannot be set.
```

Before introducing a binding, determine which object owns the state and which direction the value should flow.

---

# 22. CSS

JavaFX CSS is encouraged.

Application CSS should normally live under resources:

```text
src/main/resources/css/
```

Views should apply semantic style classes:

```java
button.getStyleClass()
        .add("primary-button");
```

Prefer stylesheet rules over large inline Java style strings.

The responsibility is:

```text
View
    What is this element?

CSS
    What does it look like?
```

CSS controls presentation.

MVCI controls application responsibility.

---

# 23. Abstract MVCI Base Classes

The project uses shared abstract base classes such as:

```text
ControllerBase
InteractorBase
ViewBase
```

These exist to:

- Reduce repetitive setup.
- Standardize MVCI initialization.
- Enforce predictable relationships.
- Make each feature follow the same structure.

Use the existing base classes.

Do not create parallel replacements without explicit instruction.

Before modifying code that depends on these classes, inspect their actual implementation.

Do not assume:

- Constructor signatures
- Generic bounds
- Initialization order
- Visibility
- Message handling
- `getView()` behavior

from another MVCI implementation.

Use the project's actual framework.

---

# 24. ControllerBase

`ControllerBase` normally standardizes:

```text
Model
  ↓
Interactor
  ↓
View
```

initialization.

Concrete Controllers implement methods analogous to:

```java
protected abstract M createModel();

protected abstract I createInteractor(M model);

protected abstract V createView(M model);
```

and then initialize the MVCI:

```java
initializeMVCI();
```

`getView()` exposes the built JavaFX root `Region`.

MainController may call another Controller's `getView()` because MainController is responsible for launching and coordinating MVCI units.

Do not expose the concrete View simply so other Controllers can call View-specific methods.

---

# 25. ViewBase

`ViewBase` provides the common View relationship:

- Strongly typed Model
- Typed action callback
- Builder implementation
- Model-to-View listener structure

A typical shape is:

```java
public abstract class ViewBase<
        T,
        M,
        MSG>
        implements Builder<T> {

    protected final M model;

    protected final Consumer<MSG> action;

    protected ViewBase(
            M model,
            Consumer<MSG> action) {

        this.model = model;
        this.action = action;
    }

    protected abstract void viewListener();
}
```

Concrete Views build their own JavaFX interface.

---

# 26. InteractorBase

`InteractorBase` owns the strongly typed Model reference:

```java
public abstract class InteractorBase<M> {

    protected final M model;

    protected InteractorBase(M model) {
        this.model = model;
    }
}
```

The concrete Interactor adds the business operations required by its MVCI.

---

# 27. Launching Another MVCI

MVCIs are launched by `MainController`.

A feature Controller does not create another feature Controller directly.

Use:

```java
private void openExampleMVCI() {
    exampleController =
            new ExampleController(this);

    interactor.requestOpen(
            exampleController.getView()
    );
}
```

If the new MVCI requires initial information:

1. Construct the Controller with **only `MainController`**.
2. Pass the required information through a controlled Controller method.
3. Let that Controller delegate initialization to its Interactor.
4. Let its Interactor update its Model.
5. Build/display the View according to the established Main MVCI flow.

For example:

```java
ExampleController controller =
        new ExampleController(this);

controller.setInitialData(data);

Region content =
        controller.getView();
```

Do not add `data` to the Controller constructor.

---

# 28. Application-Level UI Changes

`MainController` coordinates application-level navigation but should avoid manipulating `MainView` directly.

If Main needs to open a tab, replace content, or otherwise alter application-level UI:

```text
MainController
    ↓
MainInteractor
    ↓
MainModel
    ↓
MainView
```

For example, `MainController` may pass the new feature `Region` to `MainInteractor`.

`MainInteractor` stores the necessary request information in `MainModel` and sets an appropriate View message.

`MainView` observes that message and performs the JavaFX presentation operation.

This preserves:

```text
Controller
    ↓
Interactor
    ↓
Model
    ↓
View
```

even inside the Main MVCI.

---

# 29. Responsibility Test

Before deciding where code belongs, ask:

| Question | Owner |
|---|---|
| What controls exist? | View |
| How are controls arranged? | View |
| How does the UI look? | View / CSS |
| What did the user request? | View → Controller |
| Where should the request go? | Controller |
| What work must happen? | Interactor |
| Where is observable state stored? | Model |
| Who queries or persists data? | Repository through Interactor |
| Who coordinates two MVCIs? | MainController |
| How does an MVCI communicate outside itself? | Controller → MainController |
| How does the View learn state changed? | Model properties/messages |
| Where does background work happen? | Interactor |
| Who owns application-wide infrastructure? | MainController |

---

# 30. When Modifying Existing Code

Before proposing a change:

1. Inspect the existing class.
2. Identify which MVCI owns the behavior.
3. Determine which component owns the responsibility.
4. Inspect the relevant base class when framework behavior matters.
5. Preserve existing MVCI boundaries.
6. Preserve existing working functionality.
7. Make one clearly scoped change at a time.
8. Do not invent missing APIs.
9. Do not introduce FXML.
10. Do not bypass the Interactor.
11. Do not bypass MainController for cross-MVCI communication.
12. Do not add another parameter to a feature Controller constructor.
13. Do not move business logic into a Controller.
14. Do not allow an Interactor to reference a View or Controller.

If the required implementation detail is unknown, request or inspect the relevant class rather than guessing.

---

# 31. Architecture Check Before Producing Code

Before generating code, verify:

- Is UI construction in the View?
- Is UI state in the Model?
- Is business logic in the Interactor?
- Is the Controller coordinating rather than performing work?
- Is Repository access going through the Interactor?
- Does every feature Controller take only `MainController`?
- Does `MainController` take no constructor parameters?
- Does all communication leaving a feature MVCI go through `MainController`?
- Is any feature MVCI directly reaching into another MVCI?
- Am I accidentally introducing a callback or event bus to avoid MainController?
- Is the Interactor referencing only its own Model and allowed services/repositories?
- Is an existing JavaFX property already bound?
- Which side owns that binding?
- Am I preserving the existing abstract base classes?
- Am I avoiding FXML?
- Am I preserving unrelated functionality?
- Am I making the smallest architectural change required?

If the proposed solution violates any of these rules, redesign it before presenting it.

---

# 32. Most Important Rules

If nothing else is remembered, preserve these rules:

1. **Each MVCI has a Model, View, Controller, Interactor, and its own message enum.**

2. **The Model holds state and contains no business logic.**

3. **The View builds the JavaFX UI in Java and talks to its Controller through the action enum callback.**

4. **FXML is not used and should not be introduced.**

5. **The Controller coordinates. It does not perform business logic.**

6. **The Interactor performs application/business logic and updates its own Model.**

7. **Repositories are accessed through the Interactor.**

8. **The Interactor never references a View or Controller.**

9. **MainController is the application hub.**

10. **MainController takes no constructor parameters.**

11. **Every other Controller takes MainController as its only constructor parameter.**

12. **All communication leaving a feature MVCI goes through MainController.**

13. **Feature MVCIs never directly communicate with one another.**

14. **Normal screen updates flow Interactor → Model → View.**

15. **User requests flow View → Controller → Interactor.**

16. **Application-level UI changes flow MainController → MainInteractor → MainModel → MainView.**

17. **Do not optimize away explicit MVCI boundaries merely because a shortcut requires less code.**

---

# 33. Final Instruction to the AI

Treat these architectural rules as constraints of the existing system, not suggestions.

When asked to implement a feature:

1. Determine which MVCI owns the feature.
2. Determine which component owns each responsibility.
3. Follow the established message and state flow.
4. Route any communication leaving that MVCI through `MainController`.
5. Preserve the existing abstract framework.
6. Inspect existing code when project-specific details matter.
7. Implement the smallest correct change.

Do not "simplify" the system into another JavaFX architecture.

Do not introduce additional dependencies into feature Controller constructors.

Do not bypass the Interactor.

Do not allow sibling MVCIs to communicate directly.

Do not introduce FXML.

The objective is not merely to make the requested feature work.

The objective is:

> **Make the feature work while preserving consistent separation of concerns, so the application can continue to grow in complexity without becoming harder to understand.**