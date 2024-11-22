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

public class LinguisticModel {

	
	public static void processcrimeComplaintAndcreateComponents(String crimeComplaint)
    {

    	try
    	{
    		//String standardQuery = crimeComplaint;
    	    int complaintId = 1;
    	    String preProcessedCrimeComplaint = new String();
                
    		Element rootElement = null;
    		Element mainElement = null;
    		DocumentBuilderFactory documentBuilderFactory;
    		documentBuilderFactory = DocumentBuilderFactory.newInstance();
    		Document document = null;

    		DocumentBuilder builder = documentBuilderFactory.newDocumentBuilder();
    		String strFileName = "intermediateProcessedComplaint"+complaintId+".xml";
    		String intermediateComponentFileStr = Paths.LINGUISTIC_intermediate_Processed_UserRequest+strFileName;
    		File f1 = new File(intermediateComponentFileStr);
    		if(f1.exists()) {
    			f1.delete();
    		}
    		document = builder.newDocument();
    		mainElement = document.createElement("linguistic-Complaint-components");
    		document.appendChild(mainElement);
    		
    		rootElement = document.createElement("complaint");
    		mainElement.appendChild(rootElement);

    		Element childElement, subElement , requestAnalyze, requestSentence , statement, clause, tokens;
    		
    		subElement = document.createElement("complaintDesc");
    		subElement.appendChild(document.createTextNode(crimeComplaint));
    		rootElement.appendChild(subElement);
    		
    		/*LanguageParserStanford languageParserStanford3 = new LanguageParserStanford();
    		languageParserStanford3.getSubject_Stanford_En(languageQuery);*/
    		
    		DetectLanguage.apiKey="e1ec7ac68976be4706d8ffd1a4608c47";
            List<com.detectlanguage.Result> languageSingle =  DetectLanguage.detect(crimeComplaint);
            String strLanguage = languageSingle.get(0).language;
            
            /*if(strLanguage.equals("gu")) {

            	Map<String,ArrayList<GujaratiSynsVO>> mapGujaratiVO = GujaratiProcessor.findTheMatchingWord(languageQuery);
            	System.out.println("GujaratiSyns");
            	mapGujaratiVO.forEach((k,v)->{

            		for (int i = 0; i <v.size(); i++) {
            			System.out.println(k+" = "+ v.toString());	
            		}

            	});

            	boolean ismatchstopword = GujaratiProcessor.matchstopword(languageQuery);
            	System.out.println("ismatchstopword == >"+ismatchstopword);


            	boolean isNREToken = GujaratiProcessor.matchNREToken(languageQuery);

            	System.out.println("isNREToken == >"+isNREToken);

            	String strGUOREN = GujaratiProcessor.matchENToGUAndGUToEN(languageQuery,true);

            	System.out.println("strGUOREN == >"+strGUOREN);

            	String splitString = GujaratiProcessor.removeNONINUNA(languageQuery);

            	System.out.println("splitString == >"+splitString);

            }*/
			
            Sentiment sentiment = null;
            List<Token> syntaxTextList = null;
            List<Entity> entityList = null;
            if(strLanguage.equals("en")) {
    		syntaxTextList = GoogleTextAnalyzer.analyzeSyntaxText_Google_En(crimeComplaint,strLanguage);
    		entityList = GoogleTextAnalyzer.analyzeEntitiesText_Google_En(crimeComplaint,strLanguage);
    		sentiment = GoogleTextAnalyzer.analyzeSentimentText_Google_En(crimeComplaint,strLanguage);
    		//List<ClassificationCategory> categoryList = GoogleTextAnalyzer.processTextGoogleAnalizeCategory_Google_En(languageQuery);
            }
    		String queryCategory = "";//NLPProcessor.processText_Google_En();
    		requestAnalyze = document.createElement("request_analyze");
    		rootElement.appendChild(requestAnalyze);
    		
    		subElement = document.createElement("complaint_sentences_Language");
    		subElement.appendChild(document.createTextNode(strLanguage));
    		requestAnalyze.appendChild(subElement);
    		if(strLanguage.equals("en")) {
    			subElement = document.createElement("complaint_Category_En");
    			subElement.appendChild(document.createTextNode(queryCategory));
    			requestAnalyze.appendChild(subElement);

    			Element sentimentElement = document.createElement("complaint_sentiment_Googl_En");
    			requestAnalyze.appendChild(sentimentElement);

    			if(sentiment != null ) {
    			subElement = document.createElement("sentimentMagnitude_En");
    			subElement.appendChild(document.createTextNode(String.valueOf(sentiment.getMagnitude())));
    			sentimentElement.appendChild(subElement);

    			subElement = document.createElement("sentimentScore_En");
    			subElement.appendChild(document.createTextNode(String.valueOf(sentiment.getScore())));
    			sentimentElement.appendChild(subElement);
    			}
    		}
    		Map<String,String> stanfordPOC = new HashMap<>();
    		Element sentencesanalyze = document.createElement("sentences_analyze");
    		requestAnalyze.appendChild(sentencesanalyze);
    			
    		Element sentences = document.createElement("sentences");
    			sentencesanalyze.appendChild(sentences);
    			
    			requestSentence = document.createElement("sentence");
    			requestSentence.setAttribute("id", ""+complaintId);
    			sentences.appendChild(requestSentence);
    			
    			
    			if(strLanguage.equals("en")) {
    				Element sentence_differentia = document.createElement("sentence_differentia");
    				requestSentence.appendChild(sentence_differentia);
    				
    				Element sentence_Element = document.createElement("sentence_Constitution");
    				requestSentence.appendChild(sentence_Element);
    				sentence_Element = document.createElement("sentence_Purpose");
    				requestSentence.appendChild(sentence_Element);
    				sentence_Element = document.createElement("sentence_Degree");
    				requestSentence.appendChild(sentence_Element);
    				sentence_Element = document.createElement("sentence_Constituent");
    				requestSentence.appendChild(sentence_Element);
    				sentence_Element = document.createElement("sentence_Affirmation");
    				requestSentence.appendChild(sentence_Element);
    				sentence_Element = document.createElement("sentence_Method");
    				requestSentence.appendChild(sentence_Element);
    				sentence_Element = document.createElement("sentence_Voice");
    				requestSentence.appendChild(sentence_Element);
    				sentence_Element = document.createElement("sentence_Speech");
    				requestSentence.appendChild(sentence_Element);
    				sentence_Element = document.createElement("sentence_Structure");
    				requestSentence.appendChild(sentence_Element);
    			
    			
    			Element sentenceSyntacticCorrect = document.createElement("isSentenceSyntacticCorrect_En");                    
    			requestSentence.appendChild(sentenceSyntacticCorrect);

    			boolean isGrammarValid = true;
    			GrammarChecker grammarChecker = new GrammarChecker(crimeComplaint);
    			subElement = document.createElement("is_Sentence_Grammar_well-formed_Gingr_En");
    			isGrammarValid = GrammarChecker.checkGingerGrammar_En(crimeComplaint);
    			subElement.appendChild(document.createTextNode(String.valueOf(isGrammarValid)));
    			sentenceSyntacticCorrect.appendChild(subElement);


    			subElement = document.createElement("is_Sentence_Grammar_well-formed_langTool_En");    
    			isGrammarValid = GrammarChecker.languageToolGrammarChecker_En(crimeComplaint);  
    			subElement.appendChild(document.createTextNode(String.valueOf(isGrammarValid)));
    			sentenceSyntacticCorrect.appendChild(subElement);

    			subElement = document.createElement("is_Sentence_Grammar_well-formed_GrammarBot_En");    
    			isGrammarValid = grammarChecker.checkGrammarBot_En();  
    			subElement.appendChild(document.createTextNode(String.valueOf(isGrammarValid)));
    			sentenceSyntacticCorrect.appendChild(subElement);

    			subElement = document.createElement("is_Sentence_Grammar_well-formed_proposed_En");    
    			String grammarBotResponse = grammarChecker.boatResponse_En();  
    			subElement.appendChild(document.createTextNode(grammarBotResponse));
    			sentenceSyntacticCorrect.appendChild(subElement);

    			CoreMap stanfordCoreMap = LanguageParserStanford.getStanfordParseTree_Stanford_En(crimeComplaint);
    			String score = "";
    			
    			if(stanfordCoreMap != null) {
    				Tree tree = stanfordCoreMap.get(TreeAnnotation.class); 
    				score = tree.score()+"";
    				List<Tree> leaves = tree.getLeaves();
    				for (Tree leaf : leaves) { 
    					Tree parent = leaf.parent(tree);
    					stanfordPOC.put(leaf.label().value(), parent.label().value());
    				}
    			}

    			subElement = document.createElement("StanfordGrammaticalityScore_En");    
    			subElement.appendChild(document.createTextNode(score));
    			sentenceSyntacticCorrect.appendChild(subElement);

    			LanguageParserStanford languageParserStanford1 = new LanguageParserStanford(crimeComplaint);
    			Tree tree = languageParserStanford1.getConstituency_Stanford_En();
    			Element consituency = document.createElement(tree.value());
    			constituencyTreeXML(tree,document,consituency);

    			Element ConstituencyElement = document.createElement("Sentence_ConstituencyTree_stnfd_En");
    			ConstituencyElement.appendChild(consituency);
    			requestSentence.appendChild(ConstituencyElement); 
    			LanguageParserStanford languageParserStanford = new LanguageParserStanford(crimeComplaint);
    			SemanticGraph dependencyString = languageParserStanford.getDependency_Stanford_En();
    			Element DependencyElement = document.createElement("Sentence_DependencyTree_stnfd_En");
    			dependencyTreeXML(dependencyString,document,DependencyElement);
    			requestSentence.appendChild(DependencyElement); 
    		}
    		statement = document.createElement("sentence_clauses");
    		requestSentence.appendChild(statement);
    		
    		clause = document.createElement("clause");
    		statement.appendChild(clause);
    		
    		tokens = document.createElement("clause_Text");
    		clause.appendChild(tokens);
    		
    		tokens = document.createElement("clause_Type_Name");
    		clause.appendChild(tokens);
    		
    	
    		JWNL.initialize(new FileInputStream(Paths.LINGUISTIC_WORDNET_HELPER));
    		System.setProperty("wordnet.database.dir",Paths.LINGUISTIC_WORDNET_DATABASEDIR);
    		WordNetDatabase wordnetDatabase = WordNetDatabase.getFileInstance();
    		
    		Analysis lexicalAnalysis = new Analysis();
    		QueryPreprocessing queryPreprocessing = new QueryPreprocessing();
            SemanticAnalysis semanticAnalysis = new SemanticAnalysis(wordnetDatabase);
            SyntacticAnalysis syntacticAnalysis = new SyntacticAnalysis(wordnetDatabase);
    		
    		String strtoken;
    		String delimiter = " ";
    		List<String> tokenslst = lexicalAnalysis.tokenizeQueryByDelimiter_LexicalAnalysis_En(crimeComplaint, delimiter);

    		List<List<com.detectlanguage.Result>> language = DetectLanguage.detect(tokenslst.toArray(new String[tokenslst.size()]));

    		subElement = document.createElement("token_list_PTB"); 
            subElement.appendChild(document.createTextNode(NLPProcessor.ptbTokenizer(crimeComplaint)));
 			tokens.appendChild(subElement);
 			
 			subElement = document.createElement("token_list_LngPpe"); 
 			subElement.appendChild(document.createTextNode(NLPProcessor.lingpipeTokenize(crimeComplaint)));
 			tokens.appendChild(subElement);
    		for(int i=0;i<tokenslst.size();i++)
    		{
                
    			strtoken = tokenslst.get(i);
    			Element tokenElement = document.createElement("token");     
    			tokenElement.setAttribute("index_id", ""+(i+1));
    			tokens.appendChild(tokenElement);

    			subElement = document.createElement("token_name");
    			subElement.appendChild(document.createTextNode(strtoken));
    			tokenElement.appendChild(subElement);
    			
    			if(strLanguage.equals("gu")) {
    				boolean isNREToken = GujaratiProcessor.matchNREToken(strtoken);
    				subElement = document.createElement("name_Gu");
        			tokenElement.appendChild(subElement);
        			
        			Element childsubElement = document.createElement("Isentity_Gu");
        			childsubElement.appendChild(document.createTextNode(""+isNREToken));
        			subElement.appendChild(childsubElement);
    			}
    			
    			subElement = document.createElement("language");
    			String strlanguage = "";
    			if(language.size() > i && !language.get(i).isEmpty() && language.get(i).get(0) != null) {
    				strlanguage = language.get(i).get(0).language;
    			}
    			subElement.appendChild(document.createTextNode(strlanguage));
    			tokenElement.appendChild(subElement);
    			
    			/** token pre process   Start**/
    			Element querypreprocessElement = document.createElement("token_Preprocess");                    
    			tokenElement.appendChild(querypreprocessElement);
    			
    			if(strLanguage.equals("gu")) {
    				boolean ismatchstopword = GujaratiProcessor.matchstopword(strtoken);
    				subElement = document.createElement("isStopWord_Gu");
        			subElement.appendChild(document.createTextNode(queryPreprocessing.correctSpelling(""+ismatchstopword)));
        			querypreprocessElement.appendChild(subElement);
    			}
    			
    			if(strLanguage.equals("en")) {
    			subElement = document.createElement("correctSpelling_En");
    			subElement.appendChild(document.createTextNode(queryPreprocessing.correctSpelling(strtoken)));
    			querypreprocessElement.appendChild(subElement);
    			
    			subElement = document.createElement("smallcase_En");
    			subElement.appendChild(document.createTextNode(queryPreprocessing.converttolowerCase(strtoken)));
    			querypreprocessElement.appendChild(subElement);
    			
    			subElement = document.createElement("isStopWord_En");
    			subElement.appendChild(document.createTextNode(String.valueOf(queryPreprocessing.isStopWord(strtoken))));
    			querypreprocessElement.appendChild(subElement);
    			}
    			/** tokenpreprocess   End**/
    			

    			/** Morpological process   Start**/
    			
    			if(strLanguage.equals("gu")) {
    				 Element morphologicalElement = document.createElement("Morphological_Gu");                    
    				 tokenElement.appendChild(morphologicalElement);
    				 
    				 String strGUOREN = GujaratiProcessor.matchENToGUAndGUToEN(strtoken,true);
    				 
    				 subElement = document.createElement("translated_to_En_From_Gu");
    				 subElement.appendChild(document.createTextNode(strGUOREN));
    				 morphologicalElement.appendChild(subElement);
    			}
    			
			 if(strLanguage.equals("en")) {
				 Element morphologicalElement = document.createElement("token_morphology_En");                    
				 tokenElement.appendChild(morphologicalElement);
			
				 String morphStem = lexicalAnalysis.stemTokenApacheOpenNLPPorterStemer_MorphologicalAnalysis_En(strtoken);
			
				 subElement = document.createElement("stem_En");
				 subElement.appendChild(document.createTextNode(morphStem));
				 morphologicalElement.appendChild(subElement);
			
			
				 if(syntaxTextList !=null) {
				 subElement = document.createElement("lemma_En");
				 subElement.appendChild(document.createTextNode(syntaxTextList.get(i).getLemma()));
				 morphologicalElement.appendChild(subElement);
				 }
			 }
			    		
			 /** Morpological process   End**/
    			
    			/** synatactic process   Start**/
    			
    			if(strLanguage.equals("gu")) {
    				Element syntacticElement = document.createElement("semantic_Gu");                    
        			tokenElement.appendChild(syntacticElement);
    				Map<String,ArrayList<GujaratiSynsVO>> mapGujaratiVO = GujaratiProcessor.findTheMatchingWord(strtoken);
    				
    				for (Entry<String, ArrayList<GujaratiSynsVO>> entry : mapGujaratiVO.entrySet()) {
    					ArrayList<GujaratiSynsVO> v = entry.getValue();
    					for (int j = 0; j <v.size(); j++) {

    						subElement = document.createElement("synonyms_Gu");
    						subElement.appendChild(document.createTextNode(v.get(j).getSynset_gujarati()));
    						syntacticElement.appendChild(subElement);

    						subElement = document.createElement("concept_Gu");
    						subElement.appendChild(document.createTextNode(v.get(j).getConcept()));
    						syntacticElement.appendChild(subElement);

    						subElement = document.createElement("example_Gu");
    						subElement.appendChild(document.createTextNode(v.get(j).getExample()));
    						syntacticElement.appendChild(subElement);

    						subElement = document.createElement("TDIL_pos_Gu");
    						subElement.appendChild(document.createTextNode(v.get(j).getCat()));
    						syntacticElement.appendChild(subElement);
    					}

    				}
    			}
    			if(strLanguage.equals("en")) {
    				Element syntacticElement = document.createElement("token_semantic_En");                    
        			tokenElement.appendChild(syntacticElement);
    			Entity entity = null;
    			for (Entity list : entityList) {
					if(strtoken.equalsIgnoreCase(list.getName())) {
						entity=list;
						break;
					}
				}
    			Element entityElement = document.createElement("token_entity");
    			syntacticElement.appendChild(entityElement);
    			String entityType = "";
    			String salience = "";
    			String name = "";
    			if(entity != null) {
    				if(entity.getMentions(0) != null) {
    					entityType = entity.getMentions(0).getType().toString();
    				}
    				name = entity.getName();
    				salience = String.valueOf(entity.getSalience());
    			}
    			subElement = document.createElement("entityname_En");
    			subElement.appendChild(document.createTextNode(name));
    			entityElement.appendChild(subElement);
    			
    			subElement = document.createElement("entityType_En");
    			subElement.appendChild(document.createTextNode(entityType));
    			entityElement.appendChild(subElement);
    			
    			subElement = document.createElement("entitysalience_En");
    			subElement.appendChild(document.createTextNode(salience));
    			entityElement.appendChild(subElement);
    			
			/*
			 * Element categoryElement = document.createElement("category");
			 * entityElement.appendChild(categoryElement); categoryList.get(i); subElement =
			 * document.createElement("nameofcategory");
			 * subElement.appendChild(document.createTextNode(salience));
			 * categoryElement.appendChild(subElement);
			 * 
			 * subElement = document.createElement("confidence");
			 * subElement.appendChild(document.createTextNode(salience));
			 * categoryElement.appendChild(subElement);
			 */
    			
    	        Element parsingElement = document.createElement("parsing_of_token_En");
     			syntacticElement.appendChild(parsingElement);    
     			
     			
    			if(!syntaxTextList.get(i).getPartOfSpeech().getAspect().name().equals(syntaxTextList.get(i).getPartOfSpeech().getAspect().ASPECT_UNKNOWN.name())) {
    				subElement = document.createElement("aspect_En");
        			subElement.appendChild(document.createTextNode(syntaxTextList.get(i).getPartOfSpeech().getAspect().name()));
        			parsingElement.appendChild(subElement);
    			}
    			
    			if(!syntaxTextList.get(i).getPartOfSpeech().getCase().name().equals(syntaxTextList.get(i).getPartOfSpeech().getCase().CASE_UNKNOWN.name())) {
    				subElement = document.createElement("case_En");
        			subElement.appendChild(document.createTextNode(syntaxTextList.get(i).getPartOfSpeech().getCase().name()));
        			parsingElement.appendChild(subElement);
    			}
    			
    			if(!syntaxTextList.get(i).getPartOfSpeech().getForm().name().equals(syntaxTextList.get(i).getPartOfSpeech().getForm().FORM_UNKNOWN.name())) {
    				subElement = document.createElement("form_En");
        			subElement.appendChild(document.createTextNode(syntaxTextList.get(i).getPartOfSpeech().getForm().name()));
        			parsingElement.appendChild(subElement);
    			}
    			
    			if(!syntaxTextList.get(i).getPartOfSpeech().getGender().name().equals(syntaxTextList.get(i).getPartOfSpeech().getGender().GENDER_UNKNOWN.name())) {
    				subElement = document.createElement("gender_En");
        			subElement.appendChild(document.createTextNode(syntaxTextList.get(i).getPartOfSpeech().getGender().name()));
        			parsingElement.appendChild(subElement);
    			}
    			
    			if(!syntaxTextList.get(i).getPartOfSpeech().getMood().name().equals(syntaxTextList.get(i).getPartOfSpeech().getMood().MOOD_UNKNOWN.name())) {
    				subElement = document.createElement("mood_En");
        			subElement.appendChild(document.createTextNode(syntaxTextList.get(i).getPartOfSpeech().getMood().name()));
        			parsingElement.appendChild(subElement);
    			}
    			
    			if(!syntaxTextList.get(i).getPartOfSpeech().getNumber().name().equals(syntaxTextList.get(i).getPartOfSpeech().getNumber().NUMBER_UNKNOWN.name())) {
    				subElement = document.createElement("number_En");
        			subElement.appendChild(document.createTextNode(syntaxTextList.get(i).getPartOfSpeech().getNumber().name()));
        			parsingElement.appendChild(subElement);
    			}
    			
    			if(!syntaxTextList.get(i).getPartOfSpeech().getPerson().name().equals(syntaxTextList.get(i).getPartOfSpeech().getPerson().PERSON_UNKNOWN.name())) {
    				subElement = document.createElement("person_En");
        			subElement.appendChild(document.createTextNode(syntaxTextList.get(i).getPartOfSpeech().getPerson().name()));
        			parsingElement.appendChild(subElement);
    			}
    			
    			if(!syntaxTextList.get(i).getPartOfSpeech().getProper().name().equals(syntaxTextList.get(i).getPartOfSpeech().getProper().PROPER_UNKNOWN.name())) {
    				subElement = document.createElement("proper_En");
        			subElement.appendChild(document.createTextNode(syntaxTextList.get(i).getPartOfSpeech().getProper().name()));
        			parsingElement.appendChild(subElement);
    			}
    			
    			if(!syntaxTextList.get(i).getPartOfSpeech().getReciprocity().name().equals(syntaxTextList.get(i).getPartOfSpeech().getReciprocity().RECIPROCITY_UNKNOWN.name())) {
    				subElement = document.createElement("reciprocity_En");
        			subElement.appendChild(document.createTextNode(syntaxTextList.get(i).getPartOfSpeech().getReciprocity().name()));
        			parsingElement.appendChild(subElement);
    			}
    			
    			if(!syntaxTextList.get(i).getPartOfSpeech().getTense().name().equals(syntaxTextList.get(i).getPartOfSpeech().getTense().TENSE_UNKNOWN.name())) {
    				subElement = document.createElement("tense_En");
        			subElement.appendChild(document.createTextNode(syntaxTextList.get(i).getPartOfSpeech().getTense().name()));
        			parsingElement.appendChild(subElement);
    			}
    			
    			if(!syntaxTextList.get(i).getPartOfSpeech().getVoice().name().equals(syntaxTextList.get(i).getPartOfSpeech().getVoice().VOICE_UNKNOWN.name())) {
    				subElement = document.createElement("voice_En");
        			subElement.appendChild(document.createTextNode(syntaxTextList.get(i).getPartOfSpeech().getVoice().name()));
        			parsingElement.appendChild(subElement);
    			}
    			
    			subElement = document.createElement("google_dependencyEdgeParseLabel_En");
    			subElement.appendChild(document.createTextNode(syntaxTextList.get(i).getDependencyEdge().getLabel().name()));
    			parsingElement.appendChild(subElement);
    			
    			subElement = document.createElement("google_dependencyindex_HeadToken_Index_En");
    			int indexHead = syntaxTextList.get(i).getDependencyEdge().getHeadTokenIndex(); 
    			subElement.appendChild(document.createTextNode(""+indexHead));
    			parsingElement.appendChild(subElement);
    			
    			subElement = document.createElement("google_dependencyindex_HeadToken_ParseLabel_En");
    			subElement.appendChild(document.createTextNode(syntaxTextList.get(indexHead).getDependencyEdge().getLabel().name()));
    			parsingElement.appendChild(subElement);
    			
    			subElement = document.createElement("google_dependencyindex_HeadToken_LabelName_En");
    			subElement.appendChild(document.createTextNode(syntaxTextList.get(indexHead).getText().getContent()));
    			parsingElement.appendChild(subElement);
    			
    			
    			Element partofspeechElement = document.createElement("part_of_speech_En");
        		syntacticElement.appendChild(partofspeechElement);    
        			
        		subElement = document.createElement("POS_Type_Name_Googlgl_En");
       			subElement.appendChild(document.createTextNode(syntaxTextList.get(i).getPartOfSpeech().getTag().name()));
       			partofspeechElement.appendChild(subElement);
    			
       			subElement = document.createElement("POS_Type_Name_Stnfd_En");
       			subElement.appendChild(document.createTextNode(stanfordPOC.get(strtoken)));
       			partofspeechElement.appendChild(subElement);
       			
       			LanguageParserStanford languageParserStanford = new LanguageParserStanford(strtoken);
       			Tree tree = languageParserStanford.getConstituency_Stanford_En();
       			Element consituency = document.createElement(tree.value());
       			constituencyTreeXML(tree,document,consituency);
       			
       			Element constituencyElement = document.createElement("token_Constituency_TreePath_stnfd_En");
       			constituencyElement.appendChild(consituency);
        		syntacticElement.appendChild(constituencyElement); 
        		
					/*
					 * dependencyString = languageParserStanford.getDependency_Stanford_En();
					 * DependencyElement = document.createElement("Dependency_En");
					 * dependencydirectmultygraphXML(dependencyString,document,DependencyElement);
					 * syntacticElement.appendChild(DependencyElement);
					 */
        		
    			/** synatactic process   End**/
    			
    			
    			
    			
    			
    			POS[] pos=syntacticAnalysis.findPartsOfSpeechUsingWordnet(strtoken);
    			String strNodeValue = "";
    			/** lexical Analysis   Start**/
    			
    			Element lexicalElement = document.createElement("token_lexical_En");                    
    			tokenElement.appendChild(lexicalElement);
    			
    			strNodeValue = String.valueOf(lexicalAnalysis.isNumeralOperator_LexicalAnalysis_EN(strtoken));
    			subElement = document.createElement("isNumeralOperator_En");
    			subElement.appendChild(document.createTextNode(strNodeValue));
    			lexicalElement.appendChild(subElement);
    			
    			strNodeValue = lexicalAnalysis.getSymbolofNumeralOperator_LexicalAnalysis_EN(strtoken);
    			strNodeValue= strNodeValue==null?"":strNodeValue;
    			subElement = document.createElement("getSymbolofNumeralOperator_En");
    			subElement.appendChild(document.createTextNode(strNodeValue));
    			lexicalElement.appendChild(subElement);
    			
    			strNodeValue = lexicalAnalysis.getWordForAbbreviation_LexicalAnalysis_EN(strtoken);
    			strNodeValue= strNodeValue==null?"":strNodeValue;
    			subElement = document.createElement("getWordForAbbreviation_En");
    			subElement.appendChild(document.createTextNode(strNodeValue));
    			lexicalElement.appendChild(subElement);
    			
    			strNodeValue = String.valueOf(lexicalAnalysis.isSimplePreposition_LexicalAnalysis_EN(strtoken));
    			subElement = document.createElement("isSimplePreposition_En");
    			subElement.appendChild(document.createTextNode(strNodeValue));
    			lexicalElement.appendChild(subElement);
    			
    			strNodeValue = String.valueOf(lexicalAnalysis.isCompoundPreposition_LexicalAnalysis_EN(strtoken));
    			subElement = document.createElement("isCompoundPreposition_En");
    			subElement.appendChild(document.createTextNode(strNodeValue));
    			lexicalElement.appendChild(subElement);
    			
    			strNodeValue = String.valueOf(lexicalAnalysis.isAggregateWord_LexicalAnalysis_EN(strtoken));
    			subElement = document.createElement("isAggregateWord_En");
    			subElement.appendChild(document.createTextNode(strNodeValue));
    			lexicalElement.appendChild(subElement);
    			
    			/** lexical Analysis   End**/
    			 
    			/** semantic Analysis   Start**/
    			Element semanticElement = document.createElement("token_semantic_En");                    
    			tokenElement.appendChild(semanticElement);
    			
    			 
                 Element synonymsElement = document.createElement("token_synonyms_Googl_En");  
                 semanticElement.appendChild(synonymsElement);
                 
                 Map<String,String> synonyms = semanticAnalysis.getSynonymsUsingWordnet(strtoken);
		  			 for(Map.Entry<String,String> entry : synonyms.entrySet())
		  			 {
		  				 subElement = document.createElement("token_synonym_Googl_En");
		  				subElement.setAttribute("POS_Value", entry.getValue());
		  				strNodeValue = entry.getKey();
	        			strNodeValue= strNodeValue==null?"":strNodeValue;
		  				 subElement.appendChild(document.createTextNode(strNodeValue));
		  				 synonymsElement.appendChild(subElement);
		  			 }
		  			 
		  		
		  			Element antonymsElement = document.createElement("token_antonyms_Googl_En");  
                    semanticElement.appendChild(antonymsElement);
                    
                    Map<String,String> antonyms = semanticAnalysis.getAntonymsUsingWordnet(strtoken);
                    for(Map.Entry<String,String> entry : antonyms.entrySet())
                    {
                    	subElement = document.createElement("token_antonym_Googl_En");
                    	subElement.setAttribute("POS_Value", entry.getValue());
                    	strNodeValue = entry.getKey();
            			strNodeValue= strNodeValue==null?"":strNodeValue;
                    	subElement.appendChild(document.createTextNode(strNodeValue));
                    	antonymsElement.appendChild(subElement);
                    }
                    
                    Element hyponymElement = document.createElement("token_hyponym_POS_Googl_En"); 
                    semanticElement.appendChild(hyponymElement);
                    
                    Element hypernymElement = document.createElement("token_hypernym_POS_Googl_En");  
                    semanticElement.appendChild(hypernymElement);
                    
                    if (pos.length > 0) {
     	                  for (int posi = 0; posi < pos.length; posi++) {
     	                     /* if((pos[posi].getLabel()).equals("noun"))
     	                      {*/
     	                    	NounSynset nounSynset;
     	                		NounSynset[] hyponyms;
     	                    	 Synset[] synsets = semanticAnalysis.getHyponymforNounUsingWordnet(strtoken);
     	                    	for (int x = 0; x < synsets.length; x++) {
     	                    		nounSynset = (NounSynset)synsets[x];
     	                    		hyponyms = nounSynset.getHyponyms();
     	                    		
     	                    		for(int j = 0; j < hyponyms.length; j++) {
     	                    			subElement = document.createElement("word_En");
     	                    			strNodeValue = hyponyms[j].getWordForms()[0];
     	                   			    strNodeValue= strNodeValue==null?"":strNodeValue;
     	                    			subElement.appendChild(document.createTextNode(strNodeValue));
     	                    			hypernymElement.appendChild(subElement);
     	                    			
     	                    			subElement = document.createElement("definition_En");
     	                    			strNodeValue = hyponyms[j].getDefinition();
     	                   			    strNodeValue= strNodeValue==null?"":strNodeValue;
     	                    			subElement.appendChild(document.createTextNode(strNodeValue));
     	                    			hypernymElement.appendChild(subElement);
     	                    		}
     	                    	}
     	                    			  
     	                    	 
     	                    	 
     	                    	 synsets = semanticAnalysis.getHypernymforNounUsingWordnet(strtoken);
     	                    	for (int x = 0; x < synsets.length; x++) {
     	                    		nounSynset = (NounSynset)synsets[x];
     	                    		hyponyms = nounSynset.getHyponyms();
     	                    		for(int j = 0; j < hyponyms.length; j++) {
     	                    			subElement = document.createElement("word_En");
     	                    			strNodeValue = hyponyms[j].getWordForms()[0];     	                    			
     	                   			    strNodeValue= strNodeValue==null?"":strNodeValue;
     	                    			subElement.appendChild(document.createTextNode(strNodeValue));
     	                    			hypernymElement.appendChild(subElement);
     	                    			
     	                    			subElement = document.createElement("definition_En");
     	                    			strNodeValue = hyponyms[j].getDefinition();
     	                   			    strNodeValue= strNodeValue==null?"":strNodeValue;
     	                    			subElement.appendChild(document.createTextNode(strNodeValue));
     	                    			hypernymElement.appendChild(subElement);
     	                    		}
     	                    	}
     	                     // }
     	                  }
     	             }
                    
    			/** semantic Analysis   End**/
    			}
    			 
       			
    		}
    		
    		
    		
    		
    		
    		Transformer t = TransformerFactory.newInstance().newTransformer();
    		t.setOutputProperty(OutputKeys.INDENT, "yes");
    		t.setOutputProperty(OutputKeys.METHOD, "xml");
    		t.setOutputProperty("{http://xml.apache.org/xslt}indent-amount",
    				"2");

    		Source source = new DOMSource(document);
    		Result result = new StreamResult(f1);
    		t.transform(source, result);
    		System.out.println("After Writing to XML File");

    	
    	} catch (ParserConfigurationException e) {
    		// TODO Auto-generated catch block
    		e.printStackTrace();
    	} catch (TransformerException e) {
    		// TODO Auto-generated catch block
    		e.printStackTrace();
    	} catch (FileNotFoundException e) {
    		e.printStackTrace();
    	} catch (JWNLException e) {
    		e.printStackTrace();
    	} catch(Exception e){
    		e.printStackTrace();
    	}
    }
		
	/** 
	 * SBARQ             whClause
	 * WHNP              whNounPhrase
	 * WP                pronoun.interrogative
	 * SQ                partialWhClause
	 * NP                nounPhrase
	 * DT                determiner
	 * NNS               noun.plural
	 * IN                preposition
	 * CD                nbrCardinal
	 * 
	 * For more 
	 * URL : > https://sourceforge.net/p/gate/mailman/message/32460482/
	 */
	private static void constituencyTreeXML(Tree tree,Document document,Element childElement) {
		for(int i=0;i<tree.children().length;i++) {
			 if(tree.getChild(i).children().length == 0) {
				 childElement.appendChild(document.createTextNode(tree.getChild(i).value()));
			 } else {
				 Element firstChildElement = document.createElement(tree.getChild(i).value());
				 constituencyTreeXML(tree.getChild(i),document,firstChildElement);    
				 childElement.appendChild(firstChildElement);
			 }
		 }
	}
	
	private static void dependencyTreeXML(SemanticGraph tree,Document document,Element childElement) {
		IndexedWord root = tree.getFirstRoot();
		 Element rootChildElement = document.createElement("root");
		 rootChildElement.appendChild(document.createTextNode(root.toString()));
		 childElement.appendChild(rootChildElement);
		for(int i=0;i<tree.edgeListSorted().size();i++) {
				 Element firstChildElement = document.createElement(tree.edgeListSorted().get(i).getRelation().toString());
				 firstChildElement.setAttribute("Source", tree.edgeListSorted().get(i).getSource().toString());
				 firstChildElement.appendChild(document.createTextNode(tree.edgeListSorted().get(i).getTarget().toString()));
				 childElement.appendChild(firstChildElement);
		 } 
	}
	
	private static void dependencydirectmultygraphXML(SemanticGraph semanticGraph,Document document,Element childElement) {
		 SemanticGraph newSg = new SemanticGraph();
		   if ( ! semanticGraph.getRoots().isEmpty())
			      newSg.setRoot(semanticGraph.getFirstRoot());
		   Element VerticesElement = document.createElement("Vertices");
		   int count = 1;
		   Map<String,String> nodeIdMap  = new HashMap<>();
		   for (IndexedWord index:semanticGraph.vertexListSorted()) {
			   Element firstChildElement = document.createElement("Vertex");
			   Element Child1Element = document.createElement("Id");
		    	 Child1Element.appendChild(document.createTextNode(""+count));
		    	 firstChildElement.appendChild(Child1Element);
		    	 
		    	 Element Child2Element = document.createElement("Value");
		    	 Child2Element.appendChild(document.createTextNode(index.originalText()));
		    	 firstChildElement.appendChild(Child2Element);
		    	 
		    	 Child2Element = document.createElement("parselabel");
		    	 Child2Element.appendChild(document.createTextNode(index.tag()));
		    	 firstChildElement.appendChild(Child2Element);
			
		    	 VerticesElement.appendChild(firstChildElement);
		    	 nodeIdMap.put(index.toString(), count+"");
		    	 count++;
		   }
		   
		    childElement.appendChild(VerticesElement);
		    
		    Element EdgesElement = document.createElement("Edges");
			    for (SemanticGraphEdge edge : semanticGraph.edgeIterable()) {
			    	Element firstChildElement = document.createElement("Edge");
			    	 Element Child1Element = document.createElement("EdgeFromSource");
			    	 Child1Element.appendChild(document.createTextNode(nodeIdMap.get(edge.getSource().toString())));
			    	 firstChildElement.appendChild(Child1Element);
			    	 
			    	 Child1Element = document.createElement("EdgeToDestination");
			    	 Child1Element.appendChild(document.createTextNode(nodeIdMap.get(edge.getTarget().toString())));
			    	 firstChildElement.appendChild(Child1Element);
			    	 
			    	 Child1Element = document.createElement("EdgeValue");
			    	 Child1Element.appendChild(document.createTextNode(edge.getRelation().toString()));
			    	 firstChildElement.appendChild(Child1Element);
			    	 EdgesElement.appendChild(firstChildElement);
			      //newSg.addEdge(edge.getSource(), edge.getTarget(), edge.getRelation(), edge.getWeight(), edge.isExtra());
			    }
			    childElement.appendChild(EdgesElement);
			    
	}
}
