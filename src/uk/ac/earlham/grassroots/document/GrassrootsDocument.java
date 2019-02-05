package uk.ac.earlham.grassroots.document;


import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FloatDocValuesField;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;

import org.apache.lucene.facet.FacetField;

import org.apache.lucene.index.IndexableField;

import org.json.simple.JSONObject;


/**
 * The base class for all of the Grassroots datatypes that we'll be storing in Lucene
 * 
 * @author billy
 */
abstract public class GrassrootsDocument {
	static private String GD_BOOST_SUFFIX = "_boost";
	static private String GD_MONGO_ID = "_id";
	static public String GD_DATATYPE = "type";
	
	protected Document gd_document;
	
	/**
	 * Create a new GrassrootsDocument
	 * 
	 * @param json_doc The Grassroots JSON document to pull the data from.
	 * @throws IllegalArgumentException If the Grassroots JSON document does not contain the required keys.
	 */
	public GrassrootsDocument (JSONObject json_doc) throws IllegalArgumentException {
		gd_document = new Document ();
		
		/*
		 * Add the common fields
		 */
		addText (json_doc, getNameKey (), 4.0f);
		addText (json_doc, "so:description", 3.0f);
		addNonIndexedString (json_doc, GD_MONGO_ID);
		
		FacetField type_facet = new FacetField (GD_DATATYPE, getUserFriendlyTypename ());
		gd_document.add (type_facet);
		
	}

	
	/**
	 * Get the underlying Lucene document.
	 * 
	 * @return The Lucene document
	 */
	public Document getDocument () {
		return gd_document;
	}


	/**
	 * Add a TextField to the Lucene document with a given boost value for search scoring.
	 * 
	 * @param json_doc The Grassroots JSON document to pull the data from.
	 * @param key The key within the given Grassroots JSON document to get the value for.
	 * @param boost The boost value that will be used for this field when searching.
	 * @return <code>true</code> if the Field was added to the underlying Lucene document successfully, 
	 * <code>false</code> otherwise.
	 * @throws IllegalArgumentException If the Grassroots JSON document does not contain the given key.
	 */
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

	
	/**
	 * Add a StringField to the Lucene document with a given boost value for search scoring.
	 * 
	 * @param json_doc The Grassroots JSON document to pull the data from.
	 * @param key The key within the given Grassroots JSON document to get the value for.
	 * @param boost The boost value that will be used for this field when searching.
	 * @return <code>true</code> if the Field was added to the underlying Lucene document successfully, 
	 * <code>false</code> otherwise.
	 * @throws IllegalArgumentException If the Grassroots JSON document does not contain the given key.
	 */
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

	/**
	 * Add a String value to be stored, but not indexed, to the Lucene document.
	 * 
	 * @param json_doc The Grassroots JSON document to pull the data from.
	 * @param key The key within the given Grassroots JSON document to get the value for.
	 * @return <code>true</code> if the Field was added to the underlying Lucene document successfully, 
	 * <code>false</code> otherwise.
	 * @throws IllegalArgumentException If the Grassroots JSON document does not contain the given key.
	 */
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
	
	
	/**
	 * Add a field to the underlying Lucene document.
	 * 
	 * @param field The field to add.
	 * @param boost The boost value that will be used for this field when searching.
	 */
	protected void addField (IndexableField field, float boost) {
		gd_document.add (field);
		gd_document.add (new FloatDocValuesField (field.name() + GD_BOOST_SUFFIX, boost));
	}


	/**
	 * Add a YYYY-MM-DD date value to be stored, but not indexed, to the Lucene document.
	 * 
	 * @param json_doc The Grassroots JSON document to pull the data from.
	 * @param key The key within the given Grassroots JSON document to get the value for.
	 * @return <code>true</code> if the Field was added to the underlying Lucene document successfully, 
	 * <code>false</code> otherwise.
	 * @throws IllegalArgumentException If the Grassroots JSON document does not contain the given key or 
	 * if the corresponding value is not in the form YYYY-MM-DD.
	 */
	public boolean addDateString (JSONObject json_doc, String key) throws IllegalArgumentException {
		boolean success_flag = false;
		String value = (String) json_doc.get (key);
		
		if (value != null) {
			/*
			 * Grassroots dates are as YYYY-MM-DD and for easy sorting let's store them as
			 * YYYYMMDD. First let's check that is valid.
			 */
			try {
				LocalDate.parse (value);
			} catch (DateTimeParseException dtpe) {	
				throw new IllegalArgumentException ("invalid date " + value + " in " + json_doc);
			}
			
			StringBuilder sb = new StringBuilder ();
			
			sb.append (value.substring (0, 3));
			sb.append (value.substring (5, 6));
			sb.append (value.substring (8, 9));
		
			Field f = new StoredField (key, sb.toString ());
			gd_document.add (f);
			
			success_flag = true;
		} else {
			String json = json_doc.toJSONString ();
			
			throw new IllegalArgumentException ("No " + key + " in " + json);
		}

		return success_flag;
	}
	
	
	public String getNameKey () {
		return "so:name";
	}
	
	abstract public String getUserFriendlyTypename ();
}

