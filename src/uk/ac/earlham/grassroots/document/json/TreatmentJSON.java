package uk.ac.earlham.grassroots.document.json;

import java.util.Map;

import org.apache.lucene.document.Document;

import uk.ac.earlham.grassroots.document.lucene.TreatmentDocument;

public class TreatmentJSON extends GrassrootsJSON {
	final public static String TJ_TRAIT = "trait";
	final public static String TJ_MEASUREMENT = "measurement";
	final public static String TJ_UNIT = "unit";
	final public static String TJ_VARIABLE = "variable";

	
	public TreatmentJSON (Document doc, Map <String, String []> highlights) {
		super (doc, highlights);	
	}
	
	
	public boolean addToJSON (Document doc, Map <String, String []> highlights) {
		boolean b = super.addToJSON (doc, highlights);
		
		if (b) {

		}
		
		return b;
	}
}
