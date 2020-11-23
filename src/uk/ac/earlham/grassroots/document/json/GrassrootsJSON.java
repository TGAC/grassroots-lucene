package uk.ac.earlham.grassroots.document.json;


import java.util.Map;

import org.apache.lucene.document.Document;

import org.json.simple.JSONObject;


public class GrassrootsJSON {
	protected JSONObject gj_json;
	
	
	public GrassrootsJSON (Document doc, Map <String, String []> highlights) {
		
	}
	
	
	public JSONObject getAsJSON () {
		return gj_json;
	}
	
	public boolean addToJSON (Document doc, Map <String, String []> highlights) {
		boolean b = false;
		
		return b;
	}
	
	
	protected boolean addJSONField (Document doc, String input_key, String output_key) {
		boolean b = false;
		String value = doc.get (input_key);
		
		if (value != null) {
			gj_json.put (output_key, value);
			b = true;
		}
		
		return b;
	}
}
