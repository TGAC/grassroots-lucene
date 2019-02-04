package uk.ac.earlham.grassroots.document;


import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FloatDocValuesField;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexableField;

import org.json.simple.JSONObject;


public class GrassrootsDocument {
	static private String GD_BOOST_SUFFIX = "_boost";
	static private String GD_MONGO_ID = "_id";

	protected Document gd_document;
	

	public GrassrootsDocument (JSONObject json_doc) throws IllegalArgumentException {
		gd_document = new Document ();
		
		/*
		 * Add the common fields
		 */
		addText (json_doc, "so:name", 4.0f);
		addText (json_doc, "so:description", 3.0f);
		addNonIndexedString (json_doc, GD_MONGO_ID);
	}

	
	public Document getDocument () {
		return gd_document;
	}

	
	public boolean addText (JSONObject json_doc, String key, float boost) throws IllegalArgumentException {
		boolean success_flag = false;
		String value = (String) json_doc.get (key);
		
		if (value != null) {
			Field f = new TextField (key, value, Field.Store.YES);
			addField (f, boost);
			
			success_flag = true;
		} else {
			String json = json_doc.toJSONString ();
			
			throw new IllegalArgumentException ("No " + key + " in " + json);
		}

		return success_flag;
	}

	
	public boolean addString (JSONObject json_doc, String key, float boost) throws IllegalArgumentException {
		boolean success_flag = false;
		String value = (String) json_doc.get (key);
		
		if (value != null) {
			Field f = new StringField (key, value, Field.Store.YES);
			addField (f, boost);
			
			success_flag = true;
		} else {
			String json = json_doc.toJSONString ();
			
			throw new IllegalArgumentException ("No " + key + " in " + json);
		}

		return success_flag;
	}

	
	public boolean addNonIndexedString (JSONObject json_doc, String key) throws IllegalArgumentException {
		boolean success_flag = false;
		String value = (String) json_doc.get (key);
		
		if (value != null) {
			Field f = new StoredField (key, value);
			gd_document.add (f);
			
			success_flag = true;
		} else {
			String json = json_doc.toJSONString ();
			
			throw new IllegalArgumentException ("No " + key + " in " + json);
		}

		return success_flag;
	}
	
	
	public void addField (IndexableField field, float boost) {
		gd_document.add (field);
		gd_document.add (new FloatDocValuesField (field.name() + GD_BOOST_SUFFIX, boost));
	}

}

