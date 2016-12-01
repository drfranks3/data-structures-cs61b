import java.util.Scanner;
/**
 * AlphabetSort. Sorts a list of words into alphabetical order,
 * according to a given permutation of some alphabet. It takes input from
 * stdin and prints to stdout.
 * @author Donald Franks
 *
 */
public class AlphabetSort {

    private static final int MAX = 255;
    private static final int WORD_MAX = 50;

    /**
     * Takes an input, sorts based on an alphabet, and prints to stdout.
     *
     * @param args - arguments passed to main
     */

    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        Trie trie;
        String alphabet;
        int[] sort;

        if (!scan.hasNextLine()) {
            throw new IllegalArgumentException("No input detected.");
        } else {
            alphabet = scan.nextLine();
            sort = new int[MAX];
        }

        for (int i = 0, l = alphabet.length(); i < l; i++) {
            char c = alphabet.charAt(i);
            if (sort[c] != 0) {
                throw new IllegalArgumentException("Alphabet contains repeat letter.");
            }
            sort[c] = i + 1;
        }

        if (!scan.hasNextLine()) {
            throw new IllegalArgumentException("No words dectected.");
        }

        AlphabetComparator ac = new AlphabetComparator(sort);
        trie = new Trie(ac, alphabet);

        while (scan.hasNextLine()) {
            trie.insert(scan.nextLine(), ac);
        }

        traverse(trie);
    }

    /**
     * Traverse method - takes a tree and traverses from root
     *
     * @param trie - the trie to begin traversal
     */

    private static void traverse(Trie trie) {
        Node root = trie.getRoot();
        print(root, "");
    }

    /**
     * Print Method - prints out Trie 
     *
     * @param n - Node to traverse next
     * @param s - string of word developed
     */

    public static void print(Node n, String s) {
        if (n == null) {
            return; 
        }

        if (n.exists()) {
            System.out.println(s);
        }

        for (Character c : n.keySet()) {
            print(n.get(c), s + c);
        }

    }
}
