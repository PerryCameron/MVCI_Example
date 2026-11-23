# JavaFX MVCI Example

A working JavaFX example demonstrating the **MVCI (Model-View-Controller-Interactor)** architectural pattern.

This project accompanies the JavaFX MVCI Guide and is intended to show how multiple independent MVCI units can work together while maintaining strict separation of concerns.

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
2. Add the part to the available parts list.
3. Select a part.
4. Add the selected part to a work note.
5. Continue building the work note with additional parts.
6. Clear the work note.

The application itself is intentionally simple.

The purpose is not the functionality of the Parts and Work Note application. The purpose is to demonstrate how that functionality moves through MVCI.

---

## Requirements

- Java 21
- JavaFX
- Gradle
- A Java IDE such as IntelliJ IDEA

### Recommended JDK

For simplicity, using an OpenJDK distribution that already includes JavaFX is recommended.

This project was developed using **BellSoft Liberica JDK 21 Full**.

The exact distribution is not required. Other Java 21 OpenJDK distributions that include JavaFX may also be used.

Using a JDK with JavaFX bundled avoids requiring a separate JavaFX SDK and keeps the initial project configuration focused on the application rather than JavaFX installation and module-path configuration.

---

## Running the Project

The Gradle Wrapper is included, so a separate Gradle installation is not required.

On Windows:

```powershell
.\gradlew clean build
```

Then:

```powershell
.\gradlew run
```

The application can also be imported into IntelliJ IDEA and run from the `Main` class.

---

# Project Structure

The important parts of the project are: