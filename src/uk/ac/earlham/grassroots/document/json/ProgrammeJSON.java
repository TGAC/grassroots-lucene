package uk.ac.earlham.grassroots.document.json;


import java.util.Map;

import org.apache.lucene.document.Document;

import org.json.simple.JSONObject;

import uk.ac.earlham.grassroots.document.lucene.ProgrammeDocument;



public class ProgrammeJSON extends MongoJSON {
	final public static String PJ_ABBREVIATION = "so:alternateName";
	final public static String PJ_CROP = "crop";
	final public static String PJ_PI = "principal_investigator";
	final public static String PJ_URL = "so:url";
	final static public String PJ_PI_NAME = "so:name";
	final static public String PJ_FUNDER = "funders";
	final static public String PJ_CODE = "code";
	
	public ProgrammeJSON (Document doc, Map <String, String []> highlights, int highlighter_index) {
		super (doc, highlights, highlighter_index);	
	}
	

	public boolean addToJSON (Document doc) {
		boolean b = super.addToJSON (doc);
		
		if (b) {
			JSONObject pi = new JSONObject ();
			
			gj_json.put (ProgrammeJSON.PJ_PI, pi);
			pi.put ("@type", "Person");
			
			if (addJSONField (doc, pi, ProgrammeDocument.PD_PI, ProgrammeJSON.PJ_PI_NAME)) {
				
				addJSONField (doc, ProgrammeDocument.PD_ABBREVIATION, ProgrammeJSON.PJ_ABBREVIATION);
				addJSONField (doc, ProgrammeDocument.PD_CROP, ProgrammeJSON.PJ_CROP);
				addJSONField (doc, ProgrammeDocument.PD_URL, ProgrammeJSON.PJ_URL);
				addJSONField (doc, ProgrammeDocument.PD_FUNDER, ProgrammeJSON.PJ_FUNDER);
				addJSONField (doc, ProgrammeDocument.PD_CODE, ProgrammeJSON.PJ_CODE);				
			} else {
				b = false;
			}
		}
		
		return b;
	}
}
