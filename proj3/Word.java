/**
 * Word class to conveniently store words and their weights.
 * @author Donald Franks
 *
 */
public class Word implements Comparable<Word> {

    private String word;
    private double val;

    /**
     * Constructor for Word
     *
     * @param w - word to insert
     * @param v - value associated with word
     */
    public Word(String w, double v) {
        word = w;
        val = v;
    }

    /**
     * Returns the String of the word
     *
     * @return String
     */

    public String getWord() {
        return word;
    }

    /**
     * Returns the weight of the word
     * 
     * @return double
     */

    public double val() {
        return val;
    }

    /**
     * Compares this word's weight with another
     *
     * @param w - other word to compareTo
     * @return int
     */
    
    public int compareTo(Word w) {
        return (int) (w.val() - val);
    }
}
