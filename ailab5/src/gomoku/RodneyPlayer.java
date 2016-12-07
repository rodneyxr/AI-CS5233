package gomoku;

import java.io.*;
import java.util.*;

public class RodneyPlayer implements Runnable {
	Scanner in; // for reading from the agent
	PrintStream out; // for printing info to the agent
	
	// big values for winning and losing
	public static final int WIN = 1000000;
	public static final int LOSE = -1000000;

	// weights for evaluating a Gomoku position
	public static final int[] PLAYER_WEIGHTS = {0, 1, 2, 6, 10, WIN};
	public static final int[] OPPONENT_WEIGHTS = {0, -1, -3, -5, -10, LOSE};
	
	// directions for wonGame method when checking for five in lines
	public static final int[] RDIR = { 1, 0, 1, 1 };
	public static final int[] CDIR = { 0, 1, 1, -1 };

	public RodneyPlayer(Scanner in, PrintStream out) {
		this.in = in;
		this.out = out;
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
		if (tokens.length == 4 && (tokens[3].equals("1") || tokens[3].equals("2"))) {
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
			// stop loop if game is over
			if (whoseTurn == player) {
				// for storing the chosen move
				// row is the 10s digit
				// col is the 1s digit
				int move = maxNode(board, player, opponent, 3, true);
				int row = move / 10;
				int col = move % 10;

				// print the move
				out.print(row + " " + col);
				out.print('\n');
				// avoid off by one
				board[row + 1][col + 1] = player;
				whoseTurn = opponent;
			} else {
				line = in.nextLine();

				// could be the end of the game
				if (line.startsWith("win") || line.startsWith("draw")) {
					gameOver = true;
					break;
				}

				// else expecting an opponent's move
				tokens = line.split("\\s+");
				if (tokens.length == 2 && tokens[0].length() == 1 && tokens[1].length() == 1
						&& -1 != "0123456789".indexOf(tokens[0]) && -1 != "0123456789".indexOf(tokens[1])) {
					int row = Integer.valueOf(tokens[0]);
					int col = Integer.valueOf(tokens[1]);
					// avoid off by one
					board[row + 1][col + 1] = opponent;
					whoseTurn = player;
				} else {
					in.close();
					out.close();
					throw new RuntimeException("Unexpected input: " + line);
				}
			}
		}
		in.close();
		out.close();
	}
	
    /*
     * See if the move by player wins the game. Assumes the player just
     * made a move to board[r][c].
     * 
     * r is used to index into the board so it is between 1 and 10.
     * Same thing for c.
     */
    public static boolean wonGame(int[][] board, int player, int r, int c) {
    	for (int d = 0; d < 4; d++) {
    		int inLine = 1;
    		for (int i = 1; i <= 4; i++) {
    			int v = board[r + i*RDIR[d]][c + i*CDIR[d]];
    			if (v == player) inLine++;
    			else break;
    		}
    		for (int i = 1; i <= 4; i++) {
    			int v = board[r - i*RDIR[d]][c - i*CDIR[d]];
    			if (v == player) inLine++;
    			else break;
    		}
    		if (inLine >= 5) return true;
    	}
    	return false;
    }

	/*
	 * Max node in a minimax search.
	 * If this is the top level call, returns best move as a two digit number.
	 * Else return evaluation of board via minimax search.
	 * 
     * r is used to index into the board so it is between 1 and 10.
     * Same thing for c.
 	 */
	public int maxNode(int[][] board, int player, int opponent, int depth, boolean topCall) {
		int move = -1;
		int maxEval = 0;
		int eval = 0;
		for (int r = 1; r <= 10; r++) {
			for (int c = 1; c <= 10; c++) {
				if (board[r][c] == 0) {
					board[r][c] = player;
					if (wonGame(board, player, r, c)) {
						eval = WIN + depth;  // prefer shorter paths to a win
					} else if (depth <= 1) {
						eval = gomokuEval(board, player, opponent);
					} else {
						eval = minNode(board, player, opponent, depth - 1);
					}
					if (move == -1 || eval > maxEval) {
						// avoid off by one
						move = (r - 1) * 10 + (c - 1);
						maxEval = eval;
					}

					board[r][c] = 0;
				}
			}
		}
		if (topCall)
			return move;
		return maxEval;
	}

	/*
	 * Min node in a minimax search.
	 * Returns evaluation of board via minimax search.
	 * 
	 * 
     * r is used to index into the board so it is between 1 and 10.
     * Same thing for c.
 	 */
	public int minNode(int[][] board, int player, int opponent, int depth) {
		boolean minEvalNotAssignedYet = true;
		int minEval = 0;
		int eval = 0;
		for (int r = 1; r <= 10; r++) {
			for (int c = 1; c <= 10; c++) {
				if (board[r][c] == 0) {
					board[r][c] = opponent;
					
					if (wonGame(board, opponent, r, c)) {
						eval = LOSE - depth;  // prefer longer paths to a loss
					} else if (depth <= 1) {
						eval = gomokuEval(board, player, opponent);
					} else {
						eval = maxNode(board, player, opponent, depth - 1, false);
					}
					if (minEvalNotAssignedYet || eval < minEval) {
						minEvalNotAssignedYet = false;
						minEval = eval;
					}

					board[r][c] = 0;
				}
			}
		}

		return minEval;
	}

	/*
	 * Evaluate the board.
	 * For each possible five in line, the number of player pieces and
	 * opponent pieces are counted.  If one is zero and the other is nonzero,
	 * then the number of nonzero pieces is used as index into
	 * PLAYER_WEIGHTS or OPPONENT_WEIGHTS as appropriate and that weight
	 * is added to the evaluation.
	 *
     * r is used to index into the board so it is between 1 and 10.
     * Same thing for c.
     */
	public int gomokuEval(int[][] board, int player, int opponent) {
		int eval = 0;
		int[] counts = new int[3];

		// check five in line in a row
		for (int r = 1; r <= 10; r++) {
			counts[0] = 0;
			counts[1] = 0;
			counts[2] = 0;
			for (int c = 1; c <= 10; c++) {
				counts[board[r][c]]++;
				if (c > 4) {
					if (counts[player] > 0 && counts[opponent] == 0) {
						eval += PLAYER_WEIGHTS[counts[player]];
					} else if (counts[opponent] > 0 && counts[player] == 0) {
						eval += OPPONENT_WEIGHTS[counts[opponent]];
					}
					counts[board[r][c-4]]--;
				}
			}
		}

		// check five in line in a column
		for (int c = 1; c <= 10; c++) {
			counts[0] = 0;
			counts[1] = 0;
			counts[2] = 0;
			for (int r = 1; r <= 10; r++) {
				counts[board[r][c]]++;
				if (r > 4) {
					if (counts[player] > 0 && counts[opponent] == 0) {
						eval += PLAYER_WEIGHTS[counts[player]];
					} else if (counts[opponent] > 0 && counts[player] == 0) {
						eval += OPPONENT_WEIGHTS[counts[opponent]];
					}
					counts[board[r-4][c]]--;
				}
			}
		}

		// check five in line in a diagonal upper left to lower right
		for (int i = -5; i <= 5; i++) {
			// might start at r=1 and c between 1 and 6, or c=1 and r between 1 and 6
			int r = (i >= 0) ? 1 : 1 - i;
			int c = (i <= 0) ? 1 : i + 1;

			counts[0] = 0;
			counts[1] = 0;
			counts[2] = 0;
			while (r <= 10 && c <= 10) {
				counts[board[r][c]]++;
				if (r > 4 && c > 4) {
					if (counts[player] > 0 && counts[opponent] == 0) {
						eval += PLAYER_WEIGHTS[counts[player]];
					} else if (counts[opponent] > 0 && counts[player] == 0) {
						eval += OPPONENT_WEIGHTS[counts[opponent]];
					}
					counts[board[r-4][c-4]]--;
				}
				r++;
				c++;
			}
		}

		// check five in line in a diagonal upper right to lower left
		for (int i = -5; i <= 5; i++) {
			// might start at r=1 and c between 5-10, or c=10 and r between 1 and 6
			int r = (i >= 0) ? 1 : 1 - i;
			int c = (i <= 0) ? 10 : 10 - i;

			counts[0] = 0;
			counts[1] = 0;
			counts[2] = 0;
			while (r <= 10 && c >= 1) {
				counts[board[r][c]]++;
				if (r > 4 && c < 7) {
					if (counts[player] > 0 && counts[opponent] == 0) {
						eval += PLAYER_WEIGHTS[counts[player]];
					} else if (counts[opponent] > 0 && counts[player] == 0) {
						eval += OPPONENT_WEIGHTS[counts[opponent]];
					}
					counts[board[r-4][c+4]]--;
				}
				
				r++;
				c--;
			}
		}

		return eval;
	}
}
