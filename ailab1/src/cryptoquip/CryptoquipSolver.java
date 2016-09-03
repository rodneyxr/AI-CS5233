package cryptoquip;

import java.util.*;
import java.io.*;

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
		int[] plainToCipherMapping = new int[27]; // index 0 will be ignored
		char[] hint = quip.getHint();
		cipherToPlainMapping[hint[0] % 32] = hint[1] % 32;
		plainToCipherMapping[hint[1] % 32] = hint[0] % 32;
		cipherOrder = quip.sortCipherChars();

		// Students need to code the search method
		int[] answer = search();

		if (answer == null) {
			// This is not a good answer, but it's better than nothing. It maps the 
			// most common cipher chars in order to the most common plain chars.
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
		return null;
	}

}
