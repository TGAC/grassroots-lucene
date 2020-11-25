package uk.ac.earlham.grassroots.document.json;

import java.util.Map;

import org.apache.lucene.document.Document;

import uk.ac.earlham.grassroots.document.lucene.MongoDocument;


public class MongoJSON extends GrassrootsJSON {
	final public static String MJ_ID = "_id";
	
	
	public MongoJSON (Document doc, Map <String, String []> highlights, int highlighter_index) {
		super (doc, highlights, highlighter_index);	
	}
	

	public boolean addToJSON (Document doc) {
		boolean b = super.addToJSON (doc);
		
		if (b) {
			if (!addJSONField (doc, MongoDocument.MD_ID, MongoJSON.MJ_ID)) {
				b = false;
			}
		}
		
		return b;
	}

	
	public String getIdKey () {
		return MongoJSON.MJ_ID;
	}

}
