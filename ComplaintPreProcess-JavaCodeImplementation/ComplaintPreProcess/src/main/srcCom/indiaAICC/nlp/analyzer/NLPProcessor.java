/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main.srcCom.indiaAICC.nlp.analyzer;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import com.aliasi.tokenizer.IndoEuropeanTokenizerFactory;
import com.google.cloud.language.v1.ClassificationCategory; 
import com.google.cloud.language.v1.ClassifyTextRequest;
import com.google.cloud.language.v1.ClassifyTextResponse;
import com.google.cloud.language.v1.Document;
import com.google.cloud.language.v1.Document.Type;

import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.PTBTokenizer;

import com.google.cloud.language.v1.LanguageServiceClient;
import com.google.cloud.language.v1.Sentiment;
/**
 *
 * @author Axita
 */
public class NLPProcessor {
    public static String processText_Google_En() throws Exception
    {
	      String text = "The two sides also signed agreements worth around USD 50 million that includes setting up of a USD 30 million super specialty hospital after Palestinian President Mahmoud Abbas received Prime Minister Modi in an official ceremony at the presidential compound, also known as Muqata'a, in Ramallah - the Palestinian seat of government. On his part, President Abbas acknowledged that the Indian leadership has always stood by peace in Palestine. Abbas said Palestine is always ready to engage in negotiations to achieve its goal of an independent state. He asked India to facilitate the peace process with Israel. We rely on India's role as an international voice of great standing and weigh through its historical role in the Non-Aligned Movement and in all international forum and its increasingly growing power on the strategic and economic levels, in a way that is conducive to just and desired peace in our region, said President Abbas. The two sides signed agreements worth USD 50 million. The agreement includes setting up of a super speciality hospital worth USD 30 million in Beit Sahur and construction of a centre for empowering women worth USD 5 million. Three agreements in the education sector worth USD 5 million and for procurement of equipment and machinery for the National Printing Press in Ramallah were also signed.";
	      String queryCategory = "";
	      try (LanguageServiceClient language = LanguageServiceClient.create()) {
              
	            // set content to the text string
                  
	            Document doc = Document.newBuilder()
	                .setContent(text)
	                .setType(Type.PLAIN_TEXT)
	                .build();
	            ClassifyTextRequest request = ClassifyTextRequest.newBuilder()
	                .setDocument(doc)
	                .build();
	            // detect categories in the given text
	            ClassifyTextResponse response = language.classifyText(request);
	            for (ClassificationCategory category : response.getCategoriesList()) {
	              System.out.printf("Category name : %s, Confidence : %.3f\n",
	                  category.getName(), category.getConfidence());
	              queryCategory += category.getName()+","; 
	            }
                 text = "Hello, world!";
                 doc = Document.newBuilder()
                        .setContent(text).setType(Type.PLAIN_TEXT).build();

                // Detects the sentiment of the text
                Sentiment sentiment = language.analyzeSentiment(doc).getDocumentSentiment();

                System.out.printf("Hello Text: %s%n", text);
                System.out.printf("Sentiment: %s, %s%n", sentiment.getScore(), sentiment.getMagnitude());
                if(queryCategory.endsWith(",")) {
      	    	  queryCategory=queryCategory.substring(0, queryCategory.length()-1);
      	      	}
	          }
              catch(Exception e)
              {
                  e.printStackTrace();
              }
	      return queryCategory;
	    }
    
    public static String lingpipeTokenize(String text) {
        List<String> tokenList = new ArrayList<>();
        List<String> whiteList = new ArrayList<>();
		/*
		 * String text = "A sample sentence processed \nby \tthe " +
		 * "LingPipe tokenizer.";
		 */
        com.aliasi.tokenizer.Tokenizer tokenizer = IndoEuropeanTokenizerFactory.INSTANCE.
                tokenizer(text.toCharArray(), 0, text.length());
        tokenizer.tokenize(tokenList, whiteList);
        String strToken = "";
        for (String element : tokenList) {
            System.out.print(element + " ");
            strToken+=element+",";
        }
        System.out.println();
        return strToken;
    }
    
    public static String ptbTokenizer(String languageQuery) {
        PTBTokenizer ptb = new PTBTokenizer(
				new StringReader(languageQuery/* "He lives at 1511 W. Randolph." */),
                new CoreLabelTokenFactory(), null);
        String strToken = "";
        while (ptb.hasNext()) {
        	String str = ""+ptb.next();
            System.out.println("stanford :: "+ str);
            strToken +=str+",";
        }
        return strToken;
    }
}
