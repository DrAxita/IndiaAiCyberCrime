package main.srcCom.indiaAICC;

import java.util.StringTokenizer;

import main.srcCom.indiaAICC.nlp.preprocessing.QueryPreprocessingLib;
import main.srcCom.indiaAICC.nlp.service.LinguisticService;

public class CrimeComplaintPreProcessing {
	public static void main(String[] args)
	{
		QueryPreprocessingLib qpLib = new QueryPreprocessingLib();
		String inputText= new String("Sir I want to inform You that on  Feb  a transaction of  two times  was debited from my account whose transaction number  at darjling atm WDD sir I beg to inform you that this transaction was ");
		
		
		/*
		 * while (till the end of the file) {
		 *  inputText = Read the csv file and pass .nextline
		 * string one by one to the below function
		 * LinguisticService.processcrimeComplaint(inputText); }
		 */
		LinguisticService.processcrimeComplaint(inputText); 
		
		// Pre-Process the crime complaint and generate the xml files....
		
		
		/*StringTokenizer st = new StringTokenizer(inputText);
		Analysis lexicalAnalysis = new Analysis();
		// String Tokenize by space...
        while(st.hasMoreTokens())
        {
            String token = st.nextToken();
            token = qpLib.converttolowerCase(token);    // Lower case conversion
            System.out.println("Lowercase ::: " + token); 
            if(!qpLib.isStopWord(token)) // if the token is not the stop word 
            {
            	processedInputString = processedInputString + token;
            	System.out.println("Not the stop word...");
            }
            if(lexicalAnalysis.isNumeralOperator_LexicalAnalysis_EN(token))
            {
                System.out.println("NUMERAL");
            }
            if(lexicalAnalysis.isSimplePreposition_LexicalAnalysis_EN(token))
            {
            	System.out.println("SIMPLEPREPOSITION");
            }
            if(lexicalAnalysis.isCompoundPreposition_LexicalAnalysis_EN(token))
            {
            	System.out.println("COMPLEXPREPOSITION");
            }
            token = qpLib.correctSpelling(token);
        }*/
	}
}
