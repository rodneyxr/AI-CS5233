package cryptoquip;

import java.util.*;

public class Cryptoquip {
	private String ciphertext;
	private String plaintext;
	private char[] hint;

	/**
	 * Initializing a Cryptoquip object when the solution is unknown.
	 * 
	 * @param ct
	 *            ciphertext
	 * @param hint1
	 *            first part of the hint
	 * @param hint2
	 *            second part of the hint
	 */
	public Cryptoquip(String ct, char hint1, char hint2) {
		hint = new char[2];
		ciphertext = ct;
		hint[0] = hint1;
		hint[1] = hint2;
		plaintext = null;
	}

	/**
	 * Initializing a Cryptoquip object when the solution is known.
	 * 
	 * @param ct
	 *            ciphertext
	 * @param hint1
	 *            first part of the hint
	 * @param hint2
	 *            second part of the hint
	 * @param pt
	 *            plaintext
	 */
	public Cryptoquip(String ct, char hint1, char hint2, String pt) {
		hint = new char[2];
		ciphertext = ct;
		hint[0] = hint1;
		hint[1] = hint2;
		plaintext = pt;
		if (!checkSolution(plaintext)) {
			System.out.println(plaintext);
			System.out.println("cannot be a solution to");
			System.out.println(ciphertext);
			System.out.println(hint[0] + " equals " + hint[1]);
		}
	}

	public String getCiphertext() {
		return ciphertext;
	}

	public String getPlaintext() {
		return plaintext;
	}

	public char[] getHint() {
		return hint;
	}

	/**
	 * Return a score of how good the solution is. It starts from 100 and
	 * decrements for every missed character.
	 * 
	 * @param plaintext
	 * @return A score between 0 and 100
	 */
	public int scoreAnswer(String answer) {
		int score = 100;
		if (answer.length() != plaintext.length()) {
			score -= Math.abs(answer.length() - plaintext.length());
		}
		for (int i = 0; i < Math.min(answer.length(), plaintext.length()); i++) {
			char answerchar = answer.charAt(i);
			char plainchar = plaintext.charAt(i);
			if (Character.isLetter(answerchar) && Character.isLetter(plainchar)) {
				if (answerchar % 32 != plainchar % 32) {
					score--;
				}
			} else if (answerchar != plainchar) {
				score--;
			}
		}
		return Math.max(score, 0);
	}

	/**
	 * Return true if the plaintext can be a solution to the cryptoquip. This
	 * does not check against any list of official words.
	 * 
	 * @param plaintext
	 * @return
	 */
	public boolean checkSolution(String plaintext) {
		if (ciphertext.length() != plaintext.length()) {
			return false;
		}
		int[] mapping1 = new int[27]; // index 0 will be ignored
		int[] mapping2 = new int[27];
		mapping1[hint[0] % 32] = hint[1] % 32;
		mapping2[hint[1] % 32] = hint[0] % 32;
		for (int i = 0; i < ciphertext.length(); i++) {
			char cipherchar = ciphertext.charAt(i);
			char plainchar = plaintext.charAt(i);
			if (Character.isLetter(cipherchar) && Character.isLetter(plainchar)) {
				if (mapping1[cipherchar % 32] == 0 && mapping2[plainchar % 32] == 0) {
					if (cipherchar % 32 == plainchar % 32) {
						System.out.println(cipherchar + " cannot map to " + plainchar);
						return false; // a letter cannot map to itself in these
										// puzzles
					}
					mapping1[cipherchar % 32] = plainchar % 32;
					mapping2[plainchar % 32] = cipherchar % 32;
				} else if (mapping1[cipherchar % 32] != 0 && mapping1[cipherchar % 32] != plainchar % 32) {
					System.out.println(cipherchar + " cannot map to both " + plainchar + " and "
							+ (char) (64 + mapping1[cipherchar % 32]));
					return false;
				} else if (mapping2[plainchar % 32] != cipherchar % 32) {
					System.out.println("both " + cipherchar + " and " + (char) (64 + mapping2[plainchar % 32])
							+ " cannot map to " + plainchar);
					return false;
				}
			} else if (cipherchar != plainchar) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Extract the words from a string. A word here is defined as a string that
	 * consists only of alphabetical characters and apostrophes. For example,
	 * "isn't" is a word, and "all-stars" is not a word.
	 * 
	 * @param text
	 * @return a list of words
	 */
	private List<String> extractWords(String text) {
		String current = "";
		List<String> words = new ArrayList<String>();
		for (int i = 0; i < text.length(); i++) {
			char c = text.charAt(i);
			if (Character.isLetter(c) || c == '\'') {
				current += c;
			} else if (current.length() > 0) {
				words.add(current);
				current = "";
			}
		}
		return words;
	}

	/**
	 * 
	 * @return list of words in the ciphertext
	 */
	public List<String> extractCipherwords() {
		return extractWords(ciphertext);
	}

	/**
	 * 
	 * @return list of words in the plaintext
	 */
	public List<String> extractPlainwords() {
		return extractWords(plaintext);
	}

	/**
	 * Sort cipher chars by frequency in the cipher text. Cipher chars not in
	 * the cipher text are not included
	 * 
	 * @return an array of cipher chars in order from most frequent to less
	 *         frequent
	 */
	public char[] sortCipherChars() {
		int[] counts = new int[27]; // index 0 will be ignored
		for (int i = 0; i < ciphertext.length(); i++) {
			char cipherchar = ciphertext.charAt(i);
			if (Character.isLetter(cipherchar)) {
				counts[cipherchar % 32]++;
			}
		}
		int[] order = new int[27]; // index 0 will be ignored
		int zerocount = 0;
		for (int i = 1; i <= 26; i++) {
			order[i] = i;
			if (counts[i] == 0) {
				zerocount++;
			}
		}

		// bubble sort
		for (int i = 1; i <= 26; i++) {
			for (int j = 1; j < 26; j++) {
				if (counts[order[j]] < counts[order[j + 1]]) {
					int temp = order[j];
					order[j] = order[j + 1];
					order[j + 1] = temp;
				}
			}
		}

		char[] charOrder = new char[26 - zerocount];
		for (int i = 0; i < charOrder.length; i++) {
			charOrder[i] = (char) (order[i + 1] + 64);
		}
		return charOrder;
	}
}
