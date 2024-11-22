package main.srcCom.indiaAICC.nlp.analyzer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import main.srcCom.indiaAICC.util.Paths;

import net.didion.jwnl.JWNLException;
import net.didion.jwnl.data.IndexWord;
import net.didion.jwnl.data.POS;
import net.didion.jwnl.data.PointerType;
import net.didion.jwnl.data.PointerUtils;
import net.didion.jwnl.data.list.PointerTargetNodeList;
import net.didion.jwnl.data.list.PointerTargetTree;
import net.didion.jwnl.data.relationship.AsymmetricRelationship;
import net.didion.jwnl.data.relationship.Relationship;
import net.didion.jwnl.data.relationship.RelationshipFinder;
import net.didion.jwnl.data.relationship.RelationshipList;
import net.didion.jwnl.dictionary.Dictionary;

import edu.smu.tspell.wordnet.AdjectiveSynset;
import edu.smu.tspell.wordnet.AdverbSynset;
import edu.smu.tspell.wordnet.NounSynset;
import edu.smu.tspell.wordnet.Synset;
import edu.smu.tspell.wordnet.SynsetType;
import edu.smu.tspell.wordnet.VerbSynset;
import edu.smu.tspell.wordnet.WordNetDatabase;
import edu.smu.tspell.wordnet.WordSense;

public class SemanticAnalysis {
	
	private WordNetDatabase wordnetDatabase;
	public SemanticAnalysis(WordNetDatabase inwordnetDatabase)
	{
		wordnetDatabase = inwordnetDatabase;
	}
	
	public Map<String,String> getSynonymsUsingWordnet(String word) {
		
		//Set<String> synonyms = new TreeSet<String>();
		Map<String,String> tmp = new HashMap<>();
		Synset[] synsets = wordnetDatabase.getSynsets(word);
		for (int i = 0; i < synsets.length; i++) {
			Synset synset = synsets[i];
			String[] wordForms = synset.getWordForms();
			
			for (int j = 0; j < wordForms.length; j++) {
				if(wordForms[j] != null && !tmp.containsKey(wordForms[j])) {
					tmp.put(wordForms[j],synset.toString().substring(0, synset.toString().indexOf('@')));
				}
			}
		}

		return tmp;//new ArrayList<String>(synonyms);
	}

	public Map<String,String> getAntonymsUsingWordnet(String word) {
		return getAntonymsUsingWordnet(word, null);
	}

	public Map<String,String> getAntonymsUsingWordnet(String word, String wordClass) {
		//Set<String> antonyms = new TreeSet<String>();
		Map<String,String> tmp = new HashMap<>();
		Synset[] synsets = wordnetDatabase.getSynsets(word);

		SynsetType wordClassSynsetType = getWordClassSynsetTypeForWordnet(wordClass);
		System.out.println("wordClassSynsetType :: " + wordClassSynsetType);
		for (int i = 0; i < synsets.length; i++) {
			Synset synset = synsets[i];
			SynsetType synsetType = synsets[i].getType();

			if ((wordClassSynsetType != null)
					&& (wordClassSynsetType != synsetType)) {
				continue;
			}

			WordSense[] wordSenses = null;
			String str = "";
			if (synsetType == SynsetType.ADJECTIVE) {
				wordSenses = ((AdjectiveSynset) synset).getAntonyms(word);
				str = "Adjective";
			} else if (synsetType == SynsetType.ADVERB) {
				wordSenses = ((AdverbSynset) synset).getAntonyms(word);
				str = "Adverb";
			} else if (synsetType == SynsetType.NOUN) {
				wordSenses = ((NounSynset) synset).getAntonyms(word);
				str = "Noun";
			} else if (synsetType == SynsetType.VERB) {
				wordSenses = ((VerbSynset) synset).getAntonyms(word);
				str = "Verb";
			}

			if (wordSenses != null) {
				for (int j = 0; j < wordSenses.length; j++) {
					if(wordSenses[j] != null && wordSenses[j].getWordForm() != null && !tmp.containsKey(wordSenses[j].getWordForm())) {
						//antonyms.add(wordSenses[j].getWordForm());
						tmp.put(wordSenses[j].getWordForm(),str);
					}
				}
			}
		}

		return tmp;//new ArrayList<String>(antonyms);
	}
	
	public Synset[] getHyponymforNounUsingWordnet(String word) {
		NounSynset nounSynset;
		NounSynset[] hyponyms;
		@SuppressWarnings("resource")
		Synset[] synsets = wordnetDatabase.getSynsets(word, SynsetType.NOUN);
        return synsets;
		/*
		 * for (int i = 0; i < synsets.length; i++) { nounSynset = (NounSynset)
		 * synsets[i]; hyponyms = nounSynset.getHyponyms();
		 * System.out.println(nounSynset.getWordForms()[0] + ": " +
		 * nounSynset.getDefinition() + ") has " + hyponyms.length + " hyponyms"); for
		 * (int j = 0; j < hyponyms.length; j++) { System.out.println("hyponyms....." +
		 * hyponyms[j].getDefinition()); }
		 * 
		 * }
		 */
	}
	
	public Synset[] getHypernymforNounUsingWordnet(String word) {
		NounSynset nounSynset;
		NounSynset[] hypernyms;
		@SuppressWarnings("resource")
		Synset[] synsets = wordnetDatabase.getSynsets(word, SynsetType.NOUN);
		return synsets;
		/*
		 * for (int i = 0; i < synsets.length; i++) { nounSynset = (NounSynset)
		 * synsets[i]; hypernyms = nounSynset.getHypernyms();
		 * System.out.println(nounSynset.getWordForms()[0] + ": " +
		 * nounSynset.getDefinition() + ") has " + hypernyms.length + " hypernyms"); for
		 * (int j = 0; j < hypernyms.length; j++) {
		 * System.out.println("hypernyms for Noun....." + hypernyms[j].getDefinition());
		 * }
		 * 
		 * }
		 */
	}
	
//	public void getHypernymforVerbUsingWordnet(String word) {
//		VerbSynset verbSynset;
//		VerbSynset[] hypernyms;
//		@SuppressWarnings("resource")
//		Synset[] synsets = wordnetDatabase.getSynsets(word, SynsetType.NOUN);
//
//		for (int i = 0; i < synsets.length; i++) {
//			verbSynset = (VerbSynset) synsets[i];
//			hypernyms = verbSynset.getHypernyms();
//			System.out.println(verbSynset.getWordForms()[0] + ": "
//					+ verbSynset.getDefinition() + ") has " + hypernyms.length
//					+ " hypernyms");
//			for (int j = 0; j < hypernyms.length; j++) {
//				System.out.println("hypernyms for verb....." + hypernyms[j].getDefinition());
//			}
//			
//		}
//	}
	
	public SynsetType getWordClassSynsetTypeForWordnet(String wordClass) {
		SynsetType result = null;

		if (wordClass != null) {
			if (wordClass.equals("noun")) {
				result = SynsetType.NOUN;
			} else if (wordClass.equals("verb")) {
				result = SynsetType.VERB;
			} else if (wordClass.equals("adjective")) {
				result = SynsetType.ADJECTIVE;
			} else if (wordClass.equals("adverb")) {
				result = SynsetType.ADVERB;
			}
		}

		return result;
	}
	
//	public void getWordnetThesaurus(String word) {
//		try {
//			System.setProperty("wordnet.database.dir",
//					Paths.LINGUISTIC_WORDNET_DATABASEDIR);
//			WordNetDatabase wordnetData = WordNetDatabase.getFileInstance();
//
//			@SuppressWarnings("resource")
//			List<String> listSynonymsNames = getSynonymsUsingWordnet(word);
//			List<String> listAntonymsNames = getAntonymsUsingWordnet(word);
//
//			for (String synonymsName : listSynonymsNames) {
//				System.out.println("Synonyms : " + synonymsName);
//			}
//			for (String antonymsName : listAntonymsNames) {
//				System.out.println("Antonyms : " + antonymsName);
//			}
//
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
	
	public void getHypernymListOperationUsingWordnet(IndexWord word) throws JWNLException {
		// Get all of the hypernyms (parents) of the first sense of <var>word</var>
		PointerTargetNodeList hypernyms = PointerUtils.getInstance().getDirectHypernyms(word.getSense(1));
		System.out.println("Direct hypernyms of \"" + word.getLemma() + "\":");
		hypernyms.print();
	}

	public void getHyponymTreeUsingWordnet(IndexWord word) throws JWNLException {
		// Get all the hyponyms (children) of the first sense of <var>word</var>
		PointerTargetTree hyponyms = PointerUtils.getInstance().getHyponymTree(word.getSense(1));
		System.out.println("Hyponyms of \"" + word.getLemma() + "\":");
		hyponyms.print();
	}

	public void demonstrateAsymmetricRelationshipOperationUsingWordnet(IndexWord start, IndexWord end) throws JWNLException {
		// Try to find a relationship between the first sense of <var>start</var> and the first sense of <var>end</var>
		RelationshipList list = RelationshipFinder.getInstance().findRelationships(start.getSense(1), end.getSense(1), PointerType.HYPERNYM);
		System.out.println("Hypernym relationship between \"" + start.getLemma() + "\" and \"" + end.getLemma() + "\":");
		for (Iterator itr = list.iterator(); itr.hasNext();) {
			((Relationship) itr.next()).getNodeList().print();
		}
		System.out.println("Common Parent Index: " + ((AsymmetricRelationship) list.get(0)).getCommonParentIndex());
		System.out.println("Depth: " + ((Relationship) list.get(0)).getDepth());
	}
	public void demonstrateAsymmetricRelationshipOperationUsingWordnet(
			String start, String end) throws JWNLException {
		// Try to find a relationship between the first sense of
		// <var>start</var> and the first sense of <var>end</var>
		try {
			IndexWord indexStartWord = Dictionary.getInstance()
					.lookupIndexWord(POS.NOUN, start);
			IndexWord indexEndWord = Dictionary.getInstance().lookupIndexWord(
					POS.NOUN, end);
			RelationshipList list = RelationshipFinder.getInstance()
					.findRelationships(indexStartWord.getSense(1),
							indexEndWord.getSense(1), PointerType.HYPERNYM);
			System.out.println("Hypernym relationship between \""
					+ indexStartWord.getLemma() + "\" and \""
					+ indexEndWord.getLemma() + "\":");
			for (Iterator itr = list.iterator(); itr.hasNext();) {
				((Relationship) itr.next()).getNodeList().print();
			}
			System.out.println("Common Parent Index: "
					+ ((AsymmetricRelationship) list.get(0))
							.getCommonParentIndex());
			System.out.println("Depth: "
					+ ((Relationship) list.get(0)).getDepth());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void demonstrateSymmetricRelationshipOperationUsingWordnet(IndexWord start, IndexWord end) throws JWNLException {
		// find all synonyms that <var>start</var> and <var>end</var> have in common
		RelationshipList list = RelationshipFinder.getInstance().findRelationships(start.getSense(1), end.getSense(1), PointerType.SIMILAR_TO);
		System.out.println("Synonym relationship between \"" + start.getLemma() + "\" and \"" + end.getLemma() + "\":");
		for (Iterator itr = list.iterator(); itr.hasNext();) {
			((Relationship) itr.next()).getNodeList().print();
		}
		System.out.println("Depth: " + ((Relationship) list.get(0)).getDepth());
	}
	public void demonstrateSymmetricRelationshipOperationUsingWordnet(
			String start, String end) throws JWNLException {
		// find all synonyms that <var>start</var> and <var>end</var> have in
		// common
		try {
			IndexWord indexStartWord = Dictionary.getInstance().getIndexWord(
					POS.NOUN, start);
			IndexWord indexEndWord = Dictionary.getInstance().getIndexWord(
					POS.NOUN, end);
			RelationshipList list = RelationshipFinder.getInstance()
					.findRelationships(indexStartWord.getSense(1),
							indexEndWord.getSense(1), PointerType.SIMILAR_TO);
			System.out.println("Synonym relationship between \""
					+ indexStartWord.getLemma() + "\" and \""
					+ indexEndWord.getLemma() + "\":");
			for (Iterator itr = list.iterator(); itr.hasNext();) {
				((Relationship) itr.next()).getNodeList().print();
			}
			System.out.println("Depth: "
					+ ((Relationship) list.get(0)).getDepth());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void getThesaurusFromBigHugeLabs(String word) {
		try {
			URL url = new URL(Paths.LINGUISTIC_URL_BIGHUGELABS + word + "/");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "application/json");
			if (conn.getResponseCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ conn.getResponseCode());
			}
			BufferedReader br = new BufferedReader(new InputStreamReader(
					(conn.getInputStream())));
			String output;
			System.out
					.println("Output from getThesaurusFromBigHugeLabs .... \n");
			while ((output = br.readLine()) != null) {
				System.out.println(output);
			}
			conn.disconnect();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
