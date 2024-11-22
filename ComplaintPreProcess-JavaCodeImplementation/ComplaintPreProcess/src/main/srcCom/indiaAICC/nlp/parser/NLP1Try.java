package main.srcCom.indiaAICC.nlp.parser;

import com.aliasi.tokenizer.IndoEuropeanTokenizerFactory;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.process.PTBTokenizer;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import opennlp.tools.cmdline.postag.POSModelLoader;
import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSSample;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.tokenize.SimpleTokenizer;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.tokenize.WhitespaceTokenizer;
import opennlp.tools.util.Span;

public class NLP1Try {

    public static void NP1Main() {
    	apacheOpenNLPExample();
		/*
		 * stanfordNLPExample(); lingpipeExamples();
		 */
        findingPartsOfText();
        findingSentences();
        findingPeopleAndThings();
        nameFinderExample();        
        detectingPartsOfSpeechExample();
        extractingRelationshipsExample();       
        
    }

    private static void apacheOpenNLPExample() {
        try (InputStream is = new FileInputStream(               
                new File("D:\\Implementation\\lingual_data\\usefulfiles\\apache bins", "en-token.bin"))) {
            TokenizerModel model = new TokenizerModel(is);
            Tokenizer tokenizer = new TokenizerME(model);
            String tokens[] = tokenizer.tokenize("He lives at 1511 W. Randolph.");
            for (String a : tokens) {
                System.out.print("[" + a + "] ");
            }
            System.out.println();

        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }


    private static void splitMethodDemonstration() {
        String text = "Mr. Smith went to 123 Washington avenue.";
        String tokens[] = text.split("\\s+");
        for (String token : tokens) {
            System.out.println(token);
        }
    }

    private static void findingPartsOfText() {
        //String text = "Mr. Smith went to 123 Washington avenue.";
        String text = "A child likes toys";
        String tokens[] = text.split("\\s+");
        for (String token : tokens) {
            System.out.println(token);
        }
    }
    

    private static void findingPeopleAndThings() {
        String text = "Mr. Smith went to 123 Washington avenue.";
        String target = "Washington";
        int index = text.indexOf(target);
        System.out.println(index);
    }

    private static void nameFinderExample() {
        try {
            String[] sentences = {
                "Tim was a good neighbor. Perhaps not as good a Bob "
                + "Haywood, but still pretty good. Of course Mr. Adam "
                + "took the cake!"};
            Tokenizer tokenizer = SimpleTokenizer.INSTANCE;
            TokenNameFinderModel model = new TokenNameFinderModel(new File(
                    "D:\\Implementation\\lingual_data\\usefulfiles\\apache bins", "en-ner-person.bin"));
            NameFinderME finder = new NameFinderME(model);

            for (String sentence : sentences) {
                // Split the sentence into tokens
                String[] tokens = tokenizer.tokenize(sentence);

                // Find the names in the tokens and return Span objects
                Span[] nameSpans = finder.find(tokens);

                // Print the names extracted from the tokens using the Span data
                System.out.println(Arrays.toString(
                        Span.spansToStrings(nameSpans, tokens)));
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private static void detectingPartsOfSpeechExample() {
        try
        {
        String sentence = "POS processing is useful for enhancing the "
                + "quality of data sent to other elements of a child and people.";
        System.out.println();
        System.out.println("..... child and people......");
//        POSModel model = new POSModelLoader()
//                .load(new File("E:\\Phd in Computer Science\\Implementation\\workimpl\\usefulfiles\\apache bins", "en-pos-maxent.bin"));
//        
    String modelFileName = "en-pos-perceptron.bin";
    //"en-pos-maxent.bin"
        InputStream posStream = new FileInputStream(
            new File(new File("D:\\Implementation\\lingual_data\\usefulfiles\\apache bins"),modelFileName));
        POSModel model = new POSModel(posStream);
       
       // model = new DoccatModel(new FileInputStream( //<co id="qqpp.theModel"/>
     //       new File(modelDirectory,"en-answer.bin"))).getMaxentModel();
     
     
     
        POSTaggerME tagger = new POSTaggerME(model);

        String tokens[] = WhitespaceTokenizer.INSTANCE
                .tokenize(sentence);
        String[] tags = tagger.tag(tokens);

        POSSample sample = new POSSample(tokens, tags);
        String posTokens[] = sample.getSentence();
        String posTags[] = sample.getTags();
        for (int i = 0; i < posTokens.length; i++) {
            System.out.print(posTokens[i] + " - " + posTags[i]);
        }
        System.out.println();

        for (int i = 0; i < tokens.length; i++) {
            System.out.print(tokens[i] + "[" + tags[i] + "] ");
        }
        }catch(FileNotFoundException fe)
        {
            fe.printStackTrace();
        }
        catch(IOException ie)
        {
            ie.printStackTrace();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    
    // Stanford NLP

    
    private static void findingSentences() {
        String paragraph = "The first sentence. The second sentence.";
        Reader reader = new StringReader(paragraph);
        DocumentPreprocessor documentPreprocessor
                = new DocumentPreprocessor(reader);
        List<String> sentenceList = new LinkedList<String>();
        for (List<HasWord> element : documentPreprocessor) {
            StringBuilder sentence = new StringBuilder();
            List<HasWord> hasWordList = element;
            for (HasWord token : hasWordList) {
                sentence.append(token).append(" ");
            }
            sentenceList.add(sentence.toString());
        }
        for (String sentence : sentenceList) {
            System.out.println("stanford :: "+ sentence);
        }

    }
    private static void extractingRelationshipsExample() {
        Properties properties = new Properties();
        properties.put("annotators", "tokenize, ssplit, parse");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(properties);
        Annotation annotation = new Annotation(
                "The meaning and purpose of life is plain to see.");
        pipeline.annotate(annotation);
        pipeline.prettyPrint(annotation, System.out);

    }
}
