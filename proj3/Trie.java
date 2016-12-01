import java.util.Comparator;
/**
 * Prefix-Trie. Supports linear time find() and insert(). 
 * Should support determining whether a word is a full word in the 
 * Trie or a prefix.
 * @author Donald Franks
 *
 * insert methods: Josh Hug
 */
public class Trie {

    private Node root;
    private String alphabet = "abcdefghijklmnopqrstuvwxyz";
    private int N;

    /**
     * Initializes the Trie Class
     */

    public Trie() {
        this(null);
    }

    /**
     * Initializes the Trie Class with a comparator
     *
     * @param c - comparator used for Node creation
     */

    public Trie(Comparator c) {
        root = new Node('\0', c);
        N = 1;
    }

    /**
     * Initializes the Trie Class with a compatator and alphabet
     *
     * @param c - comparator used for Node sorting
     * @param alpha - alphabet to use for sorting
     */

    public Trie(Comparator c, String alpha) {
        this(c);
        alphabet = alpha;
    }

    /**
     * Returns root of the Trie
     *
     * @return Node
     */

    public Node getRoot() {
        return root;
    }

    /**
     * Returns the size of the Trie
     *
     * @return int
     */

    public int size() {
        return N;
    }

    /**
     * Checks to see if S is inside of Trie
     *
     * @param s - word to search for
     * @param isFullWord - whether or not marked is relevent
     * @return boolean
     */

    public boolean find(String s, boolean isFullWord) {        
        return find(root, s, isFullWord, 0);
    }

    /**
     * Resursively navigates through Trie to find S
     * 
     * @param n - node to search for branch
     * @param s - word to search for
     * @param fw - whether or not marked is relevent
     * @param d - index of current character in S
     * @return boolean
     */

    private boolean find(Node n, String s, boolean fw, int d) {
        if (n == null) {
            return false;
        }

        if (d == s.length()) {
            return (fw && n.exists()) || !fw;
        }

        char ch = s.charAt(d);
        if (!n.containsKey(ch)) {
            return false;
        }

        return find(n.get(ch), s, fw, d + 1);
    }

    /**
     * Overloading the insert method with a simple string
     * 
     * @param s - word to add to Trie
     */

    public void insert(String s) {
        insert(s, null);
    }

    /**
     * Overloading the insert method with a string and comparator
     *
     * @param s - string to insert into Trie
     * @param c - comparator to compare letters
     */

    public void insert(String s, Comparator c) {
        if (s == null || s.isEmpty()) {
            throw new IllegalArgumentException();
        }
        for (int i = 0, l = s.length(); i < l; i++) {
            if (alphabet.indexOf(s.charAt(i)) == -1) {
                return;
            }
        }
        insert(root, s, 0, c);    
    }

    /**
     * Resursively navigates through Trie to find S
     * 
     * @param x - node upon which S should be inserted
     * @param s - word to insert
     * @param d - index of current character in S
     * @param c - comparator for Node
     * @return boolean
     */

    private Node insert(Node x, String s, int d, Comparator c) {
        if (x == null) {
            char ch = d == s.length() ? '\0' : s.charAt(d);
            x = new Node(ch, c);
            N += 1;
        }

        if (d == s.length()) {
            x.doesExist();
            return x;
        }

        char ch = s.charAt(d);
        x.put(ch, insert(x.get(ch), s, d + 1, c));
        return x;
    }
}
