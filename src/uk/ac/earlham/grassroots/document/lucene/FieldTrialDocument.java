package uk.ac.earlham.grassroots.document.lucene;

import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;

import uk.ac.earlham.grassroots.document.lucene.util.DocumentWrapper;

public class FieldTrialDocument extends MongoDocument {
	final static private String FTD_PREFIX = "trial-";
	final static public String FTD_TEAM = FTD_PREFIX + "team";
	
	public FieldTrialDocument (JSONObject json_doc, DocumentWrapper wrapper) throws IllegalArgumentException {
		super (json_doc, wrapper);
	}

	
	protected boolean addFields (JSONObject json_doc) {
		boolean success_flag = false;
		
		if (super.addFields (json_doc)) {
			
			/*
			 * Add the field trial-specific fields
			 */
			if (addText (json_doc, "team", FieldTrialDocument.FTD_TEAM)) {
				success_flag = true;
			}
		}
	
		return success_flag;
	}
	
	
	@Override
	public String getUserFriendlyTypename() {
		return "Field Trial";
	}


	static public void addQueryTerms (List <String> fields, Map <String, Float> boosts) {
		fields.add (FieldTrialDocument.FTD_TEAM);
	}
	
}
