package lse;

import java.io.*;
import java.util.*;

/**
 * This class builds an index of keywords. Each keyword maps to a set of pages in
 * which it occurs, with frequency of occurrence in each page.
 *
 */
public class LittleSearchEngine {
	
	/**
	 * This is a hash table of all keywords. The key is the actual keyword, and the associated value is
	 * an array list of all occurrences of the keyword in documents. The array list is maintained in 
	 * DESCENDING order of frequencies.
	 */
	HashMap<String,ArrayList<Occurrence>> keywordsIndex;
	
	/**
	 * The hash set of all noise words.
	 */
	HashSet<String> noiseWords;
	
	/**
	 * Creates the keyWordsIndex and noiseWords hash tables.
	 */
	public LittleSearchEngine() {
		keywordsIndex = new HashMap<String,ArrayList<Occurrence>>(1000,2.0f);
		noiseWords = new HashSet<String>(100,2.0f);
	}
	
	/**
	 * Scans a document, and loads all keywords found into a hash table of keyword occurrences
	 * in the document. Uses the getKeyWord method to separate keywords from other words.
	 * 
	 * @param docFile Name of the document file to be scanned and loaded
	 * @return Hash table of keywords in the given document, each associated with an Occurrence object
	 * @throws FileNotFoundException If the document file is not found on disk
	 */
	public HashMap<String,Occurrence> loadKeywordsFromDocument(String docFile) 
	throws FileNotFoundException {
		// Always initialize your needed members first
		HashMap<String, Occurrence> ret = new HashMap<>();
		Scanner sc = new Scanner(new FileInputStream(docFile));

		// Scan for all words
		// Scanners use a space as the default delim, no need to worry
		while (sc.hasNext()) {
			// Get the current keyword
			String word = getKeyword(sc.next());

			// If the word isn't null, that means it's a keyword
			if (word != null) {
                // Get the current occurrence and increase it by 1 if the element exists
                // Otherwise add a new occurrence
                if (ret.containsKey(word))
                    ret.get(word).frequency++;
                else
                    ret.put(word, new Occurrence(docFile, 1));
			}
		}

		// Return
		return ret;
	}
	
	/**
	 * Merges the keywords for a single document into the master keywordsIndex
	 * hash table. For each keyword, its Occurrence in the current document
	 * must be inserted in the correct place (according to descending order of
	 * frequency) in the same keyword's Occurrence list in the master hash table. 
	 * This is done by calling the insertLastOccurrence method.
	 * 
	 * @param kws Keywords hash table for a document
	 */
	public void mergeKeywords(HashMap<String,Occurrence> kws) {
		for (Map.Entry<String, Occurrence> entry : kws.entrySet()) {
			// Store entry value in a variable
			ArrayList<Occurrence> entryVal = keywordsIndex.getOrDefault(entry.getKey(), new ArrayList<>());
			entryVal.add(entry.getValue());

			// Perform insertLastOccurrence\
            ArrayList<Integer> mids = insertLastOccurrence(entryVal);

            if (mids != null)
			    System.out.println(mids);

			keywordsIndex.put(entry.getKey(), entryVal);
		}
	}
	
	/**
	 * Given a word, returns it as a keyword if it passes the keyword test,
	 * otherwise returns null. A keyword is any word that, after being stripped of any
	 * trailing punctuation(s), consists only of alphabetic letters, and is not
	 * a noise word. All words are treated in a case-INsensitive manner.
	 * 
	 * Punctuation characters are the following: '.', ',', '?', ':', ';' and '!'
	 * NO OTHER CHARACTER SHOULD COUNT AS PUNCTUATION
	 * 
	 * If a word has multiple trailing punctuation characters, they must all be stripped
	 * So "word!!" will become "word", and "word?!?!" will also become "word"
	 * 
	 * See assignment description for examples
	 * 
	 * @param word Candidate word
	 * @return Keyword (word without trailing punctuation, LOWER CASE)
	 */
	public String getKeyword(String word) {
		String ret = null;

		// If the word matches the pattern
		// defined by the description
		if (word.matches("\\w+(?:$|[\\!\\?\\;\\:\\.\\,]*)")) {
			// The first instance should be the keyword
			ret = word.toLowerCase().split("(?<=[\\!\\?\\;\\:\\.\\,])|(?=[\\!\\?\\;\\:\\.\\,])")[0];

			// Check if the keyword matches a noise word
			for (String n : noiseWords) {
				if (ret.matches("(?:^|\\w)" + n.trim().toLowerCase() + "(?:$|\\w)")) {
					ret = null;
					break;
				}
			}
		}

		// Return ret
		return ret;
	}
	
	/**
	 * Inserts the last occurrence in the parameter list in the correct position in the
	 * list, based on ordering occurrences on descending frequencies. The elements
	 * 0..n-2 in the list are already in the correct order. Insertion is done by
	 * first finding the correct spot using binary search, then inserting at that spot.
	 * 
	 * @param occs List of Occurrences
	 * @return Sequence of mid point indexes in the input list checked by the binary search process,
	 *         null if the size of the input list is 1. This returned array list is only used to test
	 *         your code - it is not used elsewhere in the program.
	 */
	public ArrayList<Integer> insertLastOccurrence(ArrayList<Occurrence> occs) {
		// Initialize arrList and binary search pointers
		// Also have a convenient pointer to the last element
		Occurrence lastOc = occs.get(occs.size() - 1);
		ArrayList<Integer> midpointIndices = new ArrayList<>();
		int left = 0, right = occs.size() - 2, mid;

		// Binary search for the correct insert position
		while (left <= right) {
			// Calculate mid and add it to the ind list
			// May as well have a pointer for the element at that index
			// And (maybe) the one behind it as well
			mid = (left + right) / 2;
			System.out.print("" + mid + ", ");
			midpointIndices.add(mid);
			Occurrence back = occs.get(mid), front = null;

			// If there is an element behind the middle
			if (mid > 0)
				front = occs.get(mid - 1);

			// Check if mid spot fits
			// The criteria is that its freq is more than the one at the current pos
			// and either it's being inserted at the front or the one behind it is bigger
			if (lastOc.frequency > back.frequency) {
				if (front == null || lastOc.frequency <= front.frequency) { // Found it
					occs.add(mid, occs.remove(occs.size() - 1));
					break;
				} else // Too far right
					right = mid - 1;
			} else // Too far left
				left = mid + 1;
		}

		// Return midpointIndices if it has anything in it
		return midpointIndices.size() > 0 ? midpointIndices : null;
	}
	
	/**
	 * This method indexes all keywords found in all the input documents. When this
	 * method is done, the keywordsIndex hash table will be filled with all keywords,
	 * each of which is associated with an array list of Occurrence objects, arranged
	 * in decreasing frequencies of occurrence.
	 * 
	 * @param docsFile Name of file that has a list of all the document file names, one name per line
	 * @param noiseWordsFile Name of file that has a list of noise words, one noise word per line
	 * @throws FileNotFoundException If there is a problem locating any of the input files on disk
	 */
	public void makeIndex(String docsFile, String noiseWordsFile) 
	throws FileNotFoundException {
		// load noise words to hash table
		Scanner sc = new Scanner(new File(noiseWordsFile));
		while (sc.hasNext()) {
			String word = sc.next();
			noiseWords.add(word);
		}
		
		// index all keywords
		sc = new Scanner(new File(docsFile));
		while (sc.hasNext()) {
			String docFile = sc.next();
			HashMap<String,Occurrence> kws = loadKeywordsFromDocument(docFile);
			mergeKeywords(kws);
		}
		sc.close();
	}
	
	/**
	 * Search result for "kw1 or kw2". A document is in the result set if kw1 or kw2 occurs in that
	 * document. Result set is arranged in descending order of document frequencies. 
	 * 
	 * Note that a matching document will only appear once in the result. 
	 * 
	 * Ties in frequency values are broken in favor of the first keyword. 
	 * That is, if kw1 is in doc1 with frequency f1, and kw2 is in doc2 also with the same 
	 * frequency f1, then doc1 will take precedence over doc2 in the result. 
	 * 
	 * The result set is limited to 5 entries. If there are no matches at all, result is null.
	 * 
	 * See assignment description for examples
	 * 
	 * @param kw1 First keyword
	 * @param kw1 Second keyword
	 * @return List of documents in which either kw1 or kw2 occurs, arranged in descending order of
	 *         frequencies. The result size is limited to 5 documents. If there are no matches, 
	 *         returns null or empty array list.
	 */
	public ArrayList<String> top5search(String kw1, String kw2) {
		// Create return array and occurrence list copies
		// Don't want to be editing the reference after all
		ArrayList<String> ret = new ArrayList<>();
		String toAdd;
		ArrayList<Occurrence>
				oc1 = new ArrayList<>(keywordsIndex.getOrDefault(kw1, new ArrayList<>())),
				oc2 = new ArrayList<>(keywordsIndex.getOrDefault(kw2, new ArrayList<>()));

		// Simple merge; if the highest doc freq in oc1 is greater than or equal to oc2,
		// remove oc1's front occurrence and get the name
		// Otherwise, do that to oc2
		// Rinse and repeat until both lists are empty or your return size is == 5
		while (ret.size() < 5 && !(oc1.isEmpty() && oc2.isEmpty())) {
		    // If either the second occurrence list is empty
            // or neither of them are empty and oc1[0] freq >= oc2[0] freq
			if (oc2.isEmpty() || !oc1.isEmpty() && (oc1.get(0).frequency >= oc2.get(0).frequency))
				toAdd = oc1.remove(0).document;
			else
				toAdd = oc2.remove(0).document;

			// Do not add duplicates
			if (!ret.contains(toAdd))
				ret.add(toAdd);
		}

		// Return
		return ret;
	}
}
