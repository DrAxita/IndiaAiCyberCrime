package main.srcCom.indiaAICC.nlp.analyzer;

import edu.smu.tspell.wordnet.Synset;
import edu.smu.tspell.wordnet.SynsetType;
import edu.smu.tspell.wordnet.WordNetDatabase;
import net.didion.jwnl.data.IndexWord;
import net.didion.jwnl.data.IndexWordSet;
import net.didion.jwnl.data.POS;
import net.didion.jwnl.dictionary.Dictionary;

public class SyntacticAnalysis {
	
	private WordNetDatabase wordnetDatabase;
	public SyntacticAnalysis(WordNetDatabase inwordnetDatabase)
	{
		wordnetDatabase = inwordnetDatabase;
	}
	public POS[] findPartsOfSpeechUsingWordnet(String word) {
            try {
            // Look up all IndexWords (an IndexWord can only be one POS)
            IndexWordSet set = Dictionary.getInstance().lookupAllIndexWords(word);
            // Turn it into an array of IndexWords
            IndexWord[] words = set.getIndexWordArray();
            // Make the array of POS
            POS[] pos = new POS[words.length];
            for (int i = 0; i < words.length; i++) {
                pos[i] = words[i].getPOS();
            }

                    return pos;
                    // If we found at least one we found the word
            } catch (Exception e) {
                    e.printStackTrace();
            }
            return null;
	}
	
	public String[] getWordnetSynsetType(String word) {
		Synset[] synsets = wordnetDatabase.getSynsets(word);
		String[] synsetTypeStr = new String[synsets.length+5];
		for (int i = 0; i < synsets.length; i++) {
			SynsetType synsetType = synsets[i].getType();
			synsetTypeStr[i] = synsetType.toString();

			// if (wordClass != null) {
			// if (wordClass.equals("noun")) {
			// result = SynsetType.NOUN;
			// } else if (wordClass.equals("verb")) {
			// result = SynsetType.VERB;
			// } else if (wordClass.equals("adjective")) {
			// result = SynsetType.ADJECTIVE;
			// } else if (wordClass.equals("adverb")) {
			// result = SynsetType.ADVERB;
			// }
			// }
		}
		return synsetTypeStr;
	}
}
