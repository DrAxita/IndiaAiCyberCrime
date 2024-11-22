package main.srcCom.indiaAICC.nlp.analyzer;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import main.srcCom.indiaAICC.util.Paths;

// lower case conversion Stop Words and Spelling Correction
public class QueryPreprocessing {
	private List<String> stopwordlist;

	public QueryPreprocessing()
	{
		if (stopwordlist == null)
			getStopWords();
	}
	public String converttolowerCase(String word) {
		return word.toLowerCase();
	}
	public boolean isStopWord(String word) {
		if (stopwordlist != null)
		{
			for (int i = 0; i < stopwordlist.size(); i++)
				if (stopwordlist.get(i).toLowerCase().equals(word.toLowerCase()))
					return true;
		}
		return false;
	}

	public List<String> getStopWords() {
		List<String> wordlist = mergeStopWords();
		return wordlist;
	}

	public List<String> mergeStopWords() {
		List<String> list = new ArrayList<String>();
		List<String> mainlist = new ArrayList<String>();
		String f1 = null;
		f1 = Paths.LINGUISTIC_FILE_STOP_WORDS;
		list = filedata(f1);
		mainlist.addAll(list);
		f1 = Paths.LINGUISTIC_FILE_STOP_WORDS_MYSQL;
		list = filedata(f1);
		mainlist.addAll(list);
		// f1="E:\\Phd in Computer Science\\Implementation\\phd impl\\Workspace 2016\\NPISourceProject\\usefulefiles\\stopwords 5.txt";

		f1 = Paths.LINGUISTIC_FILE_STOP_WORDS_TEXTFIXER;
		list = filedata(f1);
		mainlist.addAll(list);
		f1 = Paths.LINGUISTIC_FILE_STOP_WORDS_GOOGLE;
		list = filedata(f1);
		mainlist.addAll(list);
		String[] finalWords = new String[mainlist.size()];
		finalWords = mainlist.toArray(finalWords);
		List<String> wordlist = new ArrayList<String>();
		;
		System.out.println(finalWords.length);
		wordlist = removeDuplicate(finalWords);
		stopwordlist = wordlist;
		return wordlist;
	}

	public void displayStopWords() {
		Iterator<String> iterator = stopwordlist.iterator();
		while (iterator.hasNext()) {
			System.out.print("[" + iterator.next() + "]  ");
		}
	}

	public String[] removeStopWords(String[] words) {
		ArrayList<String> tokens = new ArrayList<String>(Arrays.asList(words));
		for (int i = 0; i < tokens.size(); i++) {
			if (stopwordlist.contains(tokens.get(i))) {
				tokens.remove(i);
			}
		}
		return (String[]) tokens.toArray(new String[tokens.size()]);
	}

	public void addStopWordsFromArray(String[] stopWords) {
		try {
			// String[] defaultStopWords = {"i", "a", "about", "an", "are",
			// "as", "at",
			// "be", "by", "com", "for", "from", "how", "in", "is", "it", "of",
			// "on",
			// "or", "that", "the", "this", "to", "was", "what", "when",
			// "where",
			// "who", "will", "with"};

			stopwordlist.addAll(Arrays.asList(stopWords));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void addStopWordsFromFile(String fileName) {
		try {
			BufferedReader bufferedreader = new BufferedReader(new FileReader(
					fileName));
			while (bufferedreader.ready()) {
				stopwordlist.add(bufferedreader.readLine());
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	// SimpleTokenizer simpleTokenizer = SimpleTokenizer.INSTANCE;
	// paragraph = "A simple approach is to create a class "
	// + "to hold and remove stopwords.";
	// String tokens[] = simpleTokenizer.tokenize(paragraph);
	// String list[] = stopWords.removeStopWords(tokens);
	// for (String word : list) {
	// System.out.println(word);
	// }
	private static List<String> filedata(String file1) {
		List<String> wordlist = new ArrayList<String>();
		try {
			FileInputStream fstream = new FileInputStream(file1);
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;
			while ((strLine = br.readLine()) != null) {
				wordlist.add(strLine);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return wordlist;
	}

	private static List<String> removeDuplicate(String[] words) {
		List<String> wordlist = new ArrayList<String>();
		boolean isDuplicate = false;
		try {
			for (int i = 0; i < words.length; i++) {
				isDuplicate = false;
				for (int j = i + 1; j < words.length; j++) {
					if (words[i].toLowerCase().equals(words[j].toLowerCase())) {
						isDuplicate = true;
						break;
					}
				}
				if (isDuplicate == false)
					wordlist.add(words[i]);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return wordlist;
	}
	public final String correctSpelling(String word) {
		String correctWord = null;
		String file = Paths.LINGUISTIC_FILE_SPELLING;
		HashMap<String, Integer> nWords = new HashMap<String, Integer>();
		try {
			BufferedReader in = new BufferedReader(new FileReader(file));
			Pattern p = Pattern.compile("\\w+");
			for (String temp = ""; temp != null; temp = in.readLine()) {
				Matcher m = p.matcher(temp.toLowerCase());
				while (m.find())
					nWords.put((temp = m.group()),
							nWords.containsKey(temp) ? nWords.get(temp) + 1 : 1);
			}
			in.close();
			if (nWords.containsKey(word))
				return word;
			ArrayList<String> list = editspelling(word);
			HashMap<Integer, String> candidates = new HashMap<Integer, String>();
			for (String s : list)
				if (nWords.containsKey(s))
					candidates.put(nWords.get(s), s);
			if (candidates.size() > 0)
				return candidates.get(Collections.max(candidates.keySet()));
			for (String s : list)
				for (String w : editspelling(s))
					if (nWords.containsKey(w))
						candidates.put(nWords.get(w), w);
			correctWord = candidates.size() > 0 ? candidates.get(Collections
					.max(candidates.keySet())) : word;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return correctWord;
	}
	private final ArrayList<String> editspelling(String word) {
		ArrayList<String> result = new ArrayList<String>();
		for (int i = 0; i < word.length(); ++i)
			result.add(word.substring(0, i) + word.substring(i + 1));
		for (int i = 0; i < word.length() - 1; ++i)
			result.add(word.substring(0, i) + word.substring(i + 1, i + 2)
					+ word.substring(i, i + 1) + word.substring(i + 2));
		for (int i = 0; i < word.length(); ++i)
			for (char c = 'a'; c <= 'z'; ++c)
				result.add(word.substring(0, i) + String.valueOf(c)
						+ word.substring(i + 1));
		for (int i = 0; i <= word.length(); ++i)
			for (char c = 'a'; c <= 'z'; ++c)
				result.add(word.substring(0, i) + String.valueOf(c)
						+ word.substring(i));
		return result;
	}


}
