package farkle;

import java.io.*;
import java.util.*;

public class FarklePlayer implements Runnable {
	// Number of Dice 	Farkle Count 	Sum of Positive Rewards
	// 1 				4 				150
	// 2 				16 				1800
	// 3 				60 				18750
	// 4 				204 			183150
	// 5 				600 			1675800
	// 6 				1440 			14411250
	final int[] positiveRewards = {0, 150, 1800, 18750, 183150, 1675800, 14411250};
	final int[] farkleCount = {0, 4, 16, 60, 204, 600, 1440}; 
	
    Scanner in; // for reading input
    PrintStream out; // for printing output
    int numTurns;  // number of Farkle turns to play
    String line;  // for storing a line read from in
    int[] dice;  // keep track of the dice values

    public FarklePlayer(Scanner in, PrintStream out) {
        this.in = in;
        this.out = out;
        dice = new int[6];
        numTurns = 1000;
    }

    public void run() {
    	// This specifies how many turns to play.
    	// Change to 1000 for final version.
    	out.printf("%d\n", numTurns);
    	
        line = in.nextLine();
        int turn = 1;
        int turnScore = 0;
        while (! line.equals("quit")) {
        	Scanner readline = new Scanner(line);
        	int[] count = new int[7];
        	for (int i = 0; i < 6; i++) {
        		if (readline.hasNextInt()) {
        			dice[i] = readline.nextInt();
        			count[dice[i]]++;
        		} else {
        			readline.next();  // skip the X
        			dice[i] = -1;
        		}
        	}
        	readline.close();
        	
        	// Set aside 1s and 5s
        	// You will need to figure out how to set aside 3 of a kinds
        	String setAside = "";
        	for (int i = 1; i <= 6; i++) {
				if (count[i] >= 3) {
					for (int d = 0, three = 0; d < 6; d++) {
						if (dice[d] == i) {
							setAside += (d + 1) + " ";
							dice[d] = -1;
							count[i] -= 1;
							three++;
						}
						if (three == 3)
							break;
					}
				}
			}
        	
        	for (int i = 0; i < 6; i++) {
        		if (dice[i] == 1 || dice[i] == 5) {
        			// dice are numbered from 1 to 6 so avoid off by one errors
        			int d = i + 1;
        			setAside += d + " ";
        			dice[i] = -1;
        		}
        	}
        	out.print(setAside);
        	int nDice = 6;
        	for (int i = 0; i < 6; i++)
        		if (dice[i] == -1)
        			nDice--;
        	
        	// Now need to decide whether to bank or not.
        	// This program always decides to bank.
        	// You should bank if the expected reward is less than your turn score.
        	int pos = positiveRewards[nDice];
        	int neg = -1 * farkleCount[nDice] * turnScore;
        	int expectedReward = pos + neg;
        	boolean banked = (expectedReward < turnScore);
        	
        	// Finish printing
        	if (banked) out.print("bank");
        	out.print("\n");
        	
        	// Handle feedback including farkles
        	line = in.nextLine();
        	readline = new Scanner(line);
        	readline.next();  // skip 'score'
        	turnScore = readline.nextInt();  // this is the score so far for this turn
        	String status = readline.next();
        	readline.close();
        	
        	if (status.equals("banked")) {
        		turn++;
        		turnScore = 0;
        	} else if (status.equals("farkled")) {
        		turn++;
        		turnScore = 0;
        	} else if (status.equals("continue")) {
        		// nothing to do here
        	}
        	
        	// get the next dice values
        	line = in.nextLine();
        }

        out.print("quit\n");
        in.close();
        out.close();
    }
}
