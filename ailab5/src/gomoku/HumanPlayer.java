package gomoku;
import java.io.*;
import java.util.*;

public class HumanPlayer implements Runnable {
    Scanner in; // for reading from the agent
    PrintStream out; // for printing info to the agent
    Scanner human; // for reading from the keyboard

    // big values for winning and loseing
    public static final int WIN = 1000000;
    public static final int LOSE = -1000000;

    public HumanPlayer(Scanner in, PrintStream out) {
        this.in = in;
        this.out = out;
        human = new Scanner(System.in);
    }

    public void run() {
        int[][] board = new int[12][12];
        // store -1 in positions just off the board
        for (int i = 0; i < 12; i++) {
            board[i][0] = -1;
            board[i][11] = -1;
            board[0][i] = -1;
            board[11][i] = -1;
        }

        int whoseTurn = 1;
        boolean gameOver = false;

        // expecting you are agent X message
        int player = 0, opponent = 0;
        String line = in.nextLine();
        while (line.equals("")) { // finesse Windows issue
            line = in.nextLine();
        }
        String[] tokens = line.split("\\s+");
        if (tokens.length == 4
                && (tokens[3].equals("1") || tokens[3].equals("2"))) {
            if (tokens[3].equals("1")) {
                player = 1; // I am agent 1
                opponent = 2; // My opponent is agent 2
            } else {
                player = 2; // I am agent 2
                opponent = 1; // My opponent is agent 1
            }
        } else {
            in.close();
            out.close();
            throw new RuntimeException("Unexpected initial input: " + line);
        }

        while (!gameOver) {
            if (whoseTurn == player) {
                // for storing the chosen move
                int move = chooseMove(board, player, opponent);
                int row = move / 10;
                int col = move % 10;

                // print and record a move if one was selected
                if (move >= 0) {
                    out.print(row + " " + col);
                    out.print('\n'); // hoping to avoid Windows issue
                    board[row+1][col+1] = player;
                    whoseTurn = opponent;
                }
            }

            line = in.nextLine();
            
            // stop loop if game is over
            if (line.startsWith("win") || line.startsWith("draw")) {
                gameOver = true;
                break;
            }

            // else expecting an opponent's move
            tokens = line.split("\\s+");
            if (tokens.length == 2 && tokens[0].length() == 1
                    && tokens[1].length() == 1
                    && -1 != "0123456789".indexOf(tokens[0])
                    && -1 != "0123456789".indexOf(tokens[1])) {
                int row = Integer.valueOf(tokens[0]);
                int col = Integer.valueOf(tokens[1]);
                board[row+1][col+1] = opponent;
                whoseTurn = player;
            } else {
                in.close();
                out.close();
                throw new RuntimeException("Unexpected input: " + line);
            }
        }
        in.close();
        out.close();
        human.close();
    }

    // Ask the user for a move
    public int chooseMove(int[][] board, int player, int opponent) {
        // for storing the row and column of the chosen move
        int move = -1;

        // check if there are any moves left first
        boolean anyMoves = false;
        for (int r = 1; r <= 10; r++) {
            for (int c = 1; c <= 10; c++) {
            	if (board[r][c] == 0) {
                    anyMoves = true;
                }
            }
        }

        // get a legal move from human
        if (anyMoves) {
            boolean legalMove = false;
            while (!legalMove) {
                System.out.print("Enter a move for player " + player + ": ");
                String line = human.nextLine();
                Scanner scanLine = new Scanner(line);
                if (scanLine.hasNextInt()) {
                    int row = scanLine.nextInt();
                    if (scanLine.hasNextInt()) {
                        int col = scanLine.nextInt();
                        if (! scanLine.hasNext()) {
                            legalMove = (board[row+1][col+1] == 0);
                            move = row * 10 + col;
                        }
                    }
                }
                scanLine.close();
                
                if (! legalMove) {
                    System.out.println("Illegal Move: " + line);
                }
            }
        }

        return move;
    }
}
