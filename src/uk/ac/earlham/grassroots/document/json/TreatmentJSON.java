package uk.ac.earlham.grassroots.document.json;

import java.util.Map;

import org.apache.lucene.document.Document;
import org.json.simple.JSONObject;

import uk.ac.earlham.grassroots.document.lucene.TreatmentDocument;

public class TreatmentJSON extends GrassrootsJSON {
	final public static String TJ_TRAIT = "trait";
	final public static String TJ_MEASUREMENT = "measurement";
	final public static String TJ_UNIT = "unit";
	final public static String TJ_VARIABLE = "variable";
	final public static String TJ_TERM_URL = "so:sameAs";
	final public static String TJ_TERM_NAME = "so:name";
	final public static String TJ_TERM_ABBREVIATION = "abbreviation";
	final public static String TJ_TERM_DESCRIPTION = "description";

	
	public TreatmentJSON (Document doc, Map <String, String []> highlights, int highlighter_index) {
		super (doc, highlights, highlighter_index);	
	}
	
	
	public boolean addToJSON (Document doc) {
		boolean b = false;
		
		if (super.addToJSON (doc)) {
			if (addTrait (doc)) {
				if (addUnit (doc)) {
					if (addMeasurement (doc)) {
						if (addVariable (doc)) {
							b = true;
						}		
					}					
				}
			}
		}
		
		return b;
	}
	
	
	private boolean addTrait (Document doc) {
		boolean success_flag = false;
		String name = getString (doc, TreatmentDocument.TD_TRAIT_NAME);
		
		if (name != null) {
			String id = getString (doc, TreatmentDocument.TD_TRAIT_ID);

			if (id != null) {
				JSONObject trait = new JSONObject ();
				
				trait.put (TreatmentJSON.TJ_TERM_NAME, name);
				trait.put (TreatmentJSON.TJ_TERM_URL, id);

				String s = getString (doc, TreatmentDocument.TD_TRAIT_ABBREVIATION);
				if (s != null) {
					trait.put (TreatmentJSON.TJ_TERM_ABBREVIATION, s);
				}
				
				s = getString (doc, TreatmentDocument.TD_TRAIT_DESCRIPTION);
				if (s != null) {
					trait.put (TreatmentJSON.TJ_TERM_DESCRIPTION, s);
				}
				
				addJSONObject (TreatmentJSON.TJ_TRAIT, trait);
				success_flag = true;	
			}	
		}

		return success_flag;
	}
	
	
	
	
	private boolean addMeasurement (Document doc) {
		boolean success_flag = false;
		String name = getString (doc, TreatmentDocument.TD_MEASUREMENT_NAME);
		
		if (name != null) {
			String id = getString (doc, TreatmentDocument.TD_MEASUREMENT_ID);

			if (id != null) {
				JSONObject measurement = new JSONObject ();
				
				measurement.put (TreatmentJSON.TJ_TERM_NAME, name);
				measurement.put (TreatmentJSON.TJ_TERM_URL, id);

				String s = getString (doc, TreatmentDocument.TD_MEASUREMENT_DESCRIPTION);
				if (s != null) {
					measurement.put (TreatmentJSON.TJ_TERM_DESCRIPTION, s);
				}
				
				addJSONObject (TreatmentJSON.TJ_MEASUREMENT, measurement);
				
				success_flag = true;
			}
			
		}

		return success_flag;
	}
	

	
	private boolean addUnit (Document doc) {
		boolean success_flag = false;
		String name = getString (doc, TreatmentDocument.TD_UNIT_NAME);
		
		if (name != null) {
			String id = getString (doc, TreatmentDocument.TD_UNIT_ID);

			if (id != null) {
				JSONObject unit = new JSONObject ();
				
				unit.put (TreatmentJSON.TJ_TERM_NAME, name);
				unit.put (TreatmentJSON.TJ_TERM_URL, id);

				addJSONObject (TreatmentJSON.TJ_UNIT, unit);

				success_flag = true;
			}
			
		}

		return success_flag;
	}

	

	private boolean addVariable (Document doc) {
		boolean success_flag = false;
		String name = getString (doc, TreatmentDocument.TD_VARIABLE_NAME);
		
		if (name != null) {
			String id = getString (doc, TreatmentDocument.TD_VARIABLE_ID);

			if (id != null) {
				JSONObject variable = new JSONObject ();
				
				variable.put (TreatmentJSON.TJ_TERM_NAME, name);
				variable.put (TreatmentJSON.TJ_TERM_URL, id);

				addJSONObject (TreatmentJSON.TJ_VARIABLE, variable);

				success_flag = true;
			}
			
		}

		return success_flag;
	}


	
	private String getString (Document doc, String key) {
		String value = getHighlightedValue (key);
		
		if (value == null) {
			value = doc.get (key);
		}
		
		return value;
	}
}
