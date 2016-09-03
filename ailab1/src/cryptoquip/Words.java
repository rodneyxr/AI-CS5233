package cryptoquip;
import java.util.*;
import java.nio.file.*;

public class Words {
	// words[l][p][c] should be the words of length l that at position p contains
	// character (letter) c.  Lengths are encoded 1-20.  Positions are encoded 0-19. 
	// Characters (letters) are encoded 1-26.  Just need to % 32 to convert
	// from ASCII or UNICODE.
	private SortedSet<String>[][][] words;

	public Words(String wordfile) {
		Scanner scan = null;
		try {
			scan = new Scanner(Paths.get(wordfile));
		} catch (Exception e) {
			throw new IllegalArgumentException(wordfile + " cannot be read.");
		}
		// Be prepared for words up to 20 characters long.
		// Index 0 is ignored for first and last indexes.
		words = new TreeSet[21][20][27];
		while (scan.hasNext()) {
			String word = scan.next();
			int l = word.length();
			for (int p = 0; p < l; p++) {
				char plainchar = word.charAt(p);
				if (Character.isLetter(plainchar)) {
					int c = plainchar % 32;
					if (null == words[l][p][c]) {
						words[l][p][c] = new TreeSet<String>();
					}
					words[l][p][c].add(word);
				}
			}
		}
		scan.close();
	}
	
	// Is there a word that matches cipherword given the partial cipherToPlainMapping?
	public boolean match(String cipherword, int[] cipherToPlainMapping) {
		int l = cipherword.length();
		SortedSet<String> subwords = null;
		for (int p = 0; p < l; p++) {
			char cipherchar = cipherword.charAt(p);
			if (Character.isLetter(cipherchar) && 0 < cipherToPlainMapping[cipherchar % 32]) {
				int c = cipherToPlainMapping[cipherchar % 32];
				if (null == words[l][p][c]) {
					return false;
				} else if (null == subwords) {
					subwords = words[l][p][c];
				} else {
					SortedSet<String> newsubwords = new TreeSet<String>();
					for (String word : subwords) {
						if (words[l][p][c].contains(word)) {
							newsubwords.add(word);
						}
					}
					if (newsubwords.isEmpty()) {
						return false;
					}
					subwords = newsubwords;
				}
			}
		}
		return true;
	}
}
