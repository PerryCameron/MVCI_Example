# JavaFX MVCI Example Project

This project is a working demonstration of the **MVCI (Model-View-Controller-Interactor)** architectural pattern for JavaFX.

It accompanies the **JavaFX MVCI Guide** and the **MVCI Stub Project**.

Where the Stub Project provides a clean starting point for a new application, this project demonstrates how an MVCI application can grow beyond a single MVCI while maintaining strict separation of concerns.

---

## Purpose

The goal of this project is to demonstrate MVCI through a small, understandable JavaFX application.

The finished example will contain multiple MVCI units that communicate with each other through the application's Main Controller.

The project is intentionally small enough to follow from beginning to end while still demonstrating the architectural patterns needed by a much larger application.

This includes:

- Model-driven JavaFX Views
- View-to-Controller actions
- Controller-to-Interactor requests
- Interactors performing application logic
- Interactors updating Models
- Views reacting to Model state
- Multiple independent MVCI units
- Cross-MVCI communication through the Main Controller
- JavaFX interfaces built entirely in Java without FXML

---

## Requirements

- **Java 21**
- **JavaFX**
- **Gradle**
- A Java IDE such as IntelliJ IDEA

### Recommended JDK

For simplicity, this project is designed to work well with a Java 21 OpenJDK distribution that includes JavaFX.

One such distribution is **BellSoft Liberica JDK Full**.

Using a JDK distribution that already includes JavaFX avoids requiring a separate JavaFX SDK and keeps the project setup focused on the application and its architecture.

Other OpenJDK distributions that include JavaFX may also be used.

---

## Running the Project

The project includes the Gradle Wrapper, so a separate Gradle installation is not required.

On Windows:

```powershell
.\gradlew clean build
```

Then:

```powershell
.\gradlew run
```

The project can also be imported and run directly from IntelliJ IDEA.

---

## Project Architecture

The project begins with a Main MVCI and expands into multiple independent MVCI units.

The general structure is:

```text
com.example
├── Main.java
│
└── mvci
    ├── framework
    │   ├── ControllerBase.java
    │   ├── InteractorBase.java
    │   └── ViewBase.java
    │
    ├── main
    │   ├── MainController.java
    │   ├── MainInteractor.java
    │   ├── MainMessage.java
    │   ├── MainModel.java
    │   └── MainView.java
    │
    ├── part
    │   ├── PartController.java
    │   ├── PartInteractor.java
    │   ├── PartMessage.java
    │   ├── PartModel.java
    │   └── PartView.java
    │
    └── note
        ├── NoteController.java
        ├── NoteInteractor.java
        ├── NoteMessage.java
        ├── NoteModel.java
        └── NoteView.java
```

Each feature remains contained within its own MVCI.

---

## Basic MVCI Flow

A typical operation follows this path:

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

The responsibilities remain separated throughout that flow.

### View

The View builds and manages the JavaFX user interface.

The View:

- Observes its Model
- Updates the interface from Model state
- Reports user actions to its Controller
- Does not contain business logic

Views are written directly in Java.

**FXML is not used by this architecture.**

### Controller

The Controller coordinates its MVCI.

The Controller:

- Owns the MVCI components
- Receives actions from the View
- Delegates application work to the Interactor
- Coordinates communication with other MVCIs when appropriate

The Controller should coordinate work rather than perform the work itself.

### Interactor

The Interactor performs the work requested by the Controller.

The Interactor:

- Contains application and business logic
- Communicates with repositories or services when required
- Updates the Model
- Does not manipulate JavaFX controls

### Model

The Model represents the observable state of the MVCI.

The Model primarily contains JavaFX properties and observable collections.

It does not perform application logic.

---

## Cross-MVCI Communication

One of the primary purposes of this example is to demonstrate communication between independent MVCIs.

A feature MVCI should not reach directly into another feature MVCI.

For example, this is intentionally avoided:

```text
PartInteractor ─────────────► NoteInteractor
```

Instead, communication is coordinated through the Main Controller:

```text
Part MVCI
    │
    ▼
MainController
    │
    ▼
Note MVCI
```

The Main Controller acts as the coordination point between otherwise independent MVCI units.

This prevents individual features from becoming tightly coupled as the application becomes more complex.

---

## Why Multiple MVCIs?

A small application could place everything into one Controller, Model, View, and Interactor.

That approach becomes increasingly difficult to maintain as the application grows.

MVCI instead allows the application to be divided into independent functional units:

```text
Application
│
├── Main MVCI
├── Part MVCI
└── Note MVCI
```

Each unit follows the same architectural rules.

Adding more functionality therefore increases the number of small, predictable components rather than continuously increasing the responsibility of a few large classes.

The goal is **consistent separation of concerns that allows greater complexity without creating greater confusion**.

---

## Relationship to the MVCI Stub Project

The **MVCI Stub Project** is the starting template.

It contains:

- The MVCI framework classes
- A minimal Main MVCI
- Gradle configuration
- A working JavaFX entry point

This Example Project begins from that same foundation and adds working application behavior.

Use the **Stub Project** when starting a new MVCI application.

Use this **Example Project** when you want to understand how the pieces work together.

---

## Relationship to the MVCI Guide

This project is intended to be read alongside the **JavaFX MVCI Guide**.

The Guide explains the architecture and the reasoning behind its boundaries.

This repository demonstrates those rules in working Java code.

When there is uncertainty about where code belongs, the responsibility of the component should determine the answer:

```text
UI construction and presentation     → View
Coordination                          → Controller
Application/business logic            → Interactor
Observable application state          → Model
Cross-MVCI coordination               → Main Controller
```

---

## Project Status

This is an educational example project intended to demonstrate the MVCI architecture rather than serve as a production application.

Its design favors clarity and explicit architectural boundaries over adding unnecessary features.

---

## Companion Resources

This project is part of a three-resource MVCI set:

1. **MVCI Stub Project**  
   A minimal starting point for creating a new JavaFX MVCI application.

2. **MVCI Example Project**  
   This repository. A working demonstration showing multiple MVCIs interacting.

3. **MVCI AI Prompt**  
   A reusable context prompt that explains the architectural rules to an AI coding assistant before it works on an MVCI project.