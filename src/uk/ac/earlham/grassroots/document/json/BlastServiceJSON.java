package uk.ac.earlham.grassroots.document.json;

import java.util.Map;

import org.apache.lucene.document.Document;

public class BlastServiceJSON extends ServiceJSON {
	final static public String BSJ_PAYLOAD = "payload";
	
	
	public BlastServiceJSON (Document doc, Map <String, String []> highlights) {
		super (doc, highlights);	
	}
	

	public boolean addToJSON (Document doc, Map <String, String []> highlights) {
		boolean b = super.addToJSON (doc, highlights);
		
		return b;
	}
	
	
}
