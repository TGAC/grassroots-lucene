package uk.ac.earlham.grassroots.document.json;

import java.util.Map;

import org.apache.lucene.document.Document;

import uk.ac.earlham.grassroots.document.lucene.FieldTrialDocument;

public class FieldTrialJSON extends MongoJSON {
	final public static String FTJ_TEAM = "team";
	
	
	public FieldTrialJSON (Document doc, Map <String, String []> highlights) {
		super (doc, highlights);	
	}
	

	public boolean addToJSON (Document doc, Map <String, String []> highlights) {
		boolean b = super.addToJSON (doc, highlights);
		
		if (b) {
			addJSONField (doc, FieldTrialDocument.FT_TEAM, FieldTrialJSON.FTJ_TEAM);
		}
		
		return b;
	}
}
