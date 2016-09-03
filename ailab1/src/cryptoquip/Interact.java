package cryptoquip;
import java.util.*;
import java.util.concurrent.*;
import java.io.*;

public class Interact {
	public static void main(String[] args) {
		PipedOutputStream pipeout = new PipedOutputStream();
		PipedInputStream pipein;
		try {
			pipein = new PipedInputStream(pipeout);
		} catch (Exception e) {
			throw new RuntimeException("pipe failed " + e);
		}
		Scanner envirIn = new Scanner(pipein);
		PrintStream printToEnvir = new PrintStream(pipeout, true);
		pipeout = new PipedOutputStream();
		try {
			pipein = new PipedInputStream(pipeout);
		} catch (Exception e) {
			throw new RuntimeException("pipe failed " + e);
		}
		InputStreamReader readFromEnvir = new InputStreamReader(pipein);
		PrintStream envirOut = new PrintStream(pipeout, true);

		pipeout = new PipedOutputStream();
		try {
			pipein = new PipedInputStream(pipeout);
		} catch (Exception e) {
			throw new RuntimeException("pipe failed " + e);
		}
		Scanner solverIn = new Scanner(pipein);
		PrintStream printToSolver = new PrintStream(pipeout, true);
		pipeout = new PipedOutputStream();
		try {
			pipein = new PipedInputStream(pipeout);
		} catch (Exception e) {
			throw new RuntimeException("pipe failed " + e);
		}
		InputStreamReader readFromSolver = new InputStreamReader(pipein);
		PrintStream solverOut = new PrintStream(pipeout, true);

		Runnable asker = new CryptoquipEnvironment(envirIn, envirOut);
		Runnable solver = new CryptoquipSolver(solverIn, solverOut);

		Thread envir = new Thread(asker);
		Thread agent = new Thread(solver);
		envir.start();
		agent.start();

		boolean open1 = true;
		boolean open2 = true;
		StringBuffer envirBuffer = new StringBuffer();
		StringBuffer solverBuffer = new StringBuffer();
		while (open1 || open2) {
			String line = null;
			boolean wait = true;
			if (open1) {
				try {
					line = readLineNoBlock(readFromEnvir, envirBuffer);
				} catch (Exception e) {
					System.out.println(e);
					line = null;
				}
				if (line == null || line.length() == 0) {
					open1 = envir.isAlive();
				} else {
					wait = false;
					System.out.println(asker.getClass() + " " + line);
					/*
					if (line.equals("quit")) {
						open1 = false;
					} else {
						printToSolver.print(line);
						printToSolver.print('\n');
					}
					*/
					printToSolver.print(line);
					printToSolver.print('\n');
				}
			}
			if (open2) {
				wait = false;
				try {
					line = readLineNoBlock(readFromSolver, solverBuffer);
				} catch (Exception e) {
					line = null;
				}
				if (line == null || line.length() == 0) {
					open2 = agent.isAlive();
				} else {
					System.out.println(solver.getClass() + " " + line);
					/*
					if (line.equals("quit")) {
						open2 = false;
					} else {
						printToEnvir.print(line);
						printToEnvir.print("\n");
					}
					*/
					printToEnvir.print(line);
					printToEnvir.print("\n");
				}
			}
			if (wait) {
				try {
					Thread.sleep(1);
				} catch (Exception e) {
				}
			}
		}
	}

	public static String readLineNoBlock(InputStreamReader in, StringBuffer buf) throws IOException {
		while (in.ready()) {
			char c = (char) in.read();
			if (c == '\n') {
				String s = buf.toString();
				buf.delete(0, buf.length());
				return s;
			}
			buf.append(c);
		}
		return null;
	}
}
