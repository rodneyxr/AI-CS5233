package gomoku;
import java.io.*;
import java.util.*;

public class Gomoku implements Runnable {

    Scanner in; // for reading from the agents
    PrintStream out; // for printing info to the agents
    boolean debug = true;

    public Gomoku(Scanner in, PrintStream out) {
        this.in = in;
        this.out = out;
    }
    
    /* get ready to execute run again */
    public void refresh(Scanner in, PrintStream out) {
        this.in = in;
        this.out = out;
    }

    public void run() {
        int[][] board = new int[12][12];
        int numEmpty = 100;
        
        // sentinels are placed on rows 0 and 11 and columns 0 and 11
        for (int i = 0; i < 12; i++) {
            board[i][0] = -1;
            board[i][11] = -1;
            board[0][i] = -1;
            board[11][i] = -1;
        }

        int whoseTurn = 1;
        boolean gameOver = false;
        while (!gameOver) {
            String line = in.nextLine();
            String[] tokens = line.split("\\s+");
            if (tokens.length >= 1
                    && (tokens[0].equals("1") || tokens[0].equals("2"))) {
                int agent = Integer.valueOf(tokens[0]);
                
                boolean badMove = (agent != whoseTurn) || (tokens.length != 3)
                        || (tokens[1].length() != 1)
                        || (-1 == "0123456789".indexOf(tokens[1]))
                        || (tokens[2].length() != 1)
                        || (-1 == "0123456789".indexOf(tokens[2]));
                int row = 0, col = 0;
                if (!badMove) {
                    row = Integer.valueOf(tokens[1]);
                    col = Integer.valueOf(tokens[2]);
                    badMove = (board[row+1][col+1] != 0);
                }

                if (badMove) {
                    int opponent = 3 - agent;
                    String result = "win for agent " + opponent;
                    if (agent != whoseTurn) {
                        result += " : agent " + agent + " moved out of turn";
                    } else {
                        result += " : illegal move (" + tokens[1] + " " + tokens[2]
                                + ") by agent " + agent;
                    }
                    out.print("1 " + result);
                    out.print('\n');
                    out.print("2 " + result);
                    out.print('\n');
                    gameOver = true;
                } else {
                	board[row+1][col+1] = agent;
                	numEmpty--;
                    if (debug) 
                        displayBoard(board);
                    if (wonGame(board, agent, row, col)) {
                       out.print("1 win for agent " + agent);
                       out.print('\n');
                       out.print("2 win for agent " + agent);
                       out.print('\n');
                       gameOver = true;
                    } else if (numEmpty == 0) {
                    	out.print("1 draw for agent 1");
                        out.print('\n');
                        out.print("2 draw for agent 2");
                        out.print('\n');
                        gameOver = true;
                    } else {
                        whoseTurn = 3 - whoseTurn;
                        out.print(whoseTurn + " " + row + " " + col);
                        out.print('\n');
                    }
                }

            }
        }
        in.close();
        out.close();
    }

    /*
     * See if the move by player wins the game. Assumes the player just
     * made a row col move.
     */
    public static boolean wonGame(int[][] board, int player, int row, int col) {
    	int[] rdir = {1, 0, 1, 1};
    	int[] cdir = {0, 1, 1, -1};
    	// avoid off by one
    	row++;
    	col++;
    	for (int d = 0; d < 4; d++) {
    		int inLine = 1;
    		for (int i = 1; i <= 4; i++) {
    			int v = board[row + i*rdir[d]][col + i*cdir[d]];
    			if (v == player) inLine++;
    			else break;
    		}
    		for (int i = 1; i <= 4; i++) {
    			int v = board[row - i*rdir[d]][col - i*cdir[d]];
    			if (v == player) inLine++;
    			else break;
    		}
    		if (inLine >= 5) return true;
    	}
    	return false;
    }
    
    public void displayBoard(int[][] board) {
        System.out.println();;
        System.out.println("          0   1   2   3   4   5   6   7   8   9");
        System.out.println("        +---+---+---+---+---+---+---+---+---+---+");
        for (int row = 0; row <= 9; row++) {
            System.out.printf("      %d |", row);
            for (int col = 0; col <= 9; col++) {
                System.out.print(" ");
                if (board[row+1][col+1] == 1) {
                    System.out.print("X");
                } else if (board[row+1][col+1] == 2) {
                    System.out.print("O");
                } else {
                    System.out.print(" ");
                }
                System.out.print(" |");
            }
            System.out.println();
            System.out.println("        +---+---+---+---+---+---+---+---+---+---+");
        }
        System.out.println("          0   1   2   3   4   5   6   7   8   9");
        System.out.println();
    }
}
