package farkle;

import java.io.*;
import java.util.*;

public class FarklePlayer implements Runnable {
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
        	for (int i = 0; i < 6; i++) {
        		if (readline.hasNextInt()) {
        			dice[i] = readline.nextInt();     
        		} else {
        			readline.next();  // skip the X
        			dice[i] = -1;
        		}
        	}
        	readline.close();
        	
        	// Set aside 1s and 5s
        	// You will need to figure out how to set aside 3 of a kinds
        	String setAside = "";
        	for (int i = 0; i < 6; i++) {
        		if (dice[i] == 1 || dice[i] == 5) {
        			// dice are numbered from 1 to 6 so avoid off by one errors
        			int d = i + 1;
        			setAside += d + " ";
        		}
        	}
        	out.print(setAside);
        	
        	// Now need to decide whether to bank or not.
        	// This program always decides to bank.
        	boolean banked = true;
        	
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
