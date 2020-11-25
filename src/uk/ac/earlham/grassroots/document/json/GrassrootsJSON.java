package uk.ac.earlham.grassroots.document.json;


import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.lucene.document.Document;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import uk.ac.earlham.grassroots.document.lucene.GrassrootsDocument;


public class GrassrootsJSON {
	protected JSONObject gj_json;
	private Map <String, String []> gj_highlights;
	private int gj_highlight_index;
	
    static private Pattern gj_highlighted_pattern = Pattern.compile ("<b>(\\S+)</b>");

	public GrassrootsJSON (Document doc, Map <String, String []> highlights, int highlight_index) {
		gj_json = new JSONObject ();
		gj_highlights = highlights; 
		gj_highlight_index = highlight_index;
		
		addToJSON (doc);
	}
	
	
	public JSONObject getAsJSON () {
		return gj_json;
	}
	
	public boolean addToJSON (Document doc) {
		boolean b = false;

		String id = getIdKey ();
		
		if (id != null) {
			addJSONField (doc, GrassrootsDocument.GD_LUCENE_ID, GrassrootsDocument.GD_LUCENE_ID, false);
			addJSONField (doc, GrassrootsDocument.GD_LUCENE_ID, id, false);

			addJSONField (doc, GrassrootsDocument.GD_UNIQUE_NAME, GrassrootsDocument.GD_UNIQUE_NAME);

			addJSONField (doc, GrassrootsDocument.GD_DESCRIPTION, GrassrootsDocument.GD_DESCRIPTION);
			addJSONField (doc, GrassrootsDocument.GD_PRIVATE_TYPE, GrassrootsDocument.GD_PRIVATE_TYPE, false);
			addJSONField (doc, GrassrootsDocument.GD_ICON, GrassrootsDocument.GD_ICON, false);
			addJSONField (doc, GrassrootsDocument.GD_TYPE_DESCRIPTION, GrassrootsDocument.GD_TYPE_DESCRIPTION, false);
			
			b = true;
		}	
		
		return b;
	}
	
	
	public String getIdKey () {
		return GrassrootsDocument.GD_LUCENE_ID;
	}

	
	protected String getHighlightedValue (String doc_value, String key) {
		String value = null;
		String [] values = gj_highlights.get (key);
		
		if (values != null) {
			String highlighted_value = values [gj_highlight_index];
			
			if (highlighted_value != null) {
	    		Matcher matcher = GrassrootsJSON.gj_highlighted_pattern.matcher (highlighted_value);
					    		
				if (matcher.find ()) {
		    		String s = highlighted_value.replaceAll ("<b>", "").replaceAll ("</b>", "");

		    		value = doc_value.replace (s, highlighted_value);
				}
			}
		}
		
		return value;
	}

	
	protected boolean addJSONField (Document doc, String input_key, String output_key, boolean do_highlighting) {
		boolean b = false;
		String value = doc.get (input_key);
				
		if ((do_highlighting == true) && (gj_highlights != null)) {
			String highlighted_value = getHighlightedValue (value, input_key);
			
			if (highlighted_value != null) {
				value = highlighted_value;
			}
		}
		
		
		if (value != null) {
			gj_json.put (output_key, value);
			b = true;
		}
		
		return b;
	}


	
	protected boolean addJSONField (Document doc, String input_key, String output_key) {
		return addJSONField (doc, input_key, output_key, true);
	}
	
	
	protected boolean addJSONMultiValuedField (Document doc, String input_key, String output_key, boolean do_highlighting) {
		boolean b = false;
		String [] values = doc.getValues (input_key);
		
		if (values != null) {
			JSONArray json_values = new JSONArray ();
			
			for (String value : values) {
				if ((do_highlighting == true) && (gj_highlights != null)) {
					String highlighted_value = getHighlightedValue (value, input_key);
					
					if (highlighted_value != null) {
						value = highlighted_value;
					}
				}
								
				json_values.add (value);
			}
			
			gj_json.put (output_key, json_values);
			b = true;
		}
		
		return b;
	}

	
	protected boolean addJSONMultiValuedField (Document doc, String input_key, String output_key) {
		return addJSONMultiValuedField (doc, input_key, output_key, true); 
	}
	
	
	protected boolean addJSONObject (String key, JSONObject obj) {
		gj_json.put (key, obj);
		
		return true;
	}


}
