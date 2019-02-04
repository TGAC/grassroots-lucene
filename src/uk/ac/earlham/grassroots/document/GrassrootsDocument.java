package uk.ac.earlham.grassroots.document;


import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FloatDocValuesField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexableField;

import org.json.simple.JSONObject;


public class GrassrootsDocument {
	static private String GD_BOOST_SUFFIX = "_boost";
	
	protected Document gd_document;
	

	public GrassrootsDocument (JSONObject json_doc) throws IllegalArgumentException {
		gd_document = new Document ();
		
		/*
		 * Add the common fields
		 */
		if (addText (json_doc, "so:name", 4.0f)) {
			if (addText (json_doc, "so:description", 3.0f)) {
				
			} else {
				throw getError (json_doc, "so:description");				
			}
			
		} else {
			throw getError (json_doc, "so:name");
		}
							
	}
	
	public Document getDocument () {
		return gd_document;
	}

	
	public boolean addText (JSONObject json_doc, String key, float boost) {
		boolean success_flag = false;
		String value = (String) json_doc.get (key);
		
		if (value != null) {
			Field f = new TextField (key, value, Field.Store.YES);
			addField (f, boost);
			
			success_flag = true;
		}

		return success_flag;
	}

	
	public void addField (IndexableField field, float boost) {
		gd_document.add (field);
		gd_document.add (new FloatDocValuesField (field.name() + GD_BOOST_SUFFIX, boost));
	}

	
	protected IllegalArgumentException getError (JSONObject json_doc, String error_key) {
		String json = json_doc.toJSONString ();
		
		return new IllegalArgumentException ("No " + error_key + " in " + json);
	}
}

