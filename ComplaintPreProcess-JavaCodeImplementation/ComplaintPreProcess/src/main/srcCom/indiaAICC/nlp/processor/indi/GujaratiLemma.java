/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main.srcCom.indiaAICC.nlp.processor.indi;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import main.srcCom.indiaAICC.util.Paths;

/**
 *
 * @author Axita
 */
public class GujaratiLemma {
    public static void main(String[] args)
    {
        List<String> wordlist = new ArrayList<String>();
        String query = "લસણના મુળમાં થતા રોગના નિયંત્રણ માટે શું કરવું ? ";        
        String file = Paths.LINGUISTIC_GUJ_WORDNET_FILE; // provide guj. wordnet file
		try {
			BufferedReader readFile = new BufferedReader(new InputStreamReader(new FileInputStream(file),"UTF8"));
                        String strLine;
                        //while ((strLine = readFile.readLine()) != null) {
                        for(int cnt=0;cnt<7;cnt++)
                        {
                            strLine = readFile.readLine();
                            System.out.println(strLine);
                                
				//
			}
                        System.out.println("Query :: " + query);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
    }
}
