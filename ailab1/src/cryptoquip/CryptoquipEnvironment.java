package cryptoquip;
import java.util.*;
import java.io.*;

/**
 * This is a thread for delivering Cryptoquip puzzles to a solver and then checking
 * for correct answers.
 * 
 * @author Tom Bylander
 */
public class CryptoquipEnvironment implements Runnable {
	Scanner in;
	PrintStream out;
	ArrayList<Cryptoquip> quips;

	/**
	 * Constructor for this environment.
	 * 
	 * @param in
	 *            For nexting responses from a solver
	 * @param out
	 *            For sending information to a solver
	 */
	public CryptoquipEnvironment(Scanner in, PrintStream out) {
		this.in = in;
		this.out = out;
		quips = new ArrayList<Cryptoquip>();
		try {
			Scanner scan = new Scanner(new File("cryptoquips.txt"));
			while (scan.hasNext()) {
				String ciphertext = scan.nextLine();
				String plaintext = scan.nextLine();
				String hint = scan.nextLine();
				char hint1 = hint.charAt(0);
				char hint2 = hint.charAt(hint.length() - 1);
				Cryptoquip quip = new Cryptoquip(ciphertext, hint1, hint2, plaintext);
				quips.add(quip);
			}
			scan.close();
		} catch (Exception e) {
			throw new RuntimeException("failed to open quips.txt");
		}
	}

	public void run() {
		double sum = 0;
		int count = 0;
		for (Cryptoquip quip : quips) {
			out.print(quip.getCiphertext());
			out.print('\n');
			char[] hint = quip.getHint();
			out.print(hint[0] + " equals " + hint[1]);
			out.print('\n');
			String agentsAnswer = in.nextLine();
			int score = quip.scoreAnswer(agentsAnswer);
			sum += score;
			count++;
			out.print("Score " + score);
			out.printf(" Average %.2f", sum / count);
			out.print('\n');
		}
		// this is supposed to stop the solver
		out.print("quit");
		out.print('\n');
		out.close();
		in.close();
	}

	public static void main(String[] args) {
		CryptoquipEnvironment quipAsker = new CryptoquipEnvironment(
				new Scanner(System.in), System.out);
		quipAsker.run();
	}
}
