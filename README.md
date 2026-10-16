# Adivina Quién

A Java implementation of the classic **Guess Who?** game, developed as a project for the Programming III course. It includes a graphical interface built with Java Swing, two game modes, and two AI players with different decision-making strategies.

## Game modes

- **Human vs. Machines:** A human player competes against two AI players. The first player to guess the target character wins.
- **Machine vs. Machine:** Two AI players compete to identify the same randomly selected character. This mode makes it possible to compare their strategies.

## Features

- 23 characters with distinct combinations of attributes.
- Questions based on character attributes such as gender, hair color, baldness, and glasses.
- Two AI strategies: cautious and risky.
- A decision tree to represent the process of narrowing down the possible characters.
- Custom implementations of Merge Sort and Quick Sort.
- Persistent scoreboard saved to a file.
- Console and graphical versions of the game.

## Technologies

- Java
- Java Swing

## How to run

Open the project in a Java IDE and run one of the following entry points:

- `MainGUI.java` to launch the graphical version.
- `Main.java` to launch the console version.

## Project structure

- `Things/`: character model.
- `Interfaz/`: interfaces used by the game.
- `FunctionalThings/`: game logic, AI strategies, filters, sorting algorithms, and persistence.
- `GUI/`: graphical screens and window management.

## Academic project

Created as a Programming III course project to apply object-oriented programming concepts, algorithms, data structures, and design patterns.
