package dicegame;
import java.io.*;
import java.util.*;

public class DiceGameSolver implements Runnable {
	private Scanner in;
	private PrintStream out;
	public static final String[] DIE_VALUES = { "one", "two", "three", "four", "five", "six"};
	private int numGames; // number of games to play

	public DiceGameSolver(Scanner in, PrintStream out) {
		this.in = in;
		this.out = out;
		numGames = 2;  // Change to 1000 for final version
	}

	public void run() {
		// This specifies how many games to play.
		// Change to 1000 for final version.
		out.printf("%d\n", numGames);

		for (int game = 1; game <= numGames; game++) {
			String line = null;
			int evidence1 = 0, evidence2 = 0, evidence3 = 0;
			List<String> guessList = new ArrayList<String>();  // remember previous guesses
			List<Integer> evidenceList = new ArrayList<Integer>();  // remember evidence
			do {
				// Expecting thtree ints on this line.
				line = in.nextLine();
				if (line.contains("quit"))
					break;
				Scanner scan = new Scanner(line);
				if (scan.hasNextInt()) {
					evidence1 = scan.nextInt();
				} else {
					out.printf("quit\n");
					scan.close();
					return;
				}
				if (scan.hasNextInt()) {
					evidence2 = scan.nextInt();
				} else {
					out.printf("quit\n");
					scan.close();
					return;
				}
				if (scan.hasNextInt()) {
					evidence3 = scan.nextInt();
				} else {
					out.printf("quit\n");
					scan.close();
					return;
				}
				scan.close();
				
				// Add evidence to evidenceList
				evidenceList.add(evidence1);
				evidenceList.add(evidence2);
				evidenceList.add(evidence3);
				
				// run Bayesian network to get next guess
				int[] guesses = diceNetwork(evidenceList, guessList);
				int guess1 = guesses[0];
				int guess2 = guesses[1];

				// remember guess and print it out
				guessList.add("" + guess1 + guess2);
				out.printf("%d %d\n", guess1, guess2);

				// Expecting a string on this line (right or wrong)
				line = in.nextLine();
				if (line.contains("quit")) {
					break;
				} 
			} while (line.contains("wrong"));
		}

		out.print("quit\n");
	}
	
	// This returns the next guess.
	// evidenceList is all the evidence bits that have been read
	// guessList indicates previous guesses.
	private int[] diceNetwork(List<Integer> evidenceList, List<String> guessList) {
		BayesianNetwork bn = new BayesianNetwork();
		
		Variable die1 = new Variable("Die1", DIE_VALUES);
		bn.addVariable(die1);
        Factor dieFactor1 = new Factor(die1);
        bn.addFactor(dieFactor1);
        
        dieFactor1.set(1.0 / 6.0, 0);
        dieFactor1.set(1.0 / 6.0, 1);
        dieFactor1.set(1.0 / 6.0, 2);
        dieFactor1.set(1.0 / 6.0, 3);
        dieFactor1.set(1.0 / 6.0, 4);
        dieFactor1.set(1.0 / 6.0, 5);
        
        Variable die2 = new Variable("Die2", DIE_VALUES);
		bn.addVariable(die2);
        Factor dieFactor2 = new Factor(die2);
        bn.addFactor(dieFactor2);
        
        dieFactor2.set(1.0 / 6.0, 0);
        dieFactor2.set(1.0 / 6.0, 1);
        dieFactor2.set(1.0 / 6.0, 2);
        dieFactor2.set(1.0 / 6.0, 3);
        dieFactor2.set(1.0 / 6.0, 4);
        dieFactor2.set(1.0 / 6.0, 5);
        
        // You need to write the code to completely define the Bayesian network.
        // For example, if evidenceList.size() is n, then you will need n
        // evidence Variables and n Factors.
        // You will also need to call bn.observe n times to set the values
        // of the evidence variables.
        //
        // See BayesianNetworkTest.java for examples.
        
        Factor result1 = bn.eliminateVariables(die1);
        Factor result2 = bn.eliminateVariables(die2);
        
        // result1.get(i-1) should be P(die1 = i | evidence)
        int guess1 = -1; // need to add one later
        int guess2 = -1; // need to add one later
        for (int i = 1; i <= 6; i++) {
        	for (int j = 1; j <= 6; j++) {
        		if (! guessList.contains("" + i + j)) {
        			if (guess1 == -1 ||
        				result1.get(i-1) * result2.get(j-1) > 
        				result1.get(guess1-1) * result2.get(guess2-1)) {
        				guess1 = i;
        				guess2 = j;
        			}
        		}
        	}
        }
        	
        int[] guesses = new int[2];
        guesses[0] = guess1;
        guesses[1] = guess2;
		return guesses;
	}
}
