/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main.srcCom.indiaAICC.nlp.processor;

/**
 *
 * @author ABCD
 */
public class GujaratiSynsVO {
    
        private String id;
	private String cat;
	private String concept;
	private String example;
	private String synset_gujarati;
	
	 @Override
	public String toString() {
		String str = "ID  :: " + id  +"<BR>" +
					 "CAT :: " + cat +"<BR>" +
					 "CONCEPT :: "+concept +"<BR>" +
					 "EXAMPLE :: "+ example +"<BR>" +
					 "SYNSET-GUJARATI :: "+ synset_gujarati +"<BR>";
		return str;
	}
		
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getCat() {
		return cat;
	}
	public void setCat(String cat) {
		this.cat = cat;
	}
	public String getConcept() {
		return concept;
	}
	public void setConcept(String concept) {
		this.concept = concept;
	}
	public String getExample() {
		return example;
	}
	public void setExample(String example) {
		this.example = example;
	}
	public String getSynset_gujarati() {
		return synset_gujarati;
	}
	public void setSynset_gujarati(String synset_gujarati) {
		this.synset_gujarati = synset_gujarati;
	}
}
