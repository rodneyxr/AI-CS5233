package gomoku;
import java.util.*;
import java.io.*;

public class Interact2 {
    // This sets up an interaction between an "environment" and
    // and two "agents". Each agent gets an input stream (Scanner) and
    // and output stream (PrintStream) to talk to the environment,
    // which also gets a Scanner and a Printstream.
    //
    // This main method also monitors the messages being sent
    // between the environment and agents. The environment and
    // agents should each print "quit" when finished.
    public static void main(String[] args) {
        // set this to true to see a lot of output
        boolean debug = true;

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
        Scanner agent1In = new Scanner(pipein);
        PrintStream printToAgent1 = new PrintStream(pipeout, true);
        pipeout = new PipedOutputStream();
        try {
            pipein = new PipedInputStream(pipeout);
        } catch (Exception e) {
            throw new RuntimeException("pipe failed " + e);
        }
        InputStreamReader readFromAgent1 = new InputStreamReader(pipein);
        PrintStream agent1Out = new PrintStream(pipeout, true);

        pipeout = new PipedOutputStream();
        try {
            pipein = new PipedInputStream(pipeout);
        } catch (Exception e) {
            throw new RuntimeException("pipe failed " + e);
        }
        Scanner agent2In = new Scanner(pipein);
        PrintStream printToAgent2 = new PrintStream(pipeout, true);
        pipeout = new PipedOutputStream();
        try {
            pipein = new PipedInputStream(pipeout);
        } catch (Exception e) {
            throw new RuntimeException("pipe failed " + e);
        }
        InputStreamReader readFromAgent2 = new InputStreamReader(pipein);
        PrintStream agent2Out = new PrintStream(pipeout, true);

        // These are the only three lines that need to be
        // changed for a different environment and two agents
        Runnable envir = new Gomoku(envirIn, envirOut);
        
        Runnable agent1 = new GomokuPlayer(agent1In, agent1Out);
        // Runnable agent1 = new HumanPlayer(agent1In, agent1Out);
        // Runnable agent1 = new RodneyPlayer(agent1In, agent1Out);
        
        // Runnable agent2 = new GomokuPlayer(agent2In, agent2Out);
        // Runnable agent2 = new HumanPlayer(agent2In, agent2Out);
        Runnable agent2 = new RodneyPlayer(agent2In, agent2Out);

        Thread ethread = new Thread(envir);
        Thread a1thread = new Thread(agent1);
        Thread a2thread = new Thread(agent2);
        long start = System.currentTimeMillis();
        ethread.start();
        a1thread.start();
        a2thread.start();

        boolean alive0 = true;
        boolean alive1 = true;
        boolean alive2 = true;
        StringBuffer envirBuffer = new StringBuffer();
        StringBuffer agent1Buffer = new StringBuffer();
        StringBuffer agent2Buffer = new StringBuffer();

        String line = "you are agent 1";
        if (debug)
            System.out.println(agent1.getClass() + " " + line);
        printToAgent1.print(line);
        printToAgent1.write(10);
        line = "you are agent 2";
        if (debug)
            System.out.println(agent2.getClass() + " " + line);
        printToAgent2.print(line);
        printToAgent2.write(10);

        while (alive0 || alive1 || alive2) {
            line = null;
            boolean wait = true;
            if (alive0) {
                try {
                    line = readLineNoBlock(readFromEnvir, envirBuffer);
                } catch (Exception e) {
                    System.out.println(e);
                    line = null;
                }
                if (line == null) {
                    alive0 = ethread.isAlive();
                    if (! alive0) {
                        System.out.println("Gomoku has finished");
                    }
                } else {
                    wait = false;
                    if (debug)
                        System.out.println(envir.getClass() + " " + line);
                    if (line.startsWith("1 ")) {
                        printToAgent1.print(line.substring(2));
                        printToAgent1.print('\n');
                    } else if (line.startsWith("2 ")) {
                        printToAgent2.print(line.substring(2));
                        printToAgent2.print('\n');
                    }
                }
            } 
            if (alive1) {
                try {
                    line = readLineNoBlock(readFromAgent1, agent1Buffer);
                } catch (Exception e) {
                    line = null;
                }
                if (line == null) {
                    alive1 = a1thread.isAlive();
                    if (! alive1) {
                        System.out.println("Agent 1 has finished");
                    }
                } else {
                    wait = false;
                    if (debug)
                        System.out.println(agent1.getClass() + " " + line);
                    printToEnvir.print("1 " + line);
                    printToEnvir.write(10);
                }
            }
            if (alive2) {
                try {
                    line = readLineNoBlock(readFromAgent2, agent2Buffer);
                } catch (Exception e) {
                    line = null;
                }
                if (line == null) {
                    alive2 = a2thread.isAlive();
                    if (! alive2) {
                        System.out.println("Agent 2 has finished");
                    }
                } else {
                    wait = false;
                    if (debug)
                        System.out.println(agent2.getClass() + " " + line);
                    printToEnvir.print("2 " + line);
                    printToEnvir.write(10);
                }
            }
            if (wait) {
                try {
                    Thread.sleep(10);
                } catch (Exception e) {
                }
            }
        }
        System.out.printf("Total time: %.2f seconds\n",
        		(System.currentTimeMillis() - start) / 1000.0);
        envirIn.close();
        envirOut.close();
        try {
            readFromEnvir.close();
        } catch (IOException e) {
            // Who cares? We're quitting anyway
        }
        printToEnvir.close();
        agent1In.close();
        agent1Out.close();
        try {
            readFromAgent1.close();
        } catch (IOException e) {
            // Who cares? We're quitting anyway
        }
        printToAgent1.close();
        agent2In.close();
        agent2Out.close();
        try {
            readFromAgent2.close();
        } catch (IOException e) {
            // Who cares? We're quitting anyway
        }
        printToAgent2.close();
    }

    public static String readLineNoBlock(InputStreamReader in, StringBuffer buf)
            throws IOException {
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
