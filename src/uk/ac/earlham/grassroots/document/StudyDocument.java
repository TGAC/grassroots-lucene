package uk.ac.earlham.grassroots.document;

import org.json.simple.JSONObject;

public class StudyDocument extends GrassrootsDocument {
	
	public StudyDocument (JSONObject json_doc) throws IllegalArgumentException {
		super (json_doc);
		
		/*
		 * Add the study-specific fields
		 */
		addText (json_doc, "soil", 1.0f);
		addNonIndexedString (json_doc, "parent_field_trial_id");	
	}
}
