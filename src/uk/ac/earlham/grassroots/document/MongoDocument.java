package uk.ac.earlham.grassroots.document;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import uk.ac.earlham.grassroots.document.util.DocumentWrapper;


abstract public class MongoDocument extends GrassrootsDocument {
	
	public MongoDocument (JSONObject json_doc, DocumentWrapper wrapper) throws IllegalArgumentException {
		super (json_doc, wrapper);
	}
	

	public JSONArray getSchemaFields (JSONArray fields) {
		super.getSchemaFields (fields);
		
		addField (fields, getUniqueIdKey (), "solr.StrField", false, true);
	
		return fields;
	}
	

	protected boolean addFields (JSONObject json_doc) {
		boolean success_flag = false;
				
		if (super.addFields (json_doc)) {
			String id_key = getUniqueIdKey ();
			
			if (addMongoId (json_doc, id_key)) {
				success_flag = true;
			} else {
				System.err.println ("Failed to add mongo id for " + id_key + "  from " + json_doc);
			}				
		}
		
		return success_flag;
	}

	
	public String getUniqueIdKey () {
		return "_id";		
	}
	
}
