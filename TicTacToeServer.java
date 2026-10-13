import java.io.*;
import java.net.*;
import java.util.*;

public class TicTacToeServer {
    private static final int PORT = 5000;

    public static void main(String[] args) throws IOException {
        ServerSocket listener = new ServerSocket(PORT);
        System.out.println("Tic-Tac-Toe Server is running on port " + PORT);

        try {
            while (true) {
                Game game = new Game();
                Player playerX = new Player(listener.accept(), 'X', game);
                Player playerO = new Player(listener.accept(), 'O', game);
                playerX.setOpponent(playerO);
                playerO.setOpponent(playerX);
                game.currentPlayer = playerX;
                playerX.start();
                playerO.start();
            }
        } finally {
            listener.close();
        }
    }

    private static class Game {
        private char[] board = new char[9];
        Player currentPlayer;

        public boolean hasWinner() {
            int[][] wins = {
                    {0,1,2}, {3,4,5}, {6,7,8}, // rows
                    {0,3,6}, {1,4,7}, {2,5,8}, // cols
                    {0,4,8}, {2,4,6}          // diagonals
            };
            for (int[] w : wins) {
                if (board[w[0]] != 0 && board[w[0]] == board[w[1]] && board[w[0]] == board[w[2]]) {
                    return true;
                }
            }
            return false;
        }

        public boolean boardFilled() {
            for (char c : board) if (c == 0) return false;
            return true;
        }

        public synchronized boolean legalMove(int loc, Player player) {
            if (player == currentPlayer && board[loc] == 0) {
                board[loc] = player.mark;
                currentPlayer = player.opponent;
                currentPlayer.otherPlayerMoved(loc);
                return true;
            }
            return false;
        }
    }

    private static class Player extends Thread {
        char mark;
        Player opponent;
        Socket socket;
        BufferedReader input;
        PrintWriter output;
        Game game;

        public Player(Socket socket, char mark, Game game) {
            this.socket = socket;
            this.mark = mark;
            this.game = game;
            try {
                input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                output = new PrintWriter(socket.getOutputStream(), true);
                output.println("WELCOME " + mark);
                output.println("MESSAGE Waiting for opponent to connect");
            } catch (IOException e) {
                System.out.println("Player died: " + e);
            }
        }

        public void setOpponent(Player opponent) {
            this.opponent = opponent;
        }

        public void otherPlayerMoved(int loc) {
            output.println("OPPONENT_MOVED " + loc);
            output.println(game.hasWinner() ? "DEFEAT" : game.boardFilled() ? "TIE" : "YOUR_TURN");
        }

        public void run() {
            try {
                output.println("MESSAGE All players connected");
                if (mark == 'X') {
                    output.println("YOUR_TURN");
                }

                while (true) {
                    String command = input.readLine();
                    if (command == null) {
                        return;
                    }
                    if (command.startsWith("MOVE")) {
                        int loc = Integer.parseInt(command.substring(5));
                        if (game.legalMove(loc, this)) {
                            output.println("VALID_MOVE");
                            output.println(game.hasWinner() ? "VICTORY" : game.boardFilled() ? "TIE" : "WAIT");
                        } else {
                            output.println("MESSAGE?Not your turn or invalid move.");
                        }
                    } else if (command.startsWith("QUIT")) {
                        return;
                    }
                }
            } catch (IOException e) {
                System.out.println("Player disconnected: " + e);
            } finally {
                if (!game.hasWinner() && !game.boardFilled() && opponent != null && opponent.output != null) {
                    opponent.output.println("OTHER_PLAYER_LEFT");
                }
                try { socket.close(); } catch (IOException e) {}
            }
        }
    }
}
