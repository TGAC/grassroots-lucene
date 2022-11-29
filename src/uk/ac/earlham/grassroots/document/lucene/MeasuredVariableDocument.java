package uk.ac.earlham.grassroots.document.lucene;


import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;

import uk.ac.earlham.grassroots.document.json.MeasuredVariableJSON;
import uk.ac.earlham.grassroots.document.lucene.util.DocumentWrapper;


public class MeasuredVariableDocument extends MongoDocument {
	final static private String MVD_PREFIX = "measured_variable-";
	final static public String MVD_TRAIT_NAME = MVD_PREFIX + "trait_name";
	final static public String MVD_TRAIT_DESCRIPTION = MVD_PREFIX + "trait_description";
	final static public String MVD_TRAIT_ABBREVIATION = MVD_PREFIX + "trait_abbreviation";
	final static public String MVD_TRAIT_ID = MVD_PREFIX + "trait_id";
	final static public String MVD_MEASUREMENT_NAME = MVD_PREFIX + "measurement_name";
	final static public String MVD_MEASUREMENT_DESCRIPTION = MVD_PREFIX + "measurement_description";
	final static public String MVD_MEASUREMENT_ID = MVD_PREFIX + "measurement_id";
	final static public String MVD_UNIT_NAME = MVD_PREFIX + "unit_name";
	final static public String MVD_UNIT_ID = MVD_PREFIX + "unit_id";
	final static public String MVD_VARIABLE_NAME = MVD_PREFIX + "variable_name";
	final static public String MVD_VARIABLE_ID = MVD_PREFIX + "variable_id";
	
	
	public MeasuredVariableDocument (JSONObject json_doc, DocumentWrapper wrapper) throws IllegalArgumentException {
		super (json_doc, wrapper);
	}
	
	
	protected boolean addFields (JSONObject json_doc) {
		boolean success_flag = false;
		
		/*
		{
			"unit": {
				"so:name": "ppm",
				"so:sameAs": "ROTH_UNIT:000239",
				"so:description": "ppm",
				"abbreviation": "ppm"
			},
			"@type": "Grassroots:MeasuredVariable",
			"variable": {
				"so:name": "ZnDe_CalcIcpYldRe_ppm",
				"so:sameAs": "ROTH_VARIABLE:000538",
				"so:description": "ZnDe_CalcIcpYldRe_ppm"
			},
			"trait": {
				"so:name": "Zinc deviation",
				"so:sameAs": "ROTH_TRAIT:000507",
				"so:description": "Zinc deviation from a regression line plotted through concentration against grain yield.",
				"abbreviation": "ZnDe"
			},
			"scale": {
				"so:name": "Numerical",
				"class_type": "xsd:double"
			},
			"_id": {
				"$oid": "625ebcac721eed64c607472c"
			},
			"measurement": {
				"so:name": "Calculation from ICP data and grain yield (at 100% DM) data.",
				"so:sameAs": "ROTH_MEAS:000494",
				"so:description": "Zinc deviation calculated from a regression line plotted on grain concentration (from ICP analysis) against grain yield at 100% Dry Matter.",
				"abbreviation": "CalcIcpYldRe"
			},
			"type_description": "Measured Variable"
		}		 
		*/
		
		if (super.addFields (json_doc)) {
			MeasuredVariableDocument.indexMeasuredVariable (this, json_doc);
			
			success_flag = true;
		}
		
		return success_flag;
	}
	
	
	public static void indexMeasuredVariable (GrassrootsDocument grassroots_doc, JSONObject json_doc) {
		/*
		 * Add the treatment-specific fields
		 */
		JSONObject child = (JSONObject) json_doc.get (MeasuredVariableJSON.MVJ_TRAIT);

		if (child != null) {
			grassroots_doc.addText (child, MeasuredVariableJSON.MVJ_TERM_NAME, MeasuredVariableDocument.MVD_TRAIT_NAME);
			grassroots_doc.addText (child, MeasuredVariableJSON.MVJ_TERM_DESCRIPTION, MeasuredVariableDocument.MVD_TRAIT_DESCRIPTION);
			grassroots_doc.addString (child, MeasuredVariableJSON.MVJ_TERM_ABBREVIATION, MeasuredVariableDocument.MVD_TRAIT_ABBREVIATION);
			grassroots_doc.addString (child, MeasuredVariableJSON.MVJ_TERM_URL, MeasuredVariableDocument.MVD_TRAIT_ID);				
		}

		child = (JSONObject) json_doc.get (MeasuredVariableJSON.MVJ_MEASUREMENT);
		if (child != null) {
			grassroots_doc.addText (child, MeasuredVariableJSON.MVJ_TERM_NAME, MeasuredVariableDocument.MVD_MEASUREMENT_NAME);
			grassroots_doc.addText (child, MeasuredVariableJSON.MVJ_TERM_DESCRIPTION, MeasuredVariableDocument.MVD_MEASUREMENT_DESCRIPTION);
			grassroots_doc.addString (child, MeasuredVariableJSON.MVJ_TERM_URL, MeasuredVariableDocument.MVD_MEASUREMENT_ID);				
		}

		child = (JSONObject) json_doc.get (MeasuredVariableJSON.MVJ_UNIT);
		if (child != null) {
			grassroots_doc.addText (child, MeasuredVariableJSON.MVJ_TERM_NAME, MeasuredVariableDocument.MVD_UNIT_NAME);
			grassroots_doc.addString (child, MeasuredVariableJSON.MVJ_TERM_URL, MeasuredVariableDocument.MVD_UNIT_ID);				
		}
		
		child = (JSONObject) json_doc.get (MeasuredVariableJSON.MVJ_VARIABLE);
		if (child != null) {
			grassroots_doc.addText (child, MeasuredVariableJSON.MVJ_TERM_NAME, MeasuredVariableDocument.MVD_VARIABLE_NAME);
			grassroots_doc.addString (child, MeasuredVariableJSON.MVJ_TERM_URL, MeasuredVariableDocument.MVD_VARIABLE_ID);				
		}
		
	}
	
	@Override
	public String getUserFriendlyTypename() {
		return "Measured Variable";
	}

	
	public String getNameKey () {
		return null;
	}

	
	static public void addQueryTerms (List <String> fields, Map <String, Float> boosts, Map <String, String> string_fields) {
		fields.add (MVD_TRAIT_NAME);
		fields.add (MVD_TRAIT_DESCRIPTION);
		fields.add (MVD_TRAIT_ABBREVIATION);
		fields.add (MVD_TRAIT_ID);
		fields.add (MVD_MEASUREMENT_NAME);
		fields.add (MVD_MEASUREMENT_DESCRIPTION);
		fields.add (MVD_MEASUREMENT_ID);
		fields.add (MVD_UNIT_NAME);
		fields.add (MVD_UNIT_ID);		
		fields.add (MVD_VARIABLE_NAME);
		fields.add (MVD_VARIABLE_ID);
		
		if (boosts != null) {
			boosts.put (MVD_TRAIT_NAME, GD_NAME_BOOST);
			boosts.put (MVD_MEASUREMENT_NAME, GD_NAME_BOOST);
			boosts.put (MVD_UNIT_NAME, GD_NAME_BOOST);
		}
		
		if (string_fields != null) {
			string_fields.put (MVD_TRAIT_ABBREVIATION, MVD_TRAIT_ABBREVIATION);
			string_fields.put (MVD_TRAIT_ID, MVD_TRAIT_ID);
			string_fields.put (MVD_MEASUREMENT_ID, MVD_MEASUREMENT_ID);
			string_fields.put (MVD_UNIT_ID, MVD_UNIT_ID);
			string_fields.put (MVD_VARIABLE_ID, MVD_VARIABLE_ID);
		}
	}
	
	
	static public boolean isFieldMultiValued (String field) {			
		return MongoDocument.isFieldMultiValued (field);
	}
}
