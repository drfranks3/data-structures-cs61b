package ngordnet;
import java.util.TreeMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public class YearlyRecord {
    private TreeMap<String, Integer> records = new TreeMap<String, Integer>();
    private TreeMap<String, Integer> ranks = new TreeMap<String, Integer>();
    private boolean needsUpdate = true;

    /** Creates a new empty YearlyRecord. */
    public YearlyRecord() {
    }

    /** Creates a YearlyRecord using the given data. */
    public YearlyRecord(HashMap<String, Integer> otherrecords) {
        records.putAll(otherrecords);
    }

    /** Returns the number of times WORD appeared in this year. */
    public int count(String word) {
        return records.get(word);
    }

    /** Records that WORD occurred COUNT times in this year. */
    public void put(String word, int count) {
        records.put(word, count);
        needsUpdate = true;
    }

    /** Returns the number of words recorded this year. */
    public int size() {
        return records.size();
    }

    /** Returns all words in ascending order of count. */
    public Collection<String> words() {
        ArrayList<String> words = new ArrayList<String>();
        for (String k : records.keySet()) {
            words.add(k);
        }
        Collections.sort(words, new FreqComparator());
        return words;
    }

    /** Returns all counts in ascending order of count. */
    public Collection<Number> counts() {
        ArrayList<Number> counts = new ArrayList<Number>();
        for (String k : words()) {
            counts.add(count(k));
        }
        return counts;
    }

    /** Returns rank of WORD. Most common word is rank 1. 
      * If two words have the same rank, break ties arbitrarily. 
      * No two words should have the same rank.
      */
    public int rank(String word) {
        if (needsUpdate) {
            update();
            needsUpdate = false;
        }
        return ranks.get(word);
    }

    /** Comparator Example retrieved from YearlyRecordWeird.java */

    /** Comparator that compares strings based on word frequent. */
    private class FreqComparator implements Comparator<String> {
        public int compare(String x, String y) {
            return count(x) - count(y);
        }
    }

    /** Update sthe rank map using sorting. */
    private void update() {
        ranks = new TreeMap<String, Integer>();
        /** The slow approach:
          * For every key, compare against all other keys, and count zs.
          * O(N^2) -- slow compared to sorting. */

        /* Better approach: Sort the items! */
        /* After sorting: we get 'peel', 'zebra', 'zebras', 'zzzzzz'
        */

        String[] words = new String[size()];
        int cnt = size() - 1;
        for (String word : words()) {
            words[cnt] = word;
            cnt -= 1;
        } // sorted in reverse by max number

        /* This is specific to this weird problem I've made up, not the
         * project. */

        for (int i = 0; i < words.length; i += 1) {
            ranks.put(words[i], i + 1);
        }

    }
}
