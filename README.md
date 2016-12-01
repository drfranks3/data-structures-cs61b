# CS61B - Data Structures - Spring 15
This is a collection of projects tasked to CS61B students in the Spring of 2015.

### Project 0: [Checkers61b](http://datastructur.es/sp15/materials/proj/proj0/proj0.html)
Mission was to develop a traditional checkers game with the twist of included "Shield" and "Bomb" pieces.
Shield pieces cannot be killed by a bomb but can still be captured by any piece.
Bomb pieces that complete a capture cause an explosion that kills all pieces in a 3x3 block around the bomb's final position.
*Chain reaction explosions do not occur; if a bomb is within range of an explosion, it is simply killed.*

### Project 1: [NGordNet](http://datastructur.es/sp15/materials/proj/proj1/proj1.html)
Using [Wordnets](http://en.wikipedia.org/wiki/WordNet), directed graphs, and [Google's Ngram datasets](http://storage.googleapis.com/books/ngrams/books/datasetsv2.html), we were able to determine the count of a word in a given year, display all the hyponyms of a word, and plot the relative frequency of all words (or their hyponyms) in a given range.

### Project 2: [Gitlet](http://datastructur.es/sp15/materials/proj/proj2/proj2.html) | Unfinished
Gitlet was the attempt to create a lightweight model of the Git version control system.


### Project 3: [Fun With Tries](http://datastructur.es/sp15/materials/proj/proj2/proj2.html)
Making use of the [Trie](http://www.wikiwand.com/en/Trie) data structure, our goal was to load a dictionary into the system and readily (1) determine if an entry was within the Trie and (2) come up with potential words to search for via autocomplete, and a spellcheck capability.

**This class made a developer think carefully about the structures used to store, read, and interpret data. Particularly in Gitlet, it was necessary to balance the trade-offs of runtime and storage space in order to enhance the user and developer experience.**
