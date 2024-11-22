/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main.srcCom.indiaAICC.nlp.processor;

import java.io.*;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.*;

import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.util.StringUtil;

import main.srcCom.indiaAICC.util.Paths;
/**
 *
 * @author ABCD
 */
public class GujaratiProcessor {
    
    public static String removeNONINUNA(String strSearch){
		String splitString = "";
		try{
			String fileName = "D:\\Added\\TODO\\lingual data\\NONINUNA.txt";
			File file = new File(fileName);
			FileInputStream fis = new FileInputStream(file);
			InputStreamReader isr = new InputStreamReader(fis, StandardCharsets.UTF_8);
			BufferedReader br = new BufferedReader(isr);
			ArrayList<String> strList = new ArrayList<>();
			String line;
			while((line = br.readLine()) != null){
					strList.add(line.trim());
			}
			for (String string : strList) {
				if(strSearch.lastIndexOf(string) != -1) {
					splitString = strSearch.substring(0, strSearch.lastIndexOf(string)-1);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return splitString;
	}
	
	public static String matchENToGUAndGUToEN(String strSearch,boolean isGUmatch){
		String flag = "";
		try{
			String fileName = Paths.GUTOENTRANSLATION;
			//String fileName = "D:\\Added\\TODO\\lingual data\\gu2en_par_sample.txt";
			File file = new File(fileName);
			FileInputStream fis = new FileInputStream(file);
			InputStreamReader isr = new InputStreamReader(fis, StandardCharsets.UTF_8);
			BufferedReader br = new BufferedReader(isr);

			String line;
			while((line = br.readLine()) != null){
				String [] strString = line.split(" ");
				if(isGUmatch) {
					if(strSearch.equals(strString[0].trim())) {
						flag = strString[1].trim();
					}	
				} else {
					if(strSearch.equals(strString[1].trim())) {
						flag = strString[0].trim();
					}
				}
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return flag;
	}
	
	public static boolean matchNREToken(String strSearch){
		boolean flag = false;
		try{
			String fileName = Paths.NER_GU_SAMPLE;
			File file = new File(fileName);
			FileInputStream fis = new FileInputStream(file);
			InputStreamReader isr = new InputStreamReader(fis, StandardCharsets.UTF_8);
			BufferedReader br = new BufferedReader(isr);

			String line;
			while((line = br.readLine()) != null){
				if(strSearch.equals(line)) {
					flag = true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return flag;
	}
	
	public static boolean matchstopword(String strSearch){
		boolean flag = false;
		try{
			String fileName = Paths.STP_WORDS_GU_SAMPLE_TEMP;
			File file = new File(fileName);
			FileInputStream fis = new FileInputStream(file);
			InputStreamReader isr = new InputStreamReader(fis, StandardCharsets.UTF_8);
			BufferedReader br = new BufferedReader(isr);

			String line;
			while((line = br.readLine()) != null){
				if(strSearch.equals(line)) {
					flag = true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return flag;
	}
	
	public static Map<String,ArrayList<GujaratiSynsVO>> findTheMatchingWord(String strSearch) {
		Map<String,ArrayList<GujaratiSynsVO>> mapGujaratiVO = new HashMap<>();
		List<GujaratiSynsVO> lstGujaratiVO = createGujaratiVO();
		String[] strArr = strSearch.split(" ");
		for (int j = 0; j < strArr.length; j++) {
			String strMatch = strArr[j];
			ArrayList<GujaratiSynsVO> searchGujaratiVO = null;
			for (int i = 0; i < lstGujaratiVO.size(); i++) {
				if(containsWordsPatternMatch(lstGujaratiVO.get(i).getSynset_gujarati(),strMatch)) {
					if(searchGujaratiVO == null) {
						searchGujaratiVO = new ArrayList<>();
					}
					searchGujaratiVO.add(lstGujaratiVO.get(i));
				}
			}
			if(searchGujaratiVO != null && !searchGujaratiVO.isEmpty()) {
				mapGujaratiVO.put(strMatch, searchGujaratiVO);
			}
		}
		return mapGujaratiVO;
	}

public static boolean containsWordsPatternMatch(String inputString, String word) {
	StringBuilder regexp = new StringBuilder();
	regexp.append("(?=.*").append(word).append(")");
	Pattern pattern = Pattern.compile(regexp.toString());
	return pattern.matcher(inputString).find();
}
	/*public static Map<String,ArrayList<GujaratiSynsVO>> findTheMatchingWord(String strSearch) {
		Map<String,ArrayList<GujaratiSynsVO>> mapGujaratiVO = new HashMap<>();
		List<GujaratiSynsVO> lstGujaratiVO = createGujaratiVO();
		String[] strArr = strSearch.split(" ");
		for (int i = 0; i < strArr.length; i++) {
			String strMatch = strArr[i];
			int lenghtStr = strMatch.length();
			ArrayList<GujaratiSynsVO> searchGujaratiVO = null;
			for (int j = 0; j < lenghtStr; j++) {
				searchGujaratiVO = stringMatchwithGujaratiSynsVo(strMatch.substring(0, lenghtStr-j),lstGujaratiVO);
				if(searchGujaratiVO != null && !searchGujaratiVO.isEmpty()) {
					mapGujaratiVO.put(strMatch, searchGujaratiVO);
					break;
				}
			}
		}
		return mapGujaratiVO;
	}*/
	
	
	public static ArrayList<GujaratiSynsVO> stringMatchwithGujaratiSynsVo(String strMatch,List<GujaratiSynsVO> lstGujaratiVO) {
		ArrayList<GujaratiSynsVO> searchGujaratiVO = new ArrayList<>();
		for (int i = 0; i < lstGujaratiVO.size(); i++) {
			GujaratiSynsVO g = lstGujaratiVO.get(i);
			if(g.getSynset_gujarati().contains(strMatch)) {
				searchGujaratiVO.add(g);
			}
		}

		return searchGujaratiVO;
	}
    
	public static List<GujaratiSynsVO> createGujaratiVO(){
		List<GujaratiSynsVO> lstGujaratiVO = new ArrayList<>();
		try{
			String fileName = Paths.GUJARATI_SYNS;
			File file = new File(fileName);
			FileInputStream fis = new FileInputStream(file);
			InputStreamReader isr = new InputStreamReader(fis, StandardCharsets.UTF_8);
			BufferedReader br = new BufferedReader(isr);

			String line;
			GujaratiSynsVO gujaratiSynsVO = null;
			
			while((line = br.readLine()) != null){
				if(line.startsWith("ID")) {
					gujaratiSynsVO = new GujaratiSynsVO();
					gujaratiSynsVO.setId((line.substring(line.trim().indexOf("::")+2, line.length())).trim());
				} else if(line.startsWith("CAT")) {
					gujaratiSynsVO.setCat((line.substring(line.trim().indexOf("::")+2, line.length())).trim());
				} else if(line.startsWith("CONCEPT")) {
					gujaratiSynsVO.setConcept((line.substring(line.trim().indexOf("::")+2, line.length())).trim());
				} else if(line.startsWith("EXAMPLE")) {
					gujaratiSynsVO.setExample((line.substring(line.trim().indexOf("::")+2, line.length())).trim());
				} else if(line.startsWith("SYNSET-GUJARATI")) {
					gujaratiSynsVO.setSynset_gujarati((line.substring(line.trim().indexOf("::")+2, line.length())).trim());
					lstGujaratiVO.add(gujaratiSynsVO);
				}
			}
			br.close();
			
			} catch (Exception e) {
				e.printStackTrace();
			}
		return lstGujaratiVO;
	}
}
