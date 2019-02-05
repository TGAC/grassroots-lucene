package uk.ac.earlham.grassroots.document;

import org.json.simple.JSONObject;

public class FieldTrialDocument extends GrassrootsDocument {

	public FieldTrialDocument (JSONObject json_doc) throws IllegalArgumentException {
		super (json_doc);
		
		/*
		 * Add the field trial-specific fields
		 */
		addText (json_doc, "team", 3.0f);
		
	}

}
