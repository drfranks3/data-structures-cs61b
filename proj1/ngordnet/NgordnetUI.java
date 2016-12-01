package ngordnet;

import edu.princeton.cs.introcs.StdIn;
import edu.princeton.cs.introcs.In;

/** Provides a simple user interface for exploring WordNet and NGram data.
 *  @author Donald Franks
 */
public class NgordnetUI {

    private static int startDate = 0,
                       endDate = 0;

    public static void main(String[] args) {
        In in = new In("./ngordnet/ngordnetui.config");
        System.out.println("Reading ngordnetui.config...");

        String wordFile = in.readString();
        String countFile = in.readString();
        String synsetFile = in.readString();
        String hyponymFile = in.readString();

        NGramMap ngm = new NGramMap(wordFile, countFile);
        WordNet wn = new WordNet(synsetFile, hyponymFile);

        while (true) {
            System.out.print("> ");
            String line = StdIn.readLine();
            String[] rawTokens = line.split(" ");
            String command = rawTokens[0];
            String[] tokens = new String[rawTokens.length - 1];
            System.arraycopy(rawTokens, 1, tokens, 0, rawTokens.length - 1);
            switch (command) {
                case "quit": 
                    return;
                case "help":
                    help(tokens);
                    break;  
                case "range":
                    range(tokens);
                    break;
                case "count":
                    count(ngm, tokens);
                    break;
                case "hyponyms":
                    hyponyms(wn, tokens);
                    break;
                case "history":
                    history(ngm, tokens);
                    break;
                case "hypohist":
                    hypohist(ngm, wn, tokens);
                    break;
                case "wordlength":
                    wordlength(ngm, tokens);
                    break;
                case "zipf":
                    zipf(ngm, tokens);
                    break;
                default:
                    System.out.println("Invalid command.");  
                    break;
            }
        }

    }

    private static void help(String[] tokens) {
        if (tokens.length == 0) {
            In helpIn = new In("ngordnet/help.txt");
            String helpStr = helpIn.readAll();
            System.out.println(helpStr);
        } else {
            System.out.println("Usage - help (takes no arguments).");
        }
    }

    private static void range(String[] tokens) {
        if (tokens.length == 2) {
            try {
                startDate = Integer.parseInt(tokens[0]); 
                endDate = Integer.parseInt(tokens[1]);
                if (!(startDate < endDate)) {
                    System.out.println("Usage - range: [fromYear] [toYear]");
                    System.out.println("fromYear < toYear");
                    startDate = endDate = 0;
                }
            } catch (NumberFormatException e) {
                System.out.println("Both arguments must be numbers.");
            }
        } else {
            System.out.println("Usage: range [fromYear] [toYear]");
            System.out.println("fromYear < toYear");
        }
    }

    private static void count(NGramMap ngm, String[] tokens) {
        if (tokens.length == 2) {
            try {
                String countWord = tokens[0].toLowerCase();
                int year = Integer.parseInt(tokens[1]);
                System.out.println(ngm.countInYear(countWord, year));
            } catch (NumberFormatException e) {
                System.out.println("Second argument must be a number.");
            }
        } else {
            System.out.println("Usage: count [word] [year]");
        }
    }

    private static void hyponyms(WordNet wn, String[] tokens) {
        if (tokens.length == 1) {
            try {
                String hypoWord = tokens[0];
                System.out.println(wn.hyponyms(hypoWord));
            } catch (NullPointerException e) {
                System.out.println("Word not in hyponyms database.");
            }
        } else {
            System.out.println("Usage: hyponyms [word]");
        }
    }

    private static void history(NGramMap ngm, String[] tokens) {
        if (tokens.length == 0) {
            System.out.println("Usage: history [words...]");
        } else if (startDate > 0 && endDate > 0) {
            Plotter.plotAllWords(ngm, tokens, startDate, endDate);
        } else {
            System.out.println("Please specify a range before plotting.");
        }
    }

    private static void hypohist(NGramMap ngm, WordNet wn, String[] tokens) {
        if (tokens.length == 0) {
            System.out.println("Usage: hypohist [words...]");
        } else if (startDate > 0 && endDate > 0) {
            Plotter.plotCategoryWeights(ngm, wn, tokens, startDate, endDate);
        } else {
            System.out.println("Please specify a range before plotting.");
        }
    }

    private static void wordlength(NGramMap ngm, String[] tokens) {
        if (tokens.length == 0) {
            if (startDate > 0 && endDate > 0) {
                Plotter.plotProcessedHistory(ngm, startDate, endDate, new WordLengthProcessor());
            } else {
                System.out.println("Please specify a range before plotting.");
            }
        } else {
            System.out.println("Usage: wordlength (takes no arguments).");
        }
    }

    private static void zipf(NGramMap ngm, String[] tokens) {
        if (tokens.length == 1) {
            try {
                int year = Integer.parseInt(tokens[0]);
                Plotter.plotZipfsLaw(ngm, year);
            } catch (NumberFormatException e) {
                System.out.println("First argument must be a number.");
            }
        } else {
            System.out.println("Usage: zipf [year]");
        }
    }

} 
