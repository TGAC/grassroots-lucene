package uk.ac.earlham.grassroots.document.json;


import java.util.Map;

import org.apache.lucene.document.Document;

import uk.ac.earlham.grassroots.document.lucene.ProgrammeDocument;



public class ProgrammeJSON extends MongoJSON {
	final public static String PJ_ABBREVIATION = "so:alternateName";
	final public static String PJ_CROP = "crop";
	final public static String PJ_PI = "principal_investigator";
	final public static String PJ_URL = "so:url";

	public ProgrammeJSON (Document doc, Map <String, String []> highlights) {
		super (doc, highlights);	
	}
	

	public boolean addToJSON (Document doc, Map <String, String []> highlights) {
		boolean b = super.addToJSON (doc, highlights);
		
		if (b) {
			if (addJSONField (doc, ProgrammeDocument.PD_PI, ProgrammeJSON.PJ_PI)) {
				
				addJSONField (doc, ProgrammeDocument.PD_ABBREVIATION, ProgrammeJSON.PJ_ABBREVIATION);
				addJSONField (doc, ProgrammeDocument.PD_CROP, ProgrammeJSON.PJ_CROP);
				addJSONField (doc, ProgrammeDocument.PD_URL, ProgrammeJSON.PJ_URL);
				
			} else {
				b = false;
			}
		}
		
		return b;
	}
}
