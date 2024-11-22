package main.srcCom.indiaAICC.nlp.service;

import com.detectlanguage.DetectLanguage;
import com.google.cloud.language.v1.ClassificationCategory;
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
import main.srcCom.indiaAICC.nlp.model.LinguisticModel;
import main.srcCom.indiaAICC.nlp.parser.NLP1Try;
import main.srcCom.indiaAICC.nlp.parser.NLP2Try;
import main.srcCom.indiaAICC.nlp.processor.GujaratiProcessor;
import main.srcCom.indiaAICC.nlp.processor.GujaratiSynsVO;
import main.srcCom.indiaAICC.util.Paths;

import edu.smu.tspell.wordnet.NounSynset;
import edu.smu.tspell.wordnet.Synset;
import edu.smu.tspell.wordnet.WordNetDatabase;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;
import net.didion.jwnl.JWNL;
import net.didion.jwnl.JWNLException;
import net.didion.jwnl.data.POS;



import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class LinguisticService
{
        public static void initializeLinguisticComponents()
        {
        
        }
        public static void processcrimeComplaint(String crimeComplaint)
        {
        	
        	LinguisticModel.processcrimeComplaintAndcreateComponents(crimeComplaint);
		/*
		 * processLanguage(languageQuery);
		 * createIntermediateComponentsFromLanguageQuery(languageQuery);
		 */
        }
        
        public static void processLanguage(String languageQuery) 
        {
        	 String standardQuery = null;
        	// String languageQuery = "do sell purchases did in work good lessthan ceo";
        	 //organize, organizes, and organizing, democracy, democratic, and democratization
        	// languageQuery = "lsited sales of 2005 organizing";
        	// String languageQuery = "sale";


         try
         {
             
             //String strSearch= URLDecoder.decode(languageQuery, "UTF-8");
             
             DetectLanguage.apiKey="e1ec7ac68976be4706d8ffd1a4608c47";
             List<com.detectlanguage.Result> languageSingle =  DetectLanguage.detect(languageQuery);
             languageSingle.forEach(c->{
             System.out.println(c.language);
             });
             
             List<List<com.detectlanguage.Result>> language = DetectLanguage.detect(languageQuery.split(" "));
             language.forEach(c->{
            	 c.forEach(a->{
            		 System.out.println(a.language);	 
            	 });
             });
             
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
                        
            // initialize JWNL (this must be done before JWNL can be used) and create wordnet database
            JWNL.initialize(new FileInputStream(Paths.LINGUISTIC_WORDNET_HELPER));
            System.setProperty("wordnet.database.dir",Paths.LINGUISTIC_WORDNET_DATABASEDIR);
            WordNetDatabase wordnetDatabase = WordNetDatabase.getFileInstance();

            NLPProcessor gnlp = new NLPProcessor();
            System.out.println("GoogleTextAnalyzer.analyzeTextGoogle ..start........... : ");              	              
            //GoogleTextAnalyzer.analyzeTextGoogle(languageQuery,language.get(0).get(0).language);
            System.out.println("GoogleTextAnalyzer.analyzeTextGoogle ..end........... : ");     
            System.out.println("GoogleNLPProcessor ..start........... : ");              	              
           // NLPProcessor.processTextGoogle();
            System.out.println("GoogleNLPProcessor ..end........... : ");              	                              
             
            // Grammar Checker
                
                  System.out.println("Ginger Grammar check..start........... : ");              	              
             boolean isGrammarValid = true;
            isGrammarValid = GrammarChecker.checkGingerGrammar_En(languageQuery);
            System.out.println("isGrammarValid :: " + isGrammarValid);
            System.out.println("Ginger Grammar check ..End........... : ");
            
            

            System.out.println("Language Tool Grammar check..start........... : ");              	              
            isGrammarValid = true;
            //isGrammarValid = LanguageToolGrammarChecker.checkGrammar(languageQuery);            
            System.out.println("isGrammarValid :: " + isGrammarValid);
            System.out.println("Language Tool Grammar check ..End........... : "); 
           
            System.out.println("GrammarChecker ..start........... : ");              	              
            isGrammarValid = true;
           // isGrammarValid = GrammarChecker.checkGrammarBot(languageQuery);
            
            System.out.println("isGrammarValid :: " + isGrammarValid);
            System.out.println("GrammarChecker ..End........... : "); 
            
             
            
            
            System.out.println("LanguageParserStanford.getStanfordAnnotation ..start........... : ");              	  
            //LanguageParserStanford.getStanfordAnnotation(languageQuery);
            System.out.println("LanguageParserStanford.getStanfordAnnotation ..end........... : ");              	  
            
            System.out.println("LanguageParserStanford.getStanfordParseTree ..start........... : ");              	  
            //LanguageParserStanford.getStanfordParseTree(languageQuery);
            System.out.println("LanguageParserStanford.getStanfordParseTree ..end........... : ");     
            
            
             
             QueryPreprocessing queryPreprocessing = new QueryPreprocessing();
             //MorphologicalAnalysis morphologicalAnalysis = new MorphologicalAnalysis();
            // LanguageParserStanford lp = new LanguageParserStanford();
             Analysis lexicalAnalysis = new Analysis();
             SyntacticAnalysis syntacticAnalysis = new SyntacticAnalysis(wordnetDatabase);
             SemanticAnalysis semanticAnalysis = new SemanticAnalysis(wordnetDatabase);
             
             String token;
             System.out.println("...................Lexical Analysis Query Tokenize.............");
             String delimiter = " ";
             List<String> tokens = lexicalAnalysis.tokenizeQueryByDelimiter_LexicalAnalysis_En(languageQuery, delimiter);
             
             for(int i=0;i<tokens.size();i++)
             {
            	 token = tokens.get(i);
                 System.out.println("..................................................................token : "+token);
            	 System.out.println("...................Query Preprocessing.............");
            	 token = queryPreprocessing.converttolowerCase(token);    // Lower case conversion
                 System.out.println("Lowercase ::: " + token); 
                 token = queryPreprocessing.correctSpelling(token);  // Correct Spelling
                 System.out.println("correctSpelling ::: " + token);
                 boolean isStopWord = false;
                 isStopWord = queryPreprocessing.isStopWord(token);   // is Stop word
                 System.out.println("isStopWord ::: " + isStopWord);
             
             
                 System.out.println("...................Syntactic Analysis.............");
                 System.out.println("Syntactic ....findPartsOfSpeechUsingWordnet ::: ");
                 POS[] pos=syntacticAnalysis.findPartsOfSpeechUsingWordnet(token);
                 if (pos.length > 0) {
                     // Loop through and display them all
                     for (int posi = 0; posi < pos.length; posi++) {
                         System.out.println("POS: " + pos[posi].getLabel());
                     }
                 }
                 
                String[] synsetTypeStr = syntacticAnalysis.getWordnetSynsetType(token);
                for(int j=0;j<synsetTypeStr.length;j++)
                {
                    System.out.println("Syntactic ....Wordnet synsetType : " + j + " : " + synsetTypeStr[j]);
                   
                }
                 
                 System.out.println("...................Morphological Analysis.............");
                 //Stemming openNLP's porter stemmer
        			//System.out.println(" :: porter stemmerstem :: " + morphologicalAnalysis.stemTokenApacheOpenNLPPorterStemer(token));      			
        		
        	     //lemmatization
                 String baseMorphToken;
                 if (pos.length > 0) {
                     // Loop through and display them all
                     for (int posi = 0; posi < pos.length; posi++) {
                   	 // baseMorphToken = morphologicalAnalysis.LemmatizeTokenUsingWordnet(pos[posi].getLabel(),token);
                   	 // System.out.println("baseMorphToken Lemmatization::: " + "For " + pos[posi].getLabel() + " : " + baseMorphToken);
                     }
                 }

				/*
				 * System.out.println("...................Lexical Analysis .................");
				 * System.out.println("isNumeralOperator ::: " +
				 * lexicalAnalysis.isNumeralOperator(token));
				 * System.out.println("getSymbolofNumeralOperator ::: " +
				 * lexicalAnalysis.getSymbolofNumeralOperator(token));
				 * System.out.println("getWordForAbbreviation ::: " +
				 * lexicalAnalysis.getWordForAbbreviation(token));
				 * System.out.println("isSimplePreposition ::: " +
				 * lexicalAnalysis.isSimplePreposition(token));
				 * System.out.println("isCompoundPreposition ::: " +
				 * lexicalAnalysis.isCompoundPreposition(token));
				 * System.out.println("isAggregateWord ::: " +
				 * lexicalAnalysis.isAggregateWord(token));
				 */
                 
                 System.out.println("...................Semantic Analysis.............");
//                 System.out.println("get Thesaurus FromBigHugeLabs :::................................ ");
//                 semanticAnalysis.getThesaurusFromBigHugeLabs(token);
                 System.out.println("get Wordnet Thesaurus :::........................................... ");
      			 Map<String, String> synonyms = semanticAnalysis.getSynonymsUsingWordnet(token);
      			 for(int syni=0;syni<synonyms.size();syni++)
      			 {
      				System.out.println("Synonym : " + synonyms.get(syni));
      				
      			 }
      			 Map<String, String> antonyms = semanticAnalysis.getAntonymsUsingWordnet(token);
      			 for(int syni=0;syni<antonyms.size();syni++)
      			 {
      		 		System.out.println("Antonym : " + antonyms.get(syni));
      				
      		     }
                 if (pos.length > 0) {
	                  // Loop through and display them all
	                  for (int posi = 0; posi < pos.length; posi++) {
	                      if((pos[posi].getLabel()).equals("noun"))
	                      {
	                    	  System.out.println("Hyponym for Noun : ");	                      
	                          semanticAnalysis.getHyponymforNounUsingWordnet(token);
	                    	  System.out.println("Hypernym for Noun : ");
	                          semanticAnalysis.getHypernymforNounUsingWordnet(token);
	                      }
//	                      else if((pos[posi].getLabel()).equals("verb"))
//	                      {
//	                    	  System.out.println("Hypernym for Verb : ");
//	                    	  semanticAnalysis.getHypernymforVerbUsingWordnet(token);
//	                      }
	                  }
	             }
             }
            
            //  languageQuery = "listed is sales and cost of first year 2005 for organizing united states tuesday night";
//            System.out.println("GoogleTextAnalyzer.analyzeTextGoogle ..start........... : ");              	              
//            GoogleTextAnalyzer.analyzeTextGoogle(languageQuery);
//            System.out.println("GoogleTextAnalyzer.analyzeTextGoogle ..end........... : ");     
//            
//            System.out.println("LanguageParserStanford.getStanfordAnnotation ..start........... : ");              	  
//            LanguageParserStanford.getStanfordAnnotation(languageQuery);
//            System.out.println("LanguageParserStanford.getStanfordAnnotation ..end........... : ");              	  
//            
//            System.out.println("LanguageParserStanford.getStanfordParseTree ..start........... : ");              	  
//            LanguageParserStanford.getStanfordParseTree(languageQuery);
//            System.out.println("LanguageParserStanford.getStanfordParseTree ..end........... : ");     
            
            
            System.out.println(" NLP1.NP1Main ......start....... : ");              	  
            NLP1Try.NP1Main();
            System.out.println(" NLP1.NP1Main ........end..... : ");              	  
            System.out.println(" NLP2.NP2Main ......start....... : ");              	  
             NLP2Try.NP2Main();
            System.out.println(" NLP2.NP2Main ......end....... : ");              	  
             
//            	IndexWord indexToken = Dictionary.getInstance().getIndexWord(POS.NOUN, token);
// 				semanticAnalysis.getHyponymTreeUsingWordnet(indexToken);
// 				semanticAnalysis.getHypernymListOperationUsingWordnet(indexToken);
  //             System.out.println("getStanfordAnnotation ::: ");
//             lp.getStanfordAnnotation(word);
         } catch (FileNotFoundException e) {
             e.printStackTrace();
         } catch (JWNLException e) {
             e.printStackTrace();
         } catch(Exception e){
             e.printStackTrace();
         }
             standardQuery = languageQuery;
         

        }
	public static void createIntermediateComponentsFromLanguageQuery(String languageQuery) 
        {
        	 String standardQuery = null;
        	// String languageQuery = "do sell purchases did in work good lessthan ceo";
        	 //organize, organizes, and organizing, democracy, democratic, and democratization
        	// languageQuery = "lsited sales of 2005 organizing";
        	// String languageQuery = "sale";
                
            try
            {
                Element rootElement = null;
         
            DocumentBuilderFactory documentBuilderFactory;
            documentBuilderFactory = DocumentBuilderFactory.newInstance();
            Document document = null;

            DocumentBuilder builder = documentBuilderFactory.newDocumentBuilder();
            String intermediateComponentFileStr = Paths.LINGUISTIC_intermediate_Processed_UserRequest;
            File f1 = new File(intermediateComponentFileStr);
            if(f1.exists()) {
                f1.delete();
            }
            //if (!f1.exists()) {
                System.out.println("File not exist create...");
                    document = builder.newDocument();
                    rootElement = document.createElement("userRequest");
                    document.appendChild(rootElement);
           //  } else {
                    /*
                     * DOMParser parser=new DOMParser();
                     * parser.parse("I:/research and phd/phd/phd impl/Workspace 2016/NPISourceProject/xmlfile//Resource.xml"
                     * ); doc = parser.getDocument();
                     */
             /*       try {
                            document = builder
                                            .parse(Paths.LINGUISTIC_intermediate_Processed_UserRequest);
                    } catch (SAXException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                    } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                    }
                    rootElement = document.getDocumentElement();
            }*/
            //LINGUISTIC_intermediate_Processed_UserRequest
            Element childElement, subElement;
//	<isSyntacticCorrect>true</isSyntacticCorrect>
//	<typeofquery>to be question</typeofquery>
        subElement = document.createElement("isSyntacticCorrect");
        subElement.appendChild(document.createTextNode("false"));
        rootElement.appendChild(subElement);
        
        subElement = document.createElement("typeofquery");
        subElement.appendChild(document.createTextNode("keyword query"));
        rootElement.appendChild(subElement);
                
      
//
        

            // initialize JWNL (this must be done before JWNL can be used) and create wordnet database
            System.setProperty("wordnet.database.dir",Paths.LINGUISTIC_WORDNET_DATABASEDIR);
            JWNL.initialize(new FileInputStream(Paths.LINGUISTIC_WORDNET_HELPER));
            WordNetDatabase wordnetDatabase = WordNetDatabase.getFileInstance();
   			
   			
             QueryPreprocessing queryPreprocessing = new QueryPreprocessing();
             //MorphologicalAnalysis morphologicalAnalysis = new MorphologicalAnalysis();
             //LanguageParserStanford lp = new LanguageParserStanford();
             Analysis lexicalAnalysis = new Analysis();
             SyntacticAnalysis syntacticAnalysis = new SyntacticAnalysis(wordnetDatabase);
             SemanticAnalysis semanticAnalysis = new SemanticAnalysis(wordnetDatabase);
             
             String token;
             System.out.println("...................Lexical Analysis Query Tokenize.............");
             String delimiter = " ";
             List<String> tokens = lexicalAnalysis.tokenizeQueryByDelimiter_LexicalAnalysis_En(languageQuery, delimiter);
             
            childElement = document.createElement("tokens");
            rootElement.appendChild(childElement);
        
             for(int i=0;i<tokens.size();i++)
             {
            	 token = tokens.get(i);
                 System.out.println("..................................................................token : "+token);
            	 System.out.println("...................Query Preprocessing.............");
                    Element tokenElement = document.createElement("token");                    
                    childElement.appendChild(tokenElement);
                    
                    subElement = document.createElement("name");
                    subElement.appendChild(document.createTextNode(token));
                    tokenElement.appendChild(subElement);
                    
                     Element querypreprocessElement = document.createElement("querypreprocess");                    
                    tokenElement.appendChild(querypreprocessElement);
                    
            	/* token = queryPreprocessing.converttolowerCase(token);    // Lower case conversion
                 System.out.println("Lowercase ::: " + token); 
                 subElement = document.createElement("lowercase");
                 subElement.appendChild(document.createTextNode(token));
                 querypreprocessElement.appendChild(subElement);
                 
                 token = queryPreprocessing.correctSpelling(token);  // Correct Spelling
                 System.out.println("correctSpelling ::: " + token);
                  subElement = document.createElement("correctSpelling");
                 subElement.appendChild(document.createTextNode(token));
                 querypreprocessElement.appendChild(subElement);
                 
                 boolean isStopWord = false;
                 isStopWord = queryPreprocessing.isStopWord(token);   // is Stop word
                 System.out.println("isStopWord ::: " + isStopWord);
                  subElement = document.createElement("isStopWord");
                 subElement.appendChild(document.createTextNode(String.valueOf(isStopWord)));
                 querypreprocessElement.appendChild(subElement);
             
                Element syntacticElement = document.createElement("syntactic");                    
                tokenElement.appendChild(syntacticElement);
                 System.out.println("...................Syntactic Analysis.............");
                 System.out.println("Syntactic ....findPartsOfSpeechUsingWordnet ::: ");
                Element posElement = document.createElement("partOfSpeech");                    
                syntacticElement.appendChild(posElement);
                 POS[] pos=syntacticAnalysis.findPartsOfSpeechUsingWordnet(token);
                 if (pos.length > 0) {
                     // Loop through and display them all
                     for (int posi = 0; posi < pos.length; posi++) {
                         System.out.println("POS: " + pos[posi].getLabel());
                            subElement = document.createElement("pos");
                            subElement.appendChild(document.createTextNode(pos[posi].getLabel()));
                            posElement.appendChild(subElement);
                     }
                 }
                 
                String[] synsetTypeStr = syntacticAnalysis.getWordnetSynsetType(token);
                for(int j=0;j<synsetTypeStr.length;j++)
                {
                    System.out.println("Syntactic ....Wordnet synsetType : " + j + " : " + synsetTypeStr[j]);
                   
                }
                 
                 System.out.println("...................Morphological Analysis.............");
                Element morphElement = document.createElement("Morphological");                    
                tokenElement.appendChild(morphElement);
                 //Stemming openNLP's porter stemmer
                 String morphStem = morphologicalAnalysis.stemTokenApacheOpenNLPPorterStemer(token);
                 subElement = document.createElement("stem");
                subElement.appendChild(document.createTextNode(morphStem));
                morphElement.appendChild(subElement);
                System.out.println(" :: porter stemmerstem :: " + morphStem);      			
        		
        	     //lemmatization
                 String baseMorphToken;
                 if (pos.length > 0) {
                     // Loop through and display them all
                     Element lemmatizeElement = document.createElement("lemmatize");                    
                    morphElement.appendChild(lemmatizeElement);
                
                
                     for (int posi = 0; posi < pos.length; posi++) {
                   	  baseMorphToken = morphologicalAnalysis.LemmatizeTokenUsingWordnet(pos[posi].getLabel(),token);
                          subElement = document.createElement(pos[posi].getLabel());
                            subElement.appendChild(document.createTextNode(baseMorphToken));
                            lemmatizeElement.appendChild(subElement);
                   	  System.out.println("baseMorphToken Lemmatization::: " + "For " + pos[posi].getLabel() + " : " + baseMorphToken);
                     }
                 }

            	 System.out.println("...................Lexical Analysis .................");
                 Element lexicalElement = document.createElement("lexical");                    
                 tokenElement.appendChild(lexicalElement);
                
                 String valueNode = String.valueOf(lexicalAnalysis.isNumeralOperator(token));
                 subElement = document.createElement("isNumeralOperator");
                subElement.appendChild(document.createTextNode(valueNode));
                lexicalElement.appendChild(subElement);
                System.out.println("isNumeralOperator ::: " +  valueNode);
                
                 valueNode = lexicalAnalysis.getSymbolofNumeralOperator(token);
                 subElement = document.createElement("symbolofNumeralOperator");
                subElement.appendChild(document.createTextNode(valueNode));
                lexicalElement.appendChild(subElement);
                 System.out.println("getSymbolofNumeralOperator ::: " +  valueNode);
                 
                 valueNode = lexicalAnalysis.getWordForAbbreviation(token);
                 subElement = document.createElement("WordForAbbreviation");
                subElement.appendChild(document.createTextNode(valueNode));
                lexicalElement.appendChild(subElement);
                System.out.println("getWordForAbbreviation ::: " +valueNode);
                
                  valueNode = String.valueOf(lexicalAnalysis.isSimplePreposition(token));
                 subElement = document.createElement("isSimplePreposition");
                subElement.appendChild(document.createTextNode(valueNode));
                lexicalElement.appendChild(subElement);
                System.out.println("isSimplePreposition ::: " + valueNode);
                 
                 valueNode = String.valueOf(lexicalAnalysis.isCompoundPreposition(token));
                 subElement = document.createElement("isCompoundPreposition");
                subElement.appendChild(document.createTextNode(valueNode));
                lexicalElement.appendChild(subElement);
                System.out.println("isCompoundPreposition ::: " + valueNode);
                 
                 valueNode = String.valueOf(lexicalAnalysis.isAggregateWord(token));
                 subElement = document.createElement("isAggregateWord");
                subElement.appendChild(document.createTextNode(valueNode));
                lexicalElement.appendChild(subElement);
                System.out.println("isAggregateWord ::: " + valueNode);

                 
                 System.out.println("...................Semantic Analysis.............");
                 Element semanticElement = document.createElement("semantic");                    
                 tokenElement.appendChild(semanticElement);
//                 System.out.println("get Thesaurus FromBigHugeLabs :::................................ ");
//                 semanticAnalysis.getThesaurusFromBigHugeLabs(token);
                 System.out.println("get Wordnet Thesaurus :::........................................... ");
      			 List<String> synonyms = semanticAnalysis.getSynonymsUsingWordnet(token);
                         Element synonymsElement = document.createElement("synonyms");                    
                        semanticElement.appendChild(synonymsElement);
      			 for(int syni=0;syni<synonyms.size();syni++)
      			 {
                             String syn = synonyms.get(syni);
                             subElement = document.createElement("syn");
                                subElement.appendChild(document.createTextNode(valueNode));
                                synonymsElement.appendChild(subElement);
      				System.out.println("Synonym : " + syn);
      				
      			 }
      			 List<String> antonyms = semanticAnalysis.getAntonymsUsingWordnet(token);
                         Element antonumsElement = document.createElement("antonyms");                    
                        semanticElement.appendChild(antonumsElement);
      			 for(int syni=0;syni<antonyms.size();syni++)
      			 {
                             String ant = antonyms.get(syni);
                             subElement = document.createElement("ant");
                                subElement.appendChild(document.createTextNode(valueNode));
                                antonumsElement.appendChild(subElement);
      		 		System.out.println("Antonym : " + ant );
      				
                        }
                 if (pos.length > 0) {
	                  // Loop through and display them all
	                  for (int posi = 0; posi < pos.length; posi++) {
	                      if((pos[posi].getLabel()).equals("noun"))
	                      {
	                    	  System.out.println("Hyponym for Noun : ");	                      
	                          semanticAnalysis.getHyponymforNounUsingWordnet(token);
	                    	  System.out.println("Hypernym for Noun : ");
	                          semanticAnalysis.getHypernymforNounUsingWordnet(token);
	                      }
//	                      else if((pos[posi].getLabel()).equals("verb"))
//	                      {
//	                    	  System.out.println("Hypernym for Verb : ");
//	                    	  semanticAnalysis.getHypernymforVerbUsingWordnet(token);
//	                      }
	                  }
	             }*/
             }
            
//              languageQuery = "listed is sales year organizing ";
            System.out.println("LanguageParserStanford.getStanfordAnnotation ..start........... : ");              	  
            //LanguageParserStanford.getStanfordAnnotation(languageQuery);
            System.out.println("LanguageParserStanford.getStanfordAnnotation ..end........... : ");              	  
            System.out.println(" NLP1.NP1Main ......start....... : ");              	  
            NLP1Try.NP1Main();
            System.out.println(" NLP1.NP1Main ........end..... : ");              	  
            System.out.println(" NLP2.NP2Main ......start....... : ");              	  
             NLP2Try.NP2Main();
            System.out.println(" NLP2.NP2Main ......end....... : ");              	  
             
//            	IndexWord indexToken = Dictionary.getInstance().getIndexWord(POS.NOUN, token);
// 				semanticAnalysis.getHyponymTreeUsingWordnet(indexToken);
// 				semanticAnalysis.getHypernymListOperationUsingWordnet(indexToken);
//             System.out.println("getStanfordAnnotation ::: ");
//             lp.getStanfordAnnotation(word);

        // Writing to XML File
            System.out.println("Before Writing to XML File");
            //document.replaceChild(rootElement, rootElement);

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
             standardQuery = languageQuery;
         

        }
}
