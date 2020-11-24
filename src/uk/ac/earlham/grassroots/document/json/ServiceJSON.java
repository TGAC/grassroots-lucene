package uk.ac.earlham.grassroots.document.json;

import java.util.Map;

import org.apache.lucene.document.Document;

import uk.ac.earlham.grassroots.document.lucene.BlastServiceDocument;
import uk.ac.earlham.grassroots.document.lucene.ServiceDocument;


public class ServiceJSON extends GrassrootsJSON {

	public ServiceJSON (Document doc, Map <String, String []> highlights, int highlighter_index) {
		super (doc, highlights, highlighter_index);	
	}
	

	public boolean addToJSON (Document doc) {
		boolean b = super.addToJSON (doc);
		
		if (b) {
			addJSONField (doc, ServiceDocument.SD_SERVICE, ServiceDocument.SD_SERVICE, false);
		}
		
		return b;
	}

}
