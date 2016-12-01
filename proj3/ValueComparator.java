import java.util.Comparator;
/**
 * Public Class: ValueComparator
 * Compares Nodes using their value.
 * @author Donald Franks
 */

public class ValueComparator implements Comparator<Word> {

    /**
     * Compares two nodes with their pos attribute
     *
     * @param n1 - first node to compare
     * @param n2 - node to compare the object to
     * @return int
     */

    public int compare(Word n1, Word n2) {
        return n1.val() < n2.val() ? -1 : 1;
    }
}
