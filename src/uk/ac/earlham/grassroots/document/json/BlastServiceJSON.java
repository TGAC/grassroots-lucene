package uk.ac.earlham.grassroots.document.json;

import java.util.Map;

import org.apache.lucene.document.Document;

public class BlastServiceJSON extends ServiceJSON {
	final static public String BSJ_PAYLOAD = "payload";
	
	
	public BlastServiceJSON (Document doc, Map <String, String []> highlights, int highlighter_index) {
		super (doc, highlights, highlighter_index);	
	}
	

	public boolean addToJSON (Document doc) {
		boolean b = super.addToJSON (doc);
		
		if (b) {
			addJSONField (doc, BlastServiceJSON.BSJ_PAYLOAD, BlastServiceJSON.BSJ_PAYLOAD);
		}
		
		return b;
	}
	
	
}
