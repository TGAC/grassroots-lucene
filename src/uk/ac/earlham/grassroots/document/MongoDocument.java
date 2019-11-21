package uk.ac.earlham.grassroots.document;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import uk.ac.earlham.grassroots.document.util.DocumentWrapper;


abstract public class MongoDocument extends GrassrootsDocument {
	static private String MD_MONGO_ID = "_id";

	
	public MongoDocument (JSONObject json_doc, DocumentWrapper wrapper) throws IllegalArgumentException {
		super (json_doc, wrapper);
		
		setUniqueId (json_doc);
	}
	

	public JSONArray getSchemaFields (JSONArray fields) {
		super.getSchemaFields (fields);
		
		addField (fields, MD_MONGO_ID, "solr.StrField", false, true);
	
		return fields;
	}
	
	protected void setUniqueId (JSONObject json_doc) {
		JSONObject id_obj = (JSONObject) json_doc.get (MD_MONGO_ID);

		if (id_obj != null) {
			gd_unique_id = (String) id_obj.get ("$oid");			
		}			
	}
	

	protected boolean addFields (JSONObject json_doc) {
		boolean success_flag = false;
				
		if (super.addFields (json_doc)) {
			if (addMongoId (json_doc, MD_MONGO_ID)) {
				success_flag = true;
			} else {
				System.err.println ("Failed to add mongo id for " + MD_MONGO_ID + "  from " + json_doc);
			}				
		}
		
		return success_flag;
	}

	

	
}
