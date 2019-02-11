package uk.ac.earlham.grassroots.document;


import org.apache.lucene.document.Document;
import org.json.simple.JSONObject;

import uk.ac.earlham.grassroots.document.GrassrootsDocument;

/**
 * A factory class for inspecting the JSON data sent by Grassroots and 
 * creates the appropriate GrassrootsDocument.
 * 
 * @author billy
 *
 */
public class GrassrootsDocumentFactory {

	/**
	 * Create the GrassrootsDocument for a given JSON object.
	 * 
	 * @param json_doc The JSON object created from the Grassroots JSON.
	 * @return The appropriate GrassrootsDocument or <code>null<code> upon
	 * error
	 */
	static public GrassrootsDocument createDocument (JSONObject json_doc) {
		GrassrootsDocument doc = null;
		Object obj_type = json_doc.get ("@type");
		
		if (obj_type != null) {
			String datatype = obj_type.toString ();
			
			switch (datatype) {
				case "Grassroots:FieldTrial":
					doc = new FieldTrialDocument (json_doc);
					break;
					
				case "Grassroots:Study": 
					doc = new StudyDocument (json_doc);					
					break;
					
				case "Grassroots:Address": 
					doc = new AddressDocument (json_doc);					
					break;
					
				default:
					break;
			}	
		}
		
		return doc;
	}
	
	
	
	/**
	 * Create the GrassrootsDocument for a given JSON object.
	 * 
	 * @param json_doc The JSON object created from the Grassroots JSON.
	 * @return The appropriate GrassrootsDocument or <code>null<code> upon
	 * error
	 */
	/*
	static public JSONObject getDocumentAsJSON (Document doc) {
		JSONObject json_doc = null;
		String datatype = doc.get ("@type");
		
		if (datatype != null) {			
			switch (datatype) {
				case "FieldTrial":
					json_doc = getJSONFromLuceneFieldTrial (doc);
					break;
					
				case "Study": 
					doc = new StudyDocument (json_doc);					
					break;
					
				case "Address": 
					doc = new AddressDocument (json_doc);					
					break;
					
				default:
					break;
			}	
		}
		
		return json_doc;
	}
	*/
}
