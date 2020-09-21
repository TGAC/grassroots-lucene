package uk.ac.earlham.grassroots.document;

import org.json.simple.JSONObject;

import uk.ac.earlham.grassroots.document.util.DocumentWrapper;

/**
 * The base class for importing Grassroots Service config documents
 * 
 * @author billy
 *
 */
abstract public class ServiceDocument extends GrassrootsDocument {
	
	public ServiceDocument (JSONObject json_doc, DocumentWrapper wrapper) throws IllegalArgumentException {
		super (json_doc, wrapper);
	}
	
	
	@Override
	public String getUserFriendlyTypename() {
		return "Service";
	}

}
