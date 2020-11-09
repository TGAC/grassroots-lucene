package uk.ac.earlham.grassroots.document;

import org.json.simple.JSONObject;

import uk.ac.earlham.grassroots.document.util.DocumentWrapper;


abstract public class MongoDocument extends GrassrootsDocument {
	final static private String MD_PREFIX = "mongo-";

	public MongoDocument (JSONObject json_doc, DocumentWrapper wrapper) throws IllegalArgumentException {
		super (json_doc, wrapper);
	}
	

	protected boolean addFields (JSONObject json_doc) {
		boolean success_flag = false;
				
		if (super.addFields (json_doc)) {			
			if (addString (MD_PREFIX + "_id", gd_unique_id)) {
				success_flag = true;
			} else {
				System.err.println ("Failed to add mongo id for " + gd_unique_id + "  from " + json_doc);
			}				
		}
		
		return success_flag;
	}

	
	public boolean setId (JSONObject json_doc) {
		boolean success_flag = false;
		JSONObject id_obj = (JSONObject) json_doc.get ("_id");
		
		if (id_obj != null) {
			String oid = (String) id_obj.get ("$oid");
		
			if (oid != null) {
				gd_unique_id = oid;
				success_flag = true;
			}
		}
		
		return success_flag;
	}

}
