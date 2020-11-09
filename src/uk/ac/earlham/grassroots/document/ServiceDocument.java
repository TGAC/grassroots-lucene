package uk.ac.earlham.grassroots.document;


import org.json.simple.JSONObject;

import uk.ac.earlham.grassroots.document.util.DocumentWrapper;

/**
 * The base class for importing Grassroots Service config documents
 * 
 * @author billy
 *
 */
public class ServiceDocument extends GrassrootsDocument {
	
	public ServiceDocument (JSONObject json_doc, DocumentWrapper wrapper) throws IllegalArgumentException {
		super (json_doc, wrapper);
	}
	
	
	@Override
	public String getUserFriendlyTypename() {
		return "Service";
	}

	@Override
	public boolean setId (JSONObject json_doc) {
		boolean success_flag = false;
		String s = (String) json_doc.get ("id");
		
		if (s != null) {
			gd_unique_id = s;
			success_flag = true;
		}
		
		return success_flag;
	}


}
	
