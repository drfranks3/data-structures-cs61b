import java.util.Comparator;
import java.util.TreeMap;
import java.util.Set;
/**
 * Node class to handle each element in the Trie
 * @author Donald Franks
 *
 * Node Class: Josh Hug
 */
public class Node {

    private boolean exists = false;
    private TreeMap<Character, Node> children;
    private char letter;

    /**
     * Initializes the Node Class with a comparator
     * 
     * @param s - letter at this Node
     * @param c - comparator for Node
     */

    public Node(char s, Comparator c) {
        letter = s;
        children = new TreeMap<Character, Node>(c);
    }

    /**
     * Returns the truth value of the current word existing in the trie.
     * 
     * @return boolean
     */

    public boolean exists() {
        return exists;
    }        

    /**
     * Returns the children of this node
     * 
     * @return TreeMap<Character, Node>
     */

    public TreeMap<Character, Node> children() {
        return children;
    }        

    /**
     * Sets the exists value to true.
     */

    public void doesExist() {
        exists = true;
    }

    /**
     * Gets the letter of this node
     *
     * @return char
     */

    public char letter() {
        return letter;
    }

    /**
     * Gets a specified child
     *
     * @param c - child to get (char)
     * @return Node
     */

    public Node get(char c) {
        return children().get(c);
    }

    /**
     * Gets a specified child
     *
     * @param s - child to get (String)
     * @return Node
     */

    public Node get(String s) {
        return get(s.charAt(0));
    }

    /**
     * Adds a child to children
     *
     * @param c - char to place n
     * @param n - node to place at c
     */

    public void put(char c, Node n) {
        children().put(c, n);
    }

    /**
     * Adds a child to children
     *
     * @param s - string to place n
     * @param n - node to place at s[0]
     */

    public void put(String s, Node n) {
        put(s.charAt(0), n);
    }

    /**
     * Checks if a certain child exists
     *
     * @param c - child to lookup (char)
     * @return boolean
     */

    public boolean containsKey(char c) {
        return children().containsKey(c);
    }

    /**
     * Checks if a certain child exists
     *
     * @param s - child to lookup (String)
     * @return boolean
     */

    public boolean containsKey(String s) {
        return containsKey(s.charAt(0));
    }

    /**
     * Returns the keySet of the children
     *
     * @return Set<Character>
     */

    public Set<Character> keySet() {
        return children().keySet();
    }
}
