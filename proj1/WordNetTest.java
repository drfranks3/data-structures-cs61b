import ngordnet.WordNet;

/** Class that demonstrates advanced WordNet functionality.
 *  @author Donald
 */
public class WordNetTest {
    public static void main(String[] args) {
        WordNet wn = new WordNet("./wordnet/synsets1000-subgraph.txt", "./wordnet/hyponyms1000-subgraph.txt");

        System.out.println(wn.hyponyms("vegetable_oil").contains("crude"));
        System.out.println(wn.hyponyms("vegetable_oil").contains("resid"));
    }    
} 
