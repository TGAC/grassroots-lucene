package uk.ac.earlham.grassroots.document.json;


import java.util.Map;

import org.apache.lucene.document.Document;



public class SamtoolsServiceJSON extends ServiceJSON {

	public SamtoolsServiceJSON (Document doc, Map <String, String []> highlights) {
		super (doc, highlights);	
	}
	

	public boolean addToJSON (Document doc, Map <String, String []> highlights) {
		boolean b = super.addToJSON (doc, highlights);
		
		return b;
	}

}
