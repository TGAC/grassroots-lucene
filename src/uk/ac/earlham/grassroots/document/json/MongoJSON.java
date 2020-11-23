package uk.ac.earlham.grassroots.document.json;

import java.util.Map;

import org.apache.lucene.document.Document;
import org.json.simple.JSONObject;

import uk.ac.earlham.grassroots.document.lucene.MongoDocument;

public class MongoJSON extends GrassrootsJSON {
	final public static String MJ_ID = "_id";
	
	
	public MongoJSON (Document doc, Map <String, String []> highlights) {
		super (doc, highlights);	
	}
	

	public boolean addToJSON (Document doc, Map <String, String []> highlights) {
		boolean b = super.addToJSON (doc, highlights);
		
		if (b) {
			String id = doc.get (MongoDocument.MD_ID);
			
			if (id != null) {
				gj_json.put (MongoJSON.MJ_ID, id);
			} else {
				b = false;
			}
		}
		
		return b;
	}

}
