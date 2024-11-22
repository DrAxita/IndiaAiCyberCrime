/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main.srcCom.indiaAICC.nlp.analyzer;

import com.grammarbot.client.GrammarBotBuilder;
import com.grammarbot.client.GrammarBotClient;
import com.grammarbot.client.model.GrammarBotResponse;
import com.grammarbot.client.model.Matches;
import com.grammarbot.client.model.Rule;
import main.srcCom.indiaAICC.nlp.processor.Ginger4J;

import java.util.List;
import org.json.JSONObject;
import org.languagetool.JLanguageTool;
import org.languagetool.language.AmericanEnglish;
import org.languagetool.language.BritishEnglish;
import org.languagetool.rules.RuleMatch;
import org.languagetool.rules.spelling.SpellingCheckRule;

/**
 *
 * @author Axita
 */
public class GrammarChecker {
	GrammarBotResponse gbResponse;
	
	public GrammarChecker(String text) {
		 GrammarBotBuilder grammarBuilder = new GrammarBotBuilder();
	        grammarBuilder.setApiKey("KS9C5N3Y");
	        grammarBuilder.setBaseURI("http://api.grammarbot.io:80");
	        grammarBuilder.setLanguage("en-us");
	        GrammarBotClient botClient = grammarBuilder.build();
	        this.gbResponse = botClient.check(text);
	}
	
    public boolean checkGrammarBot_En()
    {
        boolean isGrammarValid = true;
        try
        {
       
        List<Matches> matches = this.gbResponse.getMatches();
        for (Matches matchedObj : matches) {
            Rule rule = matchedObj.getRule();
            if(rule.getCategory().equals("Capitalization") ||
                    rule.getIssueType().equals("typographical") ||
                    rule.getIssueType().equals("misspelling"))
                isGrammarValid = true;
            else
            {
                isGrammarValid = false;
                return isGrammarValid;
            }
        }
        }
        catch(Exception e)
        {
            e.printStackTrace();           
        }
         return isGrammarValid;
    }
    
    public String boatResponse_En() {
    	 //System.err.println("GrammarBotResponse :: " + this.gbResponse.toString());
    	 return this.gbResponse.getSoftware().getPremiumHint();
    }
    
     public static boolean checkGingerGrammar_En(String text)
    {
        boolean isGrammarValid = true;
        try
        {
         Ginger4J ginger = new Ginger4J();
        JSONObject result = ginger.parse(text);

        // Pretty print the result
        System.out.println(result.toString(4));
        if(result.toString(4).equals("False")) {
        	isGrammarValid = false;
        }
        // Get the correct phrase
        String correctPhrase = ginger.getResult();
        System.out.println("checkGingerGrammar :: " + correctPhrase);
               }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return isGrammarValid;
    }
     
     public static boolean languageToolGrammarChecker_En(String text)
     {
         boolean isGrammarValid = true;
         try
         {
         JLanguageTool langTool = new JLanguageTool(new BritishEnglish());
         // comment in to use statistical ngram data:
         //langTool.activateLanguageModelRules(new File("/data/google-ngram-data"));
         List<RuleMatch> matches = langTool.check(text);
         for (RuleMatch match : matches) {
           System.out.println("Potential error at characters " +
               match.getFromPos() + "-" + match.getToPos() + ": " +
               match.getMessage());
           System.out.println("Suggested correction(s): " +
               match.getSuggestedReplacements());
         }
         JLanguageTool lt = new JLanguageTool(new AmericanEnglish());
         for (org.languagetool.rules.Rule rule : lt.getAllActiveRules()) {
             System.out.println("Language Tool rule" + rule.toString());
           if (rule instanceof SpellingCheckRule) {
//             List<String> wordsToIgnore = Arrays.asList("specialword", "myotherword");
//             ((SpellingCheckRule)rule).addIgnoreTokens(wordsToIgnore);
//           }else if(rule instanceof GrammalecteRule)
//           {
//               isGrammarValid=false;
//               return false;
           }
           else
               isGrammarValid=false;
                 break;
         }
         }catch(Exception e)
         {
             e.printStackTrace();            
         }
         return isGrammarValid;
     }
}
