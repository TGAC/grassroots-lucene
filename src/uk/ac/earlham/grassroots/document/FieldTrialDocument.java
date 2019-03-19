package uk.ac.earlham.grassroots.document;

import org.json.simple.JSONObject;

public class FieldTrialDocument extends MongoDocument {

	public FieldTrialDocument (JSONObject json_doc) throws IllegalArgumentException {
		super (json_doc);
	}

	
	protected boolean addFields (JSONObject json_doc) {
		boolean success_flag = false;
		
		if (super.addFields (json_doc)) {
			
			/*
			 * Add the field trial-specific fields
			 */
			if (addText (json_doc, "team", 3.0f)) {
				success_flag = true;
			}
		}
	
		return success_flag;
	}
	
	
	@Override
	public String getUserFriendlyTypename() {
		return "Field Trial";
	}

}
