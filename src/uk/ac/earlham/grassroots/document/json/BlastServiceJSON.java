package uk.ac.earlham.grassroots.document.json;

import java.util.Map;

import org.apache.lucene.document.Document;

import uk.ac.earlham.grassroots.document.lucene.GrassrootsDocument;

public class BlastServiceJSON extends ServiceJSON {
	final static public String BSJ_PAYLOAD = "payload";
	
	
	public BlastServiceJSON (Document doc, Map <String, String []> highlights, int highlighter_index) {
		super (doc, highlights, highlighter_index);	
	}
	

	public boolean addToJSON (Document doc) {
		boolean b = super.addToJSON (doc);
		
		if (b) {
			addJSONField (doc, BlastServiceJSON.BSJ_PAYLOAD, BlastServiceJSON.BSJ_PAYLOAD, false);
			addJSONField (doc, GrassrootsDocument.GD_INTERNAL_LINK, GrassrootsDocument.GD_INTERNAL_LINK, false);
		}
		
		return b;
	}
	
	
}
