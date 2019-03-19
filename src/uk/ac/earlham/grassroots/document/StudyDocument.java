package uk.ac.earlham.grassroots.document;

import org.json.simple.JSONObject;

public class StudyDocument extends MongoDocument {
	
	public StudyDocument (JSONObject json_doc) throws IllegalArgumentException {
		super (json_doc);
	}
	
	
	protected boolean addFields (JSONObject json_doc) {
		boolean success_flag = false;
		
		if (super.addFields (json_doc)) {
			
			/*
			 * Add the study-specific fields
			 */
			addText (json_doc, "soil", 1.0f);
			
			if (addMongoId (json_doc, "parent_field_trial_id")) { 
				if (addDateString (json_doc, "sowing_date")) {
					if (addDateString (json_doc, "harvest_date")) {
						success_flag = true;
					}
				}
			}
		}
	
		return success_flag;
	}
	
	
	
	@Override
	public String getUserFriendlyTypename() {
		return "Study";
	}
}
