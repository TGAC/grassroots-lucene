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
	static public String GD_DATATYPE = "type";
	static public String GD_NAME = "name";
	static public String GD_DESCRIPTION = "description";
	static public String GD_DEFAULT_SEARCH_KEY = "default";
	
	/** 
	 * The key for the url to use  the web-based client. 
	 */
	static public String GD_PUBLIC_LINK = "so:url";
	
	/** 
	 * The key to for the url to use for an app accessing
	 * the Grassroots backend directly.
	 */
	static public String GD_INTERNAL_LINK = "internal_url";

	static public final float GD_NAME_BOOST = 5.0f;
	static public final float GD_DESCRIPTION_BOOST = 3.0f;
	
	protected Document gd_document;
	protected StringBuilder gd_default_field_buffer;
	
	/**
	 * Create a new GrassrootsDocument
	 * 
	 * @param json_doc The Grassroots JSON document to pull the data from.
	 * @throws IllegalArgumentException If the Grassroots JSON document does not contain the required keys.
	 */
	public GrassrootsDocument (JSONObject json_doc) throws IllegalArgumentException {
		gd_document = new Document ();
		gd_default_field_buffer = new StringBuilder ();
		final String PRIVATE_TYPE = "@type";
		
		String private_typename = (String) json_doc.get (PRIVATE_TYPE);
		
		if (private_typename != null) {
		
			gd_document.add (new FacetField (GD_DATATYPE, getUserFriendlyTypename ()));
			gd_document.add (new StoredField (PRIVATE_TYPE, private_typename));
						
			if (!addFields (json_doc)) {
				System.err.println ("Error adding fields for " + json_doc);
				throw new IllegalArgumentException (json_doc.toJSONString ());
			}
			
			if (gd_default_field_buffer.length () > 0) {
				String s = gd_default_field_buffer.toString ();
				TextField default_field = new TextField (GD_DEFAULT_SEARCH_KEY, s, Field.Store.YES);
				gd_document.add (default_field);			
			}
		} else {
			System.err.println ("No " + PRIVATE_TYPE + " in " + json_doc);
			throw new IllegalArgumentException (json_doc.toJSONString ());			
		}
	}

	
	protected boolean addFields (JSONObject json_doc) {
		boolean success_flag = false;
		
		/*
		 * Add the common fields
		 */
		addText (json_doc, "so:description", GD_DESCRIPTION, GD_DESCRIPTION_BOOST);
		
		if (addText (json_doc, getNameKey (), GD_NAME, GD_NAME_BOOST)) {
			boolean added_link = (addNonIndexedString (json_doc, GD_PUBLIC_LINK) || (addNonIndexedString (json_doc, GD_INTERNAL_LINK)));
			
			if (added_link) {
				success_flag = true;
			} else {
				System.err.println ("Failed to add link from " + json_doc);
			}
		} else {
			System.err.println ("Failed to add " + GD_NAME + " from " + json_doc);
		}
			
		return success_flag;
	}

	
	/**
	 * Get the underlying Lucene document.
	 * 
	 * @return The Lucene document
	 */
	public Document getDocument () {
		return gd_document;
	}

	
	public boolean addFacet (String key, String private_value, String public_value) {
		FacetField facet = new FacetField (key, private_value);
		gd_document.add (facet);
		
		StoredField field = new StoredField (key, public_value);
		gd_document.add (field);
		
		return true;
	}
	
	public boolean addMongoId (JSONObject json_doc, String key) {
		boolean success_flag = false;
		JSONObject id_obj = (JSONObject) json_doc.get (key);
		
		String oid = (String) id_obj.get ("$oid");
		
		if (oid != null) {
			Field f = new StoredField (key, oid);
			gd_document.add (f);
			
			success_flag = true;
		}
		
		return success_flag;
	}

	
	/**
	 * Add a TextField to the Lucene document with a given boost value for search scoring.
	 * 
	 * @param json_doc The Grassroots JSON document to pull the data from.
	 * @param key The key within the given Grassroots JSON document to get the value for.
	 * @param boost The boost value that will be used for this field when searching.
	 * @return The value that was added for the given input_key or <code>null</code> if it was not 
	 * found in the given JSON document.
	 * @throws IllegalArgumentException If the Grassroots JSON document does not contain the given key.
	 */
	public boolean addText (JSONObject json_doc, String key, float boost) throws IllegalArgumentException {
		return addText (json_doc, key, key, boost);
	}

	
	/**
	 * Add a TextField to the Lucene document with a given boost value for search scoring.
	 * 
	 * @param json_doc The Grassroots JSON document to pull the data from.
	 * @param key The key within the given Grassroots JSON document to get the value for.
	 * @param boost The boost value that will be used for this field when searching.
	 * @return The value that was added for the given input_key or <code>null</code> if it was not 
	 * found in the given JSON document.
	 * @throws IllegalArgumentException If the Grassroots JSON document does not contain the given key.
	 */
	public boolean addText (JSONObject json_doc, String input_key, String output_key, float boost) throws IllegalArgumentException {
		boolean success_flag = false;
		String value = (String) json_doc.get (input_key);
		
		if (value != null) {
			Field f = new TextField (output_key, value, Field.Store.YES);
			addField (f, boost);
			
			addToDefaultBuffer (value);
			success_flag = true;
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


	protected void addToDefaultBuffer (String value) {
		if (gd_default_field_buffer.length () > 0) {
			gd_default_field_buffer.append (" ");				
		}
		
		gd_default_field_buffer.append (value);
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
			
			sb.append (value.substring (0, 4));
			sb.append (value.substring (5, 7));
			sb.append (value.substring (8, 10));
		
			Field f = new StoredField (key, sb.toString ());
			gd_document.add (f);
			
			success_flag = true;
		} 

		return success_flag;
	}
	
	
	public String getNameKey () {
		return "so:name";
	}

	abstract public String getUserFriendlyTypename ();
}

