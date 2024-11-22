package main.srcCom.indiaAICC.nlp.analyzer;

import java.net.HttpURLConnection;
import java.net.URL;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;

public class WikiTest
{
	public static void main(String[] arg)
	{
		WikiTest t = new WikiTest();
		try
		{
			StringBuffer sb = t.getContents();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}

    public StringBuffer getContents()
    {
	StringBuffer buffer=new StringBuffer();
	
	try{
		
//		Jwiki jwiki = new Jwiki("elon musk");
//		System.out.println("Title :"+jwiki.getDisplayTitle()); //get title
//		System.out.println("Text : "+jwiki.getExtractText());  //get summary text
//		System.out.println("Image : "+jwiki.getImageURL());    //get image URL
//		
		
		
		String line;
		int responseCode = 0;
		HttpURLConnection connection;
		InputStream input;
		BufferedReader dataInput;
		URL url = new URL("http://en.wikipedia.org/w/api.php?action=query&prop=links&titles=computer&aplimit=51&pllimit=500");
		//URL url = new URL("http://en.wikipedia.org/w/api.php?action=query&origin=*&format=json&generator=search&gsrnamespace=0&gsrlimit=5&gsrsearch='New_England_Patriots");
				//action=parse&page=computer&format=json");
		
			
				//+ "action=query&prop=links&titles=computer");
		//&aplimit=51&pllimit=500
		connection = (HttpURLConnection)url.openConnection();
		connection.setDoOutput(true);
		connection.setRequestMethod("GET");
		responseCode = connection.getResponseCode();
		if (responseCode != HttpURLConnection.HTTP_OK) 
		{
			System.out.println("HTTP response code: " +
			String.valueOf(responseCode));
		}
		buffer = new StringBuffer();
		input = connection.getInputStream();
		dataInput = new BufferedReader(new InputStreamReader(input));
		while ((line = dataInput.readLine()) != null) 
		{
			System.out.println("Line :: " + line);
			buffer.append(line);
			buffer.append("\n");
		}
	} catch (Exception ex) {
		ex.printStackTrace();
	}
	return buffer;
    }
}