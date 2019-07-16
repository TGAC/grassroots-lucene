package uk.ac.earlham.grassroots.document;


import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import org.json.simple.JSONObject;

import uk.ac.earlham.grassroots.document.util.DocumentWrapper;


/**
 * The base class for all of the Grassroots datatypes that we'll be storing in Lucene
 * 
 * @author billy
 */
abstract public class GrassrootsDocument {
	static private String GD_BOOST_SUFFIX = "_boost";
	static public String GD_DATATYPE = "facet_type";
	static public String GD_NAME = "so:name";
	static public String GD_DESCRIPTION = "so:description";
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
	
	protected DocumentWrapper gd_wrapper;
	
	/**
	 * Create a new GrassrootsDocument
	 * 
	 * @param json_doc The Grassroots JSON document to pull the data from.
	 * @throws IllegalArgumentException If the Grassroots JSON document does not contain the required keys.
	 */
	public GrassrootsDocument (JSONObject json_doc, DocumentWrapper wrapper) throws IllegalArgumentException {
		gd_wrapper = wrapper;
		
		final String PRIVATE_TYPE = "@type";
		
		String private_typename = (String) json_doc.get (PRIVATE_TYPE);
		
		if (private_typename != null) {
		
			wrapper.addFacet (GD_DATATYPE, getUserFriendlyTypename ());
			wrapper.addNonIndexedString (PRIVATE_TYPE, private_typename);
						
			if (!addFields (json_doc)) {
				System.err.println ("Error adding fields for " + json_doc);
				throw new IllegalArgumentException (json_doc.toJSONString ());
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
		addText (json_doc, "so:description", GD_DESCRIPTION);
		
		String name_key = getNameKey ();
		
		if (name_key != null) {
			if (!addText (json_doc, name_key, GD_NAME)) {
				System.err.println ("Failed to add " + GD_NAME + " using " + name_key + " from " + json_doc);				
				return false;
			}
		}
		
		boolean added_link = (addNonIndexedString (json_doc, GD_PUBLIC_LINK) || (addNonIndexedString (json_doc, GD_INTERNAL_LINK)));
		
		success_flag = true;
		
		if (!added_link) {
			System.err.println ("Failed to add link from " + json_doc);
		}
			
		return success_flag;
	}

	
	
	public boolean addFacet (String key, String private_value, String public_value) {
		gd_wrapper.addFacet (key, private_value);
		gd_wrapper.addNonIndexedString (key, public_value);
	
		return true;
	}
	
	public boolean addMongoId (JSONObject json_doc, String key) {
		boolean success_flag = false;
		JSONObject id_obj = (JSONObject) json_doc.get (key);
		
		String oid = (String) id_obj.get ("$oid");
		
		if (oid != null) {
			gd_wrapper.addNonIndexedString (key, oid);
			
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
	public boolean addText (JSONObject json_doc, String key) throws IllegalArgumentException {
		return addText (json_doc, key, key);
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
	public boolean addText (JSONObject json_doc, String input_key, String output_key) throws IllegalArgumentException {
		boolean success_flag = false;
		String value = (String) json_doc.get (input_key);
		
		if (value != null) {
			gd_wrapper.addText (output_key, value);
			
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
	public boolean addString (JSONObject json_doc, String key) throws IllegalArgumentException {
		boolean success_flag = false;
		String value = (String) json_doc.get (key);
		
		if (value != null) {
			gd_wrapper.addString (key, value);

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
			gd_wrapper.addNonIndexedString (key, value);

			success_flag = true;
		} 
		
		return success_flag;
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
		
			gd_wrapper.addNonIndexedString (key, sb.toString ());
			
			success_flag = true;
		} 

		return success_flag;
	}
	
	
	public String getNameKey () {
		return "so:name";
	}

	abstract public String getUserFriendlyTypename ();
}

