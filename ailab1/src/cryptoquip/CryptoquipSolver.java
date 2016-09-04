package cryptoquip;

import java.io.PrintStream;
import java.util.List;
import java.util.Scanner;

public class CryptoquipSolver implements Runnable {
	private Scanner in;
	private PrintStream out;
	private Words words;
	private Cryptoquip quip;
	private List<String> cipherwords;
	public static char[] plainOrder = { 'e', 'a', 's', 't', 'i', 'o', 'n', 'l', 'r', 'u', 'h', 'd', 'c', 'p', 'm', 'g',
			'y', 'f', 'w', 'b', 'q', 'v', 'k', 'x', 'j', 'z' };
	private char[] cipherOrder;
	private int[] cipherToPlainMapping;
	private int[] plainToCipherMapping;

	public CryptoquipSolver(Scanner in, PrintStream out) {
		this.in = in;
		this.out = out;
		words = new Words("lab1-words.txt");
	}

	// method that is run within the agent thread
	public void run() {
		while (true) {
			String ciphertext = in.nextLine();
			if (ciphertext.equals("quit")) {
				break;
			}
			String hint = in.nextLine();
			char hint1 = hint.charAt(0);
			char hint2 = hint.charAt(hint.length() - 1);
			quip = new Cryptoquip(ciphertext, hint1, hint2);
			cipherwords = quip.extractCipherwords();
			int[] mapping = solve();
			for (int i = 0; i < ciphertext.length(); i++) {
				char cipherchar = ciphertext.charAt(i);
				char plainchar = cipherchar;
				if (Character.isLetter(cipherchar)) {
					plainchar = (char) (96 + mapping[cipherchar % 32]);
				}
				out.print(plainchar);
			}

			out.print('\n');
			in.nextLine(); // skip over score and average
		}
		out.print("quit\n");
		out.close();
		in.close();
	}

	/**
	 * @return a solution to the cryptoquip
	 */
	public int[] solve() {
		// maps cipher chars mod 32 to plain chars mod 32
		cipherToPlainMapping = new int[27]; // index 0 will be ignored
		// maps plain chars mod 32 to cipher chars mod 32
		plainToCipherMapping = new int[27]; // index 0 will be ignored
		char[] hint = quip.getHint();
		cipherToPlainMapping[hint[0] % 32] = hint[1] % 32;
		plainToCipherMapping[hint[1] % 32] = hint[0] % 32;
		cipherOrder = quip.sortCipherChars();

		// Students need to code the search method
		int[] answer = search();

		if (answer == null) {
			// This is not a good answer, but it's better than nothing. It maps
			// the most common cipher chars in order to the most common plain
			// chars.
			for (int i = 0; i < cipherOrder.length; i++) {
				cipherToPlainMapping[cipherOrder[i] % 32] = plainOrder[i] % 32;
			}
			return cipherToPlainMapping;
		} else {
			return answer;
		}
	}

	// method for students to code
	public int[] search() {
		int[] answer = search(cipherwords.size());
		if (answer == null) {
			return search(cipherwords.size() - 1);
		}
		return answer;
	}

	/**
	 * This method is the actual implementation of the search algorithm given as
	 * pseudocode on the assignment.
	 * 
	 * @param numberOfWordsToMatch
	 *            The number of words matched that this algorithm will accept as
	 *            an answer.
	 * @return The answer if one is accepted; null otherwise.
	 */
	private int[] search(final int numberOfWordsToMatch) {
		char cipherchoice = 0;

		// Let cipherchoice be a cipher char that has not been assigned
		for (int i = 0; i < cipherOrder.length; i++) {
			if (cipherToPlainMapping[cipherOrder[i] % 32] == 0) {
				cipherchoice = cipherOrder[i];
				break;
			}
		}

		// If all cipher chars have been assigned, return cipherToPlainMapping
		if (cipherchoice == 0) {
			return cipherToPlainMapping;
		}

		// Loop over possible plainchoices (those not assigned)
		for (int i = 0; i < plainOrder.length; i++) {
			char plainchoice = plainOrder[i];
			if (plainToCipherMapping[plainchoice % 32] != 0) {
				continue;
			}

			// Assign the choices to each other
			cipherToPlainMapping[cipherchoice % 32] = plainchoice % 32;
			plainToCipherMapping[plainchoice % 32] = cipherchoice % 32;

			// Use words.match to check every cipherword.
			int numberOfMatchedWords = 0;
			for (String cipherword : cipherwords) {
				boolean matched = words.match(cipherword, cipherToPlainMapping);
				if (matched)
					numberOfMatchedWords++;
			}

			// If all words match
			if (numberOfMatchedWords >= numberOfWordsToMatch) {
				int[] answer = search(numberOfWordsToMatch);
				if (answer != null)
					return answer;
			}

			// Undo the choice assignments
			cipherToPlainMapping[cipherchoice % 32] = 0;
			plainToCipherMapping[plainchoice % 32] = 0;
		}

		// At this point, return null because nothing was successful
		return null;
	}

}
