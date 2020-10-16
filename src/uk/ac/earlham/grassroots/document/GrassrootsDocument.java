package uk.ac.earlham.grassroots.document;


import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import uk.ac.earlham.grassroots.document.util.DocumentWrapper;


/**
 * The base class for all of the Grassroots datatypes that we'll be storing in Lucene
 * 
 * @author billy
 */
abstract public class GrassrootsDocument {
	static public String GD_DATATYPE = "facet_type";
	static public String GD_NAME = "so:name";
	static public String GD_DESCRIPTION = "so:description";
	static public String GD_DEFAULT_SEARCH_KEY = "default";
	static public String GD_LUCENE_ID = "id";
	static public String GD_ID_KEY = "_id";
	
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
	
	protected String gd_unique_id;
	
	
	/**
	 * Create a new GrassrootsDocument
	 * 
	 * @param json_doc The Grassroots JSON document to pull the data from.
	 * @throws IllegalArgumentException If the Grassroots JSON document does not contain the required keys.
	 */
	public GrassrootsDocument (JSONObject json_doc, DocumentWrapper wrapper) throws IllegalArgumentException {
		gd_wrapper = wrapper;
		gd_unique_id = null; 
		
		if (setId (json_doc)) {
			final String PRIVATE_TYPE = "@type";
			
			String private_typename = (String) json_doc.get (PRIVATE_TYPE);
			
			if (private_typename != null) {
			
				wrapper.addFacet (GD_DATATYPE, getUserFriendlyTypename ());
				wrapper.addNonIndexedString (PRIVATE_TYPE, private_typename);
				//wrapper.addString (GD_LUCENE_ID, gd_unique_id);
							

				if (!addFields (json_doc)) {
					System.err.println ("Error adding fields for " + json_doc);
					throw new IllegalArgumentException (json_doc.toJSONString ());
				}
				

			} else {
				System.err.println ("No " + PRIVATE_TYPE + " in " + json_doc);
				throw new IllegalArgumentException (json_doc.toJSONString ());			
			}
			
		} else {
			System.err.println ("No  unique id in " + json_doc);
			throw new IllegalArgumentException (json_doc.toJSONString ());			
		}
		
	}
	
	/**
	 * Add the name and description values to the JSON document
	 * @param json_doc
	 * @return
	 */
	protected boolean addFields (JSONObject json_doc) {
		boolean success_flag = false;
		boolean added_link_flag = false;
		
		/*
		 * Add the common fields
		 */
		addText (json_doc, getDescriptionKey (), GD_DESCRIPTION);
		
		String name_key = getNameKey ();
		
		if (name_key != null) {
			if (!addText (json_doc, name_key, GD_NAME)) {
				System.err.println ("Failed to add " + GD_NAME + " using " + name_key + " from " + json_doc);				
				return false;
			}
		}
		
		if (addNonIndexedString (json_doc, GD_PUBLIC_LINK)) {
			added_link_flag = true; 
		}
		
		success_flag = true;
		
		if (!added_link_flag) {
			//System.err.println ("Failed to add link from " + json_doc);
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
			gd_wrapper.addString (key, oid);
			
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
	 * Add a TextField to the Lucene document with a given boost value for search scoring.
	 * 
	 * @param key The key to use in for the added TextField in the Lucene Document.
	 * @param value The value that will be added to the Lucene Document.
	 * @return <code>true</code> if the Field was added to the underlying Lucene document successfully, 
	 * <code>false</code> otherwise.
	 * @throws IllegalArgumentException If the Grassroots JSON document does not contain the given key.
	 */
	public boolean addText (String key, String value) throws IllegalArgumentException {
		gd_wrapper.addText (key, value);

		return true;
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
		return addString (json_doc, key, key);
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
	public boolean addString (JSONObject json_doc, String input_key, String output_key) throws IllegalArgumentException {
		boolean success_flag = false;
		String value = (String) json_doc.get (input_key);
		
		if (value != null) {
			gd_wrapper.addString (output_key, value);

			success_flag = true;
		} 

		return success_flag;
	}

	
	/**
	 * Add a StringField to the Lucene document with a given boost value for search scoring.
	 * 
	 * @param key The key to use in for the added String in the Lucene Document.
	 * @param value The value that will be added to teh Lucene Document.
	 * @return <code>true</code> if the Field was added to the underlying Lucene document successfully, 
	 * <code>false</code> otherwise.
	 * @throws IllegalArgumentException If the Grassroots JSON document does not contain the given key.
	 */
	public boolean addString (String key, String value) throws IllegalArgumentException {
		gd_wrapper.addString (key, value);

		return true;
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
			addNonIndexedString (key, value);
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
	public boolean addNonIndexedString (String key, String value) throws IllegalArgumentException {
		gd_wrapper.addNonIndexedString (key, value);
		return true;
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
		Object date_value = json_doc.get (key);
		
		if (date_value != null) {
			String date_str = null;
			
			if (date_value instanceof String) {
				String value = date_value.toString ();
				/*
				 * Grassroots dates are as YYYY-MM-DD and for easy sorting let's store them as
				 * YYYYMMDD. First let's check that is valid.
				 */
				try {
					LocalDate.parse (value);
					date_str = value;
				} catch (DateTimeParseException dtpe) {	
					throw new IllegalArgumentException ("invalid date " + value + " in " + json_doc);
				}
				

			
			} else if (date_value instanceof Long){
				long l = ((Long) date_value).longValue ();

				/*
				 * l is in seconds, Date requires milliseconds 
				 * so convert it
				 */
				Date d = new Date (l * 1000);
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
				
				date_str = formatter.format (d);
				
				System.out.println ("l "  + l + " date " + d + " date_str " + date_str);
			}

			if (date_str != null) {			
				StringBuilder sb = new StringBuilder ();
				
				sb.append (date_str.substring (0, 4));
				sb.append (date_str.substring (5, 7));
				sb.append (date_str.substring (8, 10));

				date_str = sb.toString();

				
				gd_wrapper.addNonIndexedString (key, date_str);
				success_flag = true;
			}
						
		} 

		return success_flag;
	}
	
	
	public String getNameKey () {
		return "so:name";
	}

	
	public String getDescriptionKey () {
		return "so:description";
	}
	
	public JSONArray getSchemaFields (JSONArray fields) {
		if (fields == null) {
			fields = new JSONArray ();
		}
		
		addField (fields, GD_NAME, "solr.TextField", true, true);
		addField (fields, getDescriptionKey (), "solr.TextField", true, true);
		addField (fields, GD_DEFAULT_SEARCH_KEY, "solr.TextField", true, true);
		addField (fields, GD_PUBLIC_LINK, "solr.StrField", true, true);
		addField (fields, GD_INTERNAL_LINK, "solr.StrField", true, true);
	
		return fields;
	}
	
	public void addField (JSONArray fields, String name, String datatype, boolean indexed_flag, boolean stored_flag) {
		JSONObject json = new JSONObject ();
		
		json.put ("name", name);
		json.put ("type", datatype);
		json.put ("stored", stored_flag ? true : false);
		json.put ("indexed", stored_flag ? true : false);

		fields.add (json);
	}

	
	public String getId () {
		return gd_unique_id;
	}

	
	abstract public String getUserFriendlyTypename ();

	abstract public boolean setId (JSONObject json_doc);
}

