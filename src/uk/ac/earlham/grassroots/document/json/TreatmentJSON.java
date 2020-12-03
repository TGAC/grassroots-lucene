package uk.ac.earlham.grassroots.document.json;

import java.util.Map;

import org.apache.lucene.document.Document;

import uk.ac.earlham.grassroots.document.lucene.TreatmentDocument;

public class TreatmentJSON extends MongoJSON {
	final public static String TJ_ONTOLOGY_ID = "so:sameAd";
	final public static String TJ_SYNONYMS = "synonyms";

	public TreatmentJSON (Document doc, Map <String, String []> highlights, int highlighter_index) {
		super (doc, highlights, highlighter_index);	
	}
	

	public boolean addToJSON (Document doc) {
		boolean b = super.addToJSON (doc);
		
		if (b) {
			if (addJSONField (doc, TreatmentDocument.TD_ONTOLOGY_ID, TreatmentJSON.TJ_ONTOLOGY_ID)) {
				
			} else {
				b = false;
			}
		}
		
		return b;
	}
}
