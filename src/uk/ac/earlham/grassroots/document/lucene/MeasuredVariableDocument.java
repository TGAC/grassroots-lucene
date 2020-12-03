package uk.ac.earlham.grassroots.document.lucene;


import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;

import uk.ac.earlham.grassroots.document.json.MeasuredVariableJSON;
import uk.ac.earlham.grassroots.document.lucene.util.DocumentWrapper;


public class MeasuredVariableDocument extends MongoDocument {
	final static private String TD_PREFIX = "treatment-";
	final static public String TD_TRAIT_NAME = TD_PREFIX + "trait_name";
	final static public String TD_TRAIT_DESCRIPTION = TD_PREFIX + "trait_description";
	final static public String TD_TRAIT_ABBREVIATION = TD_PREFIX + "trait_abbreviation";
	final static public String TD_TRAIT_ID = TD_PREFIX + "trait_id";
	final static public String TD_MEASUREMENT_NAME = TD_PREFIX + "measurement_name";
	final static public String TD_MEASUREMENT_DESCRIPTION = TD_PREFIX + "measurement_description";
	final static public String TD_MEASUREMENT_ID = TD_PREFIX + "measurement_id";
	final static public String TD_UNIT_NAME = TD_PREFIX + "unit_name";
	final static public String TD_UNIT_ID = TD_PREFIX + "unit_id";
	final static public String TD_VARIABLE_NAME = TD_PREFIX + "variable_name";
	final static public String TD_VARIABLE_ID = TD_PREFIX + "variable_id";
	
	
	public MeasuredVariableDocument (JSONObject json_doc, DocumentWrapper wrapper) throws IllegalArgumentException {
		super (json_doc, wrapper);
	}
	
	
	protected boolean addFields (JSONObject json_doc) {
		boolean success_flag = false;
		
		/*
		 {
		    "_id" : ObjectId("5bd87c047b797f6947791b55"),
		    "trait" : {
		        "so:sameAs" : "CO_321:0000013",
		        "so:name" : "Grain yield",
		        "so:description" : "Amount (weight) of grains that was harvested.",
		        "abbreviation" : "GY"
		    },
		    "measurement" : {
		        "so:sameAs" : "CO_321:0001028",
		        "so:name" : "GY Computation",
		        "so:description" : "Use formulae to calculate grain yield in g/m2"
		    },
		    "unit" : {
		        "so:sameAs" : "CO_321:0000432",
		        "so:name" : "t/ha"
		    },
		    "internal_name" : "GRYLD",
		    "@type" : "Grassroots:Phenotype"
		}
		 */
		
		if (super.addFields (json_doc)) {
			/*
			 * Add the treatment-specific fields
			 */
			JSONObject child = (JSONObject) json_doc.get (MeasuredVariableJSON.TJ_TRAIT);

			if (child != null) {
				addText (child, MeasuredVariableJSON.TJ_TERM_NAME, MeasuredVariableDocument.TD_TRAIT_NAME);
				addText (child, MeasuredVariableJSON.TJ_TERM_DESCRIPTION, MeasuredVariableDocument.TD_TRAIT_DESCRIPTION);
				addString (child, MeasuredVariableJSON.TJ_TERM_ABBREVIATION, MeasuredVariableDocument.TD_TRAIT_ABBREVIATION);
				addString (child, MeasuredVariableJSON.TJ_TERM_URL, MeasuredVariableDocument.TD_TRAIT_ID);				
			}

			child = (JSONObject) json_doc.get (MeasuredVariableJSON.TJ_MEASUREMENT);
			if (child != null) {
				addText (child, MeasuredVariableJSON.TJ_TERM_NAME, MeasuredVariableDocument.TD_MEASUREMENT_NAME);
				addText (child, MeasuredVariableJSON.TJ_TERM_DESCRIPTION, MeasuredVariableDocument.TD_MEASUREMENT_DESCRIPTION);
				addString (child, MeasuredVariableJSON.TJ_TERM_URL, MeasuredVariableDocument.TD_MEASUREMENT_ID);				
			}

			child = (JSONObject) json_doc.get (MeasuredVariableJSON.TJ_UNIT);
			if (child != null) {
				addText (child, MeasuredVariableJSON.TJ_TERM_NAME, MeasuredVariableDocument.TD_UNIT_NAME);
				addString (child, MeasuredVariableJSON.TJ_TERM_URL, MeasuredVariableDocument.TD_UNIT_ID);				
			}
			
			child = (JSONObject) json_doc.get (MeasuredVariableJSON.TJ_VARIABLE);
			if (child != null) {
				addText (child, MeasuredVariableJSON.TJ_TERM_NAME, MeasuredVariableDocument.TD_VARIABLE_NAME);
				addString (child, MeasuredVariableJSON.TJ_TERM_URL, MeasuredVariableDocument.TD_VARIABLE_ID);				
			}
			
			success_flag = true;
		}
		
		return success_flag;
	}
	
	
	@Override
	public String getUserFriendlyTypename() {
		return "Measured Variable";
	}

	
	static public void addQueryTerms (List <String> fields, Map <String, Float> boosts, Map <String, String> string_fields) {
		fields.add (TD_TRAIT_NAME);
		fields.add (TD_TRAIT_DESCRIPTION);
		fields.add (TD_TRAIT_ABBREVIATION);
		fields.add (TD_TRAIT_ID);
		fields.add (TD_MEASUREMENT_NAME);
		fields.add (TD_MEASUREMENT_DESCRIPTION);
		fields.add (TD_MEASUREMENT_ID);
		fields.add (TD_UNIT_NAME);
		fields.add (TD_UNIT_ID);		
		fields.add (TD_VARIABLE_NAME);
		fields.add (TD_VARIABLE_ID);
		
		if (boosts != null) {
			boosts.put (TD_TRAIT_NAME, GD_NAME_BOOST);
			boosts.put (TD_MEASUREMENT_NAME, GD_NAME_BOOST);
			boosts.put (TD_UNIT_NAME, GD_NAME_BOOST);
		}
		
		if (string_fields != null) {
			string_fields.put (TD_TRAIT_ABBREVIATION, TD_TRAIT_ABBREVIATION);
			string_fields.put (TD_TRAIT_ID, TD_TRAIT_ID);
			string_fields.put (TD_MEASUREMENT_ID, TD_MEASUREMENT_ID);
			string_fields.put (TD_UNIT_ID, TD_UNIT_ID);
			string_fields.put (TD_VARIABLE_ID, TD_VARIABLE_ID);
		}
	}
	
	
	static public boolean isFieldMultiValued (String field) {			
		return MongoDocument.isFieldMultiValued (field);
	}
}
