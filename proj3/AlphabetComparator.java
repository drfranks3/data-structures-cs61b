import java.util.Comparator;
/**
 * Public Class: AlphabetComparator
 * Compares Nodes using the positions.
 * @author Donald Franks
 */

public class AlphabetComparator implements Comparator<Character> {

    private int[] order;

    /**
     * Constructor for AlphabetSort - takes in order of letters
     * 
     * @param o - array of user-defined alphabet
     */

    public AlphabetComparator(int[] o) {
        order = o;
    }

    /**
     * Compares two nodes with their pos attribute
     *
     * @param c1 - first node to compare
     * @param c2 - node to compare the object to
     * @return int
     */

    public int compare(Character c1, Character c2) {
        return order[c1] - order[c2];
    }
}
