package uk.ac.earlham.grassroots.document;

import org.json.simple.JSONObject;

import uk.ac.earlham.grassroots.app.lucene.QueryUtil;
import uk.ac.earlham.grassroots.document.util.DocumentWrapper;

public class FieldTrialDocument extends MongoDocument {
	final private static String FT_TEAM = "team";
	
	public FieldTrialDocument (JSONObject json_doc, DocumentWrapper wrapper) throws IllegalArgumentException {
		super (json_doc, wrapper);
	}

	
	protected boolean addFields (JSONObject json_doc) {
		boolean success_flag = false;
		
		if (super.addFields (json_doc)) {
			
			/*
			 * Add the field trial-specific fields
			 */
			if (addText (json_doc, FT_TEAM)) {
				success_flag = true;
			}
		}
	
		return success_flag;
	}
	
	
	@Override
	public String getUserFriendlyTypename() {
		return "Field Trial";
	}


	@Override
	public void addQueryTerms(String value, StringBuilder query_buffer) {
		QueryUtil.buildQuery (query_buffer, FT_TEAM, value);		
	}

}
