package uk.ac.earlham.grassroots.document;

import org.json.simple.JSONObject;

import uk.ac.earlham.grassroots.document.util.DocumentWrapper;

public class StudyDocument extends MongoDocument {
	
	public StudyDocument (JSONObject json_doc, DocumentWrapper wrapper) throws IllegalArgumentException {
		super (json_doc, wrapper);
	}
	
	
	protected boolean addFields (JSONObject json_doc) {
		boolean success_flag = false;
		
		if (super.addFields (json_doc)) {
			
			if (addMongoId (json_doc, "parent_field_trial_id")) { 

				if (addMongoId (json_doc, "address_id")) { 
	
					/*
					 * Add the study-specific fields
					 */
					addText (json_doc, "soil");
					addDateString (json_doc, "sowing_date");
					addDateString (json_doc, "harvest_date");
					
					success_flag = true;
				} else {
					System.err.println ("Failed to add mongo id for address_id from " + json_doc);
				}
			} else {
				System.err.println ("Failed to add mongo id for address_id from " + json_doc);
			}
		}
	
		return success_flag;
	}
	
	
	
	@Override
	public String getUserFriendlyTypename() {
		return "Study";
	}
}
