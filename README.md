# java-networked-game

A tic-tac-toe game written in Java in TCP sockets.

## Prerequisites

- JDK Version 21 or higher.

## How To Run

1. Compile the Java code: ``javac TicTacToeServer.java TicTacToeClient.java``

2. Start the tic-tac-toe server: ``java TicTacToeServer.java``
    - The server binds to port 5000.

3. Start Player 1 (X) in a new terminal: ``java TicTacToeClient.java``
    - (Optional) To play on separate computers, connect to the computer running the server with their local IP address.

      Example:``java TicTacToeClient.java 192.168.1.50``

4. Start player 2 (O) in a new terminal: ``java TicTacToeClient.java``

## Gameplay

The board is a 3x3 grid numbered from 0 to 8.

```
 0 | 1 | 2
---+---+---
 3 | 4 | 5
---+---+---
 6 | 7 | 8
```

When it is your turn, type a number from 0 to 8 to select which cell you want to mark.

Squares that are empty show the numbers, and are open to mark.

Squares that are either are an X or an O are either you or your opponent's marked squares.

First to get 3 in a row wins.

You can type QUIT to end the game early.

Get 3 in a row to win, if all tiles are captured it is a tie.

[Watch the gameplay video](https://github.com/user-attachments/assets/2e56269d-e741-4090-abf9-e7b66e65833a)
