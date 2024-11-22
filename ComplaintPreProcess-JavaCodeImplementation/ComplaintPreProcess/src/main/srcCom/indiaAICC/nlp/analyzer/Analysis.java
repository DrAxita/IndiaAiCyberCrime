package main.srcCom.indiaAICC.nlp.analyzer;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import main.srcCom.indiaAICC.util.Paths;

import edu.smu.tspell.wordnet.Synset;
import edu.smu.tspell.wordnet.SynsetType;
import edu.smu.tspell.wordnet.WordNetDatabase;

import net.didion.jwnl.JWNL;

public class Analysis {
	
	public List<String> tokenizeQueryByDelimiter_LexicalAnalysis_En(String userRequest, String delimiter) {
		List<String> tokens = new ArrayList<String>();
		Scanner sc = new Scanner(userRequest);
		sc.useDelimiter(" ");
		while (sc.hasNext()) {
			tokens.add(sc.next());
		}
		return tokens;
	}
	
	public String getWordForAbbreviation_LexicalAnalysis_EN(String abbreviation) {
		BufferedReader br = null;
		String abbreviationFile = Paths.LINGUISTIC_FILE_BUSINESS_ABBREVIATION;
		String line = "";
		String cvsSplitBy = ",";
		try {
			br = new BufferedReader(new FileReader(abbreviationFile));
			while ((line = br.readLine()) != null) {
				String[] list = line.split(cvsSplitBy);
				if (list[0].equals(abbreviation))
					return list[1];
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	public String getSymbolofNumeralOperator_LexicalAnalysis_EN(String numeralOperator) {

		String csvFile = Paths.LINGUISTIC_FILE_NUMERAL_OPERATORS;
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = ",";
		try {
			br = new BufferedReader(new FileReader(csvFile));
			while ((line = br.readLine()) != null) {
				String[] list = line.split(cvsSplitBy);
				if (numeralOperator.equals(list[0]))
					return list[1];
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	public boolean isNumeralOperator_LexicalAnalysis_EN(String numeralOperator) {

		String csvFile = Paths.LINGUISTIC_FILE_NUMERAL_OPERATORS;
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = ",";
		try {
			br = new BufferedReader(new FileReader(csvFile));
			while ((line = br.readLine()) != null) {
				String[] list = line.split(cvsSplitBy);
				if (numeralOperator.equals(list[0]))
					return true;
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return false;
	}

	public boolean isAggregateWord_LexicalAnalysis_EN(String word) {

		String aggregateFile = Paths.LINGUISTIC_FILE_AGGREGATE;
		BufferedReader br = null;
		String line = "";
		try {
			br = new BufferedReader(new FileReader(aggregateFile));
			while ((line = br.readLine()) != null) {
				if (line.equals(word))
					return true;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return false;
	}

	public boolean isSimplePreposition_LexicalAnalysis_EN(String word) {
		String simplePropositions = Paths.LINGUISTIC_FILE_SIMPLE_PREPOSITION;
		BufferedReader br = null;
		String line = "";
		try {
			br = new BufferedReader(new FileReader(simplePropositions));
			while ((line = br.readLine()) != null) {
				if (line.equals(word))
					return true;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return false;
	}

	public boolean isCompoundPreposition_LexicalAnalysis_EN(String word) {
		String compoundPropositions = Paths.LINGUISTIC_FILE_COMPOUND_PREPOSITION;
		BufferedReader br = null;
		String line = "";
		try {
			br = new BufferedReader(new FileReader(compoundPropositions));
			while ((line = br.readLine()) != null) {
				if (line.equals(word))
					return true;
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return false;
	}

	 public String stemTokenApacheOpenNLPPorterStemer_MorphologicalAnalysis_En(String token) {
		  PorterStemmer porterStemmer= new PorterStemmer();
		  return porterStemmer.stem(token);
	  }
}
