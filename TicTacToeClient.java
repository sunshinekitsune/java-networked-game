import java.io.*;
import java.net.*;
import java.util.*;

static final int PORT = 5000;
Socket socket;
BufferedReader in;
PrintWriter out;
final Scanner scanner = new Scanner(System.in);
final char[] board = "         ".toCharArray();
char myMark = ' ', oppMark = ' ';
int pendingMove = -1;

void main(String[] args) {
    String host = args.length > 0 ? args[0] : "localhost";
    System.out.println("Connecting to server: " + host + ":" + PORT + "...");
    try {
        connect(host);
        play();
    } catch (IOException e) {
        System.err.println("Could not connect to " + host + ":" + PORT);
        System.err.println("Start TicTacToeServer.java first then run the client.");
    }
}

void connect(String host) throws IOException {
    socket = new Socket(host, PORT);
    in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    out = new PrintWriter(socket.getOutputStream(), true);
}

void play() {
    try {
        String res;
        while ((res = in.readLine()) != null) {
            if (res.startsWith("WELCOME ")) {
                myMark = res.charAt(8);
                oppMark = (myMark == 'X') ? 'O' : 'X';
                System.out.println("Connected to server. You are Player " + myMark + ".");
            } else if (res.startsWith("MESSAGE?")) {
                System.out.println(res.substring(8));
                promptMove();
            } else if (res.startsWith("MESSAGE ")) {
                System.out.println(res.substring(8));
            } else if (res.equals("YOUR_TURN")) {
                printBoard();
                promptMove();
            } else if (res.equals("VALID_MOVE")) {
                if (pendingMove >= 0) board[pendingMove] = myMark;
                printBoard();
            } else if (res.startsWith("OPPONENT_MOVED ")) {
                int loc = Integer.parseInt(res.substring(15).trim());
                board[loc] = oppMark;
                System.out.println("Opponent marked square " + loc + ".");
            } else if (res.equals("WAIT")) {
                System.out.println("Waiting for opponent's move...");
            } else if (res.equals("VICTORY")) {
                System.out.println("Congrats, you won!");
                break;
            } else if (res.equals("DEFEAT") || res.equals("TIE")) {
                printBoard();
                System.out.println(
                        res.equals("DEFEAT") ? "Game over, you lost." : "Game over. It's a tie!");
                break;
            } else if (res.equals("OTHER_PLAYER_LEFT")) {
                System.out.println("\nOpponent left the game. Match ended.");
                break;
            }
        }
    } catch (IOException e) {
        System.out.println("Connection closed: " + e.getMessage());
    } finally {
        try {
            if (socket != null) socket.close();
        } catch (IOException ignored) {
        }
    }
}

void promptMove() {
    while (true) {
        System.out.print("Your turn, " + myMark + ". Enter square 0 through 8 or QUIT: ");
        String input;
        try {
            input = scanner.nextLine().trim();
        } catch (NoSuchElementException e) {
            return;
        }

        if (input.equalsIgnoreCase("QUIT")) {
            out.println("QUIT");
            System.out.println("Leaving the game.");
            return;
        }
        try {
            int loc = Integer.parseInt(input);
            if (loc < 0 || loc > 8) System.out.println("Number must be between 0 and 8.");
            else if (board[loc] != ' ')
                System.out.println("Square " + loc + " is already taken. Pick an open square.");
            else {
                pendingMove = loc;
                out.println("MOVE " + loc);
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Enter a number between 0 and 8 or QUIT.");
        }
    }
}

void printBoard() {
    System.out.println();
    for (int r = 0; r < 3; r++) {
        int b = r * 3;
        System.out.printf(" %c | %c | %c%n", cell(b), cell(b + 1), cell(b + 2));
        if (r < 2) System.out.println("---+---+---");
    }
    System.out.println();
}

char cell(int i) {
    return board[i] == ' ' ? (char) ('0' + i) : board[i];
}
