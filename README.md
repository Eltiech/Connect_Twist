# Connect 4

Our code is a Java-based implementation of the classic game of Connect 4, except with a twist! This game has timed turns and the gameboard dimensions can be customized and changed. This game supports up to 2 players and has an easy-to-interact-with GUI. There are proper win condition checks and some basic error handling capabilities.

Note: This code started with work in the following repos:
* https://github.com/CocoaPuff2/CSS-360-Project--Sailors-Connect4-Twist-/
* https://github.com/SumayaYusuf/Connect-4-Game-

However, as integrating the back-end/logic thread and the BlockingQueue<GameEvent> system required reworking and restructuring of the project, the final result of that work is located in this repository here. Still todo: port earlier tests etc from these repos.

## Table of Contents
- [Features](#features)
- [Requirements](#requirements)
- [Setup](#setup)
- [Testing](#testing)
- [Future Improvements](#future-improvements)

## Features
- Two-player gameplay with turn-based mechanics
- Graphical user interface using Swing
- Timer for each player's turn
- Win condition checking and handling
- Error handling for invalid moves and input validation

## Requirements
- Java 8 or higher
- JUnit 5 for testing

## Setup

1. **Clone the repository:**


2. **Compile the game:**

    This will launch the game window where you can start playing Connect 4.

## Testing

1. **Compile the tests:**


2. **Run the tests:**


## Future Improvements

- **Network support:** Allow multiplayer gameplay over a network by implementing a client-server architecture.
- **More thorough integration testing:** Expand the test suite to cover interactions between different components of the game.
- **Controller support:** Integrate libraries or APIs to allow the game to receive input from external controllers.
- **Expand threading:** Introduce an adapter that listens for incoming messages and translates them into game events, enhancing real-time updates and interactions.
