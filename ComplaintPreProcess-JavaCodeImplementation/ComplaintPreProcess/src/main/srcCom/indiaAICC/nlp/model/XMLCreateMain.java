package main.srcCom.indiaAICC.nlp.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.detectlanguage.DetectLanguage;
import com.google.cloud.language.v1.Entity;
import com.google.cloud.language.v1.Sentiment;
import com.google.cloud.language.v1.Token;
import main.srcCom.indiaAICC.nlp.analyzer.Analysis;
import main.srcCom.indiaAICC.nlp.analyzer.GoogleTextAnalyzer;
import main.srcCom.indiaAICC.nlp.analyzer.GrammarChecker;
import main.srcCom.indiaAICC.nlp.analyzer.LanguageParserStanford;
import main.srcCom.indiaAICC.nlp.analyzer.NLPProcessor;
import main.srcCom.indiaAICC.nlp.analyzer.QueryPreprocessing;
import main.srcCom.indiaAICC.nlp.analyzer.SemanticAnalysis;
import main.srcCom.indiaAICC.nlp.analyzer.SyntacticAnalysis;
import main.srcCom.indiaAICC.nlp.processor.GujaratiProcessor;
import main.srcCom.indiaAICC.nlp.processor.GujaratiSynsVO;
import main.srcCom.indiaAICC.util.Paths;

import edu.smu.tspell.wordnet.NounSynset;
import edu.smu.tspell.wordnet.Synset;
import edu.smu.tspell.wordnet.WordNetDatabase;
import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphEdge;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeCoreAnnotations.TreeAnnotation;
import edu.stanford.nlp.util.CoreMap;
import net.didion.jwnl.JWNL;
import net.didion.jwnl.JWNLException;
import net.didion.jwnl.data.POS;

public class XMLCreateMain {

	public static void main(String[] args) {
		int queryHistoryId = 0;
		String languageQuery = "top 5 sales of 2000";
		XMLCreate.processUserRequestAndcreateComponents1(languageQuery,queryHistoryId);
	}
}
