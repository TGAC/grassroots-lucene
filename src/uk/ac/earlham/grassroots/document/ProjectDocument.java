package uk.ac.earlham.grassroots.document;

import org.json.simple.JSONObject;

import uk.ac.earlham.grassroots.document.util.DocumentWrapper;

public class ProjectDocument extends GrassrootsDocument {

	public ProjectDocument (JSONObject json_doc, DocumentWrapper wrapper) throws IllegalArgumentException {
		super (json_doc, wrapper);
	}

	
	protected boolean addFields (JSONObject json_doc) {
		boolean success_flag = false;
		
		if (super.addFields (json_doc)) {
			
			/*
			 * Add the project-specific fields
			 */
			if (addText (json_doc, "team")) {
				success_flag = true;
			}
		}
	
		return success_flag;
	}
	
	public String getUserFriendlyTypename () {
		return "Dataset";
	}

}
