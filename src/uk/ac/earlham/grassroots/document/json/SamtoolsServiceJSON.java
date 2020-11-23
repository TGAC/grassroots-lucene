package uk.ac.earlham.grassroots.document.json;


import java.util.Map;

import org.apache.lucene.document.Document;



public class SamtoolsServiceJSON extends ServiceJSON {

	public SamtoolsServiceJSON (Document doc, Map <String, String []> highlights, int highlighter_index) {
		super (doc, highlights, highlighter_index);	
	}
	

	public boolean addToJSON (Document doc) {
		boolean b = super.addToJSON (doc);
		
		return b;
	}

}
