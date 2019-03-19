package uk.ac.earlham.grassroots.document;

import org.json.simple.JSONObject;


abstract public class MongoDocument extends GrassrootsDocument {
	static private String MD_MONGO_ID = "_id";

	
	public MongoDocument (JSONObject json_doc) throws IllegalArgumentException {
		super (json_doc);
	}
	
	protected boolean addFields (JSONObject json_doc) {
		boolean success_flag = super.addFields (json_doc);
		
		if (success_flag) {
			success_flag = addMongoId (json_doc, MD_MONGO_ID);
		}
		
		return success_flag;
	}

}
