import java.util.PriorityQueue;
/**
 * Ternary Search Trie.
 * @author Donald Franks
 *
 * Inspired and Modified From:
 * algs4.cs.princeton.edu/52trie/TST.java.html
 */
public class TST {

    private int N;
    private Node2 root;
    private PriorityQueue<Word> gpq = new PriorityQueue<Word>();

    /**
     * Private Class: Node
     * Stores:
     *  c character,
     *  Node<Value> left, mid, right children
     *  Value val (weight)
     */

    private static class Node2 {
        private char c;
        private Node2 left, mid, right;
        private double val;
    }

    /**
     * Returns the size of the TST
     *
     * @return int
     */

    public int size() {
        return N;
    }

    /**
     * Does this symbol table contain the given key?
     * @param key the key
     * @return boolean
     */
    public boolean contains(String key) {
        return get(key) != 0.0;
    }

    /**
     * Returns the value associated with the given key.
     * @param key the key
     * @return the value associated with the given key if the key is in the symbol table
     *     and <tt>null</tt> if the key is not in the symbol table
     * @throws NullPointerException if <tt>key</tt> is <tt>null</tt>
     */
    public double get(String key) {
        Node2 x = get(root, key, 0);
        if (x == null) {
            return 0.0;
        }
        return x.val;
    }

    /**
     * Private version of get - returns the node associated with a certain key
     *
     * @param x - node we are traversing
     * @param key - the string we are searching for
     * @param d - how far along the string we've gone
     * @return Node2
     */

    private Node2 get(Node2 x, String key, int d) {
        if (key == null) {
            throw new NullPointerException();
        }

        if (x == null) {
            return null;
        }

        char c = key.charAt(d);
        if (c < x.c) {
            return get(x.left, key, d);
        } else if (c > x.c) {
            return get(x.right, key, d);
        } else if (d < key.length() - 1) {
            return get(x.mid, key, d + 1);
        } else {
            return x;
        }
    }

    /**
     * Inserts the key-value pair into the symbol table, overwriting the old value
     * with the new value if the key is already in the symbol table.
     * If the value is <tt>null</tt>, this effectively deletes the key from the symbol table.
     * @param key the key
     * @param val the value
     * @throws NullPointerException if <tt>key</tt> is <tt>null</tt>
     */

    public void put(String key, double val) {
        if (contains(key)) {
            throw new IllegalArgumentException("duplicate input term: " + key);
        }
        gpq.offer(new Word(key, val));
        N++;
        root = put(root, key, val, 0);
    }

    /**
     * Private version of Put - actually places a key/value into the TST
     *
     * @param x - the Node2 we are currently at
     * @param key - the string we wish to insert
     * @param val - the weight of the string
     * @param d - the index of the string (char) we are looking at
     * @return Node2
     */

    private Node2 put(Node2 x, String key, double val, int d) {
        char c = key.charAt(d);
        if (x == null) {
            x = new Node2();
            x.c = c;
        }
        if (c < x.c) {
            x.left  = put(x.left, key, val, d);
        } else if (c > x.c) {
            x.right = put(x.right, key, val, d);
        } else if (d < key.length() - 1) {
            x.mid = put(x.mid, key, val, d + 1);
        } else {
            x.val = val;
        }
        return x;
    }

    /*
     * Returns the string in the symbol table that is the longest prefix of <tt>query</tt>,
     * or <tt>null</tt>, if no such string.
     * @param query the query string
     * @throws NullPointerException if <tt>query</tt> is <tt>null</tt>
     * @return the string in the symbol table that is the longest prefix of <tt>query</tt>,
     *     or <tt>null</tt> if no such string
     
    public String longestPrefixOf(String query) {
        if (query == null || query.length() == 0) return null;
        int length = 0;
        Node<Value> x = root;
        int i = 0;
        while (x != null && i < query.length()) {
            char c = query.charAt(i);
            if      (c < x.c) x = x.left;
            else if (c > x.c) x = x.right;
            else {
                i++;
                if (x.val != null) length = i;
                x = x.mid;
            }
        }
        return query.substring(0, length);
    }

    
     * Returns all keys in the symbol table as an <tt>Iterable</tt>.
     * To iterate over all of the keys in the symbol table named <tt>st</tt>,
     * use the foreach notation: <tt>for (Key key : st.keys())</tt>.
     * @return all keys in the sybol table as an <tt>Iterable</tt>
     *
    public Iterable<String> keys() {
        Queue<String> queue = new Queue<String>();
        collect(root, new StringBuilder(), queue);
        return queue;
    }*/

    /**
     * Returns all of the keys in the set that start with prefix.
     *
     * @param prefix - the prefix of the string
     * @return PriorityQueue<Word>
     */

    public PriorityQueue<Word> keysWithPrefix(String prefix) {
        PriorityQueue<Word> queue = new PriorityQueue<Word>();
        Node2 x = get(root, prefix, 0);
        if (x == null) {
            return queue;
        }
        if (x.val != 0.0) {
            Word w = new Word(prefix, x.val);
            queue.offer(w);
        }
        collect(x.mid, new StringBuilder(prefix), queue);
        return queue;
    }

    /**
     * Prints out the queue when the prefix is empty.
     *
     * @param k - how many strings to print out
     * @return Iterable<String>
     */

    public Iterable<String> emptyPrefix(int k) {
        Queue<String> queue = new Queue<String>();
        PriorityQueue<Word> dpq = new PriorityQueue<Word>(gpq);
        while (k > 0) {
            Word w = dpq.poll();
            if (w == null) {
                return queue;
            }
            queue.enqueue(w.getWord());
            k--;
        }
        return queue;
    }

    /**
     * Converts the PriorityQueue to an Iterable Set of Strings
     * 
     * @param prefix - prefix of the word to search
     * @param k - how many entries to display max
     * @return Iterable<String>
     */

    public Iterable<String> keysWithPrefixSorted(String prefix, int k) {
        PriorityQueue<Word> pq = keysWithPrefix(prefix);
        Queue<String> queue = new Queue<String>();
        while (k > 0) {
            Word w = pq.poll();
            if (w == null) {
                return queue;
            }
            queue.enqueue(w.getWord());
            k--;
        }
        return queue;
    }

    /**
     * Traverses the TST looking for prefix matchups
     *
     * @param x - node to traverse
     * @param prefix - the string we're developing
     * @param queue - the PQ to order by weights
     */

    private void collect(Node2 x, StringBuilder prefix, PriorityQueue<Word> queue) {
        if (x == null) {
            return;
        }
        collect(x.left, prefix, queue);
        if (x.val != 0.0) {
            Word w = new Word(prefix.toString() + x.c, x.val);
            queue.offer(w);
        }
        collect(x.mid, prefix.append(x.c), queue);
        prefix.deleteCharAt(prefix.length() - 1);
        collect(x.right, prefix, queue);
    }


    /*
     * Returns all of the keys in the symbol table that match <tt>pattern</tt>,
     * where . symbol is treated as a wildcard character.
     * @param pattern the pattern
     * @return all of the keys in the symbol table that match <tt>pattern</tt>,
     *     as an iterable, where . is treated as a wildcard character.
     
    public Iterable<String> keysThatMatch(String pattern) {
        Queue<String> queue = new Queue<String>();
        collect(root, new StringBuilder(), 0, pattern, queue);
        return queue;
    }
 
    private void collect(Nodew x, StringBuilder prefix, int i, String pat, Queue<String> queue) {
        if (x == null) return;
        char c = pat.charAt(i);
        if (c == '.' || c < x.c) collect(x.left, prefix, i, pat, queue);
        if (c == '.' || c == x.c) {
            if (i == pat.length() - 1 && x.val != null) queue.enqueue(prefix.toString() + x.c);
            if (i < pat.length() - 1) {
                collect(x.mid, prefix.append(x.c), i+1, pattern, queue);
                prefix.deleteCharAt(prefix.length() - 1);
            }
        }
        if (c == '.' || c > x.c) collect(x.right, prefix, i, pat, queue);
    }*/

}
