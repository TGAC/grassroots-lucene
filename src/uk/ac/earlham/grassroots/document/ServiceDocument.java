package uk.ac.earlham.grassroots.document;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;

/**
 * The base class for importing Grassroots Service config documents
 * 
 * @author billy
 *
 */
public class ServiceDocument extends GrassrootsDocument {
	
	public ServiceDocument (JSONObject json_doc) throws IllegalArgumentException {
		super (json_doc);
	}
	
	
	@Override
	public String getUserFriendlyTypename() {
		return "Service";
	}

}
