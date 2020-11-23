package uk.ac.earlham.grassroots.document.json;

import java.util.Map;

import org.apache.lucene.document.Document;

import uk.ac.earlham.grassroots.document.lucene.FieldTrialDocument;

public class FieldTrialJSON extends MongoJSON {
	final public static String FTJ_TEAM = "team";
	
	
	public FieldTrialJSON (Document doc, Map <String, String []> highlights, int highlighter_index) {
		super (doc, highlights, highlighter_index);	
	}
	

	public boolean addToJSON (Document doc) {
		boolean b = super.addToJSON (doc);
		
		if (b) {
			addJSONField (doc, FieldTrialDocument.FTD_TEAM, FieldTrialJSON.FTJ_TEAM);
		}
		
		return b;
	}
}
