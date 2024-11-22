/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main.srcCom.indiaAICC.nlp.analyzer;


import edu.stanford.nlp.coref.CorefCoreAnnotations;
import edu.stanford.nlp.coref.CorefCoreAnnotations.CorefChainAnnotation;
import edu.stanford.nlp.coref.data.CorefChain;
import edu.stanford.nlp.ie.util.RelationTriple;
import edu.stanford.nlp.io.IOUtils;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreAnnotations.NamedEntityTagAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeCoreAnnotations;
import edu.stanford.nlp.trees.TreeCoreAnnotations.TreeAnnotation;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.util.PropertiesUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 *
 * @author Axita
 */
public class LanguageParserStanford {
	PrintWriter out = new PrintWriter(System.out);
	Annotation annotation;
	public LanguageParserStanford() {
		
	}
	public LanguageParserStanford(String qry) {
		Properties props = new Properties();
		props.setProperty("annotators", "tokenize");
		StanfordCoreNLP pipeline = new StanfordCoreNLP(
				PropertiesUtils.asProperties(
						"annotators", "tokenize,ssplit,pos,lemma,parse,natlog",
						"ssplit.isOneSentence", "true",
						"parse.model", "edu/stanford/nlp/models/srparser/englishSR.ser.gz",
						"tokenize.language", "en"));
		//tokenize,ssplit,pos,lemma,ner,parse,coref,kbp
		annotation  = new Annotation(qry);
		pipeline.annotate(annotation);
		pipeline.prettyPrint(annotation, out);
		out.println("The top level annotation");
		out.println(annotation.toShorterString());
		out.println();
	}
	
	public void getSubject_Stanford_En(String qry) {
	  StanfordCoreNLP pipeline = new StanfordCoreNLP(PropertiesUtils.asProperties(
			  "annotators", "tokenize,ssplit,pos,lemma,ner,regexner,parse,mention,coref,kbp",
				"regexner.mapping", "ignorecase=true,validpospattern=^(NN|JJ).*,edu/stanford/nlp/models/kbp/regexner_caseless.tab;edu/stanford/nlp/models/kbp/regexner_cased.tab"));
	  
	  try {
		IOUtils.console("sentence> ", line -> {
		    Annotation ann = new Annotation(line);
		    pipeline.annotate(ann);
		    for (CoreMap sentence : ann.get(CoreAnnotations.SentencesAnnotation.class)) {
		      sentence.get(CoreAnnotations.KBPTriplesAnnotation.class).forEach(System.err::println);
		      System.out.println(sentence);
		    }
		  });
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	}
/*	public void getSubject_Stanford_En(String qry) {
		
		StanfordCoreNLP pipeline = new StanfordCoreNLP(
				  PropertiesUtils.asProperties(
						  "annotators", "tokenize,ssplit,pos,lemma,parse,kbp",
						  "ssplit.isOneSentence", "true",
						  "parse.model", "edu/stanford/nlp/models/srparser/englishSR.ser.gz",
						  "tokenize.language", "en"));

		  // read some text in the text variable    
		  System.out.println("My dog also likes eating sausage...");
		  // create an empty Annotation just with the given text  
		  Annotation document = new Annotation(qry);
		  System.out.println("After Annotation");
		  // run all Annotators on this text  
		  pipeline.annotate(document);
		  System.out.println("Stanford Parse Tree start...");
		
		 System.out.println("---");
	       System.out.println("kbp relations\n");
	       for (CoreMap sentence : annotation.get(CoreAnnotations.SentencesAnnotation.class)) {
	         for (RelationTriple rt : sentence.get(CoreAnnotations.KBPTriplesAnnotation.class)) {
	           System.out.println("\t"+rt);
	         }
	       }
	}*/
	
	public Tree getConstituency_Stanford_En() {
		Tree tree=null;
		List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
		  if (sentences != null && ! sentences.isEmpty()) {
			  CoreMap sentence = sentences.get(0);
		     tree = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);
		     //consituency = tree.toString();//.getChild(0).getChild(0).getChild(0).getChild(0)
		  }
		 return tree;
	}

	
	
	public SemanticGraph getDependency_Stanford_En() {
		SemanticGraph tree = null;
		List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
		  if (sentences != null && ! sentences.isEmpty()) {
			  CoreMap sentence = sentences.get(0);
			   tree = sentence.get(CollapsedCCProcessedDependenciesAnnotation.class);
		     //consituency = tree.toString();
			  System.out.println(tree.toString());
		  }
		 return tree;
	}
	
      
      public static CoreMap getStanfordParseTree_Stanford_En(String qry)
      {
    	  CoreMap stanfordCoreMap = null;
    	  try
    	  {
    		  StanfordCoreNLP pipeline = new StanfordCoreNLP(
    				  PropertiesUtils.asProperties(
    						  "annotators", "tokenize,ssplit,pos,lemma,parse",
    						  "ssplit.isOneSentence", "true",
    						  "parse.model", "edu/stanford/nlp/models/srparser/englishSR.ser.gz",
    						  "tokenize.language", "en"));

    		  // read some text in the text variable    
    		  System.out.println("My dog also likes eating sausage...");
    		  // create an empty Annotation just with the given text  
    		  Annotation document = new Annotation(qry);
    		  System.out.println("After Annotation");
    		  // run all Annotators on this text  
    		  pipeline.annotate(document);
    		  System.out.println("Stanford Parse Tree start...");
    		  // these are all the sentences in this document
    		  // a CoreMap is essentially a Map that uses class objects as keys and has values with custom types    
    		  List < CoreMap > sentences = document.get(SentencesAnnotation.class);
    		  for (CoreMap sentence: sentences) { 
    			  // traversing the words in the current sentence       
    			  // a CoreLabel is a CoreMap with additional token-specific methods       
    			  for (CoreLabel token: sentence.get(TokensAnnotation.class)) {         
    				  // this is the text of the token         
    				  String word = token.get(TextAnnotation.class);         
    				  // this is the POS tag of the token        
    				  String pos = token.get(PartOfSpeechAnnotation.class);        
    				  // this is the NER label of the token         
    				  String ne = token.get(NamedEntityTagAnnotation.class);      
    				  System.out.println("Word : "+word+"  == >> pos : == >> "+pos+" = > ne  : == >> "+ne);
    			  }        
    			  // this is the parse tree of the current sentence       
    			  Tree tree = sentence.get(TreeAnnotation.class); 
    			  //score = tree.score();
    			  System.out.println("parse tree score : " + tree.score());
    			  List<Tree> leaves = tree.getLeaves();
    			  // Print words and Pos Tags
    			  for (Tree leaf : leaves) { 
    				  Tree parent = leaf.parent(tree);
    				  System.out.print(leaf.label().value() + "-" + parent.label().value() + " ");
    			  }
    			  stanfordCoreMap = sentence;
    			  System.out.println();  
    		  }  
    		  System.out.println("Stanford Parse Tree end...");

    	  }catch(Exception e)
    	  {
    		  e.printStackTrace();
    	  }
    	  return stanfordCoreMap;
      }

}
