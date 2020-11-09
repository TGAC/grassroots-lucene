package uk.ac.earlham.grassroots.document;


import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;

import uk.ac.earlham.grassroots.document.util.DocumentWrapper;


public class TreatmentDocument extends MongoDocument {
	final static private String TD_PREFIX = "treatment-";
	final static private String TD_TRAIT_NAME = TD_PREFIX + "trait_name";
	final static private String TD_TRAIT_DESCRIPTION = TD_PREFIX + "trait_description";
	final static private String TD_TRAIT_ABBREVIATION = TD_PREFIX + "trait_abbreviation";
	final static private String TD_TRAIT_ID = TD_PREFIX + "trait_id";
	final static private String TD_MEASUREMENT_NAME = TD_PREFIX + "measurement_name";
	final static private String TD_MEASUREMENT_DESCRIPTION = TD_PREFIX + "measurement_description";
	final static private String TD_MEASUREMENT_ID = TD_PREFIX + "measurement_id";
	final static private String TD_UNIT_NAME = TD_PREFIX + "unit_name";
	final static private String TD_UNIT_ID = TD_PREFIX + "unit_id";
	final static private String TD_VARIABLE_NAME = TD_PREFIX + "variable_name";
	final static private String TD_VARIABLE_ID = TD_PREFIX + "variable_id";
	
	
	public TreatmentDocument (JSONObject json_doc, DocumentWrapper wrapper) throws IllegalArgumentException {
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
			JSONObject child = (JSONObject) json_doc.get ("trait");

			if (child != null) {
				addText (child, "so:name", "trait_name");
				addText (child, "so:description", "trait_description");
				addString (child, "abbreviation", "trait_abbreviation");
				addString (child, "so:sameAs", "trait_id");				
			}

			child = (JSONObject) json_doc.get ("measurement");
			if (child != null) {
				addText (child, "so:name", "measurement_name");
				addText (child, "so:description", "measurement_description");
				addString (child, "so:sameAs", "trait_id");				
			}

			child = (JSONObject) json_doc.get ("unit");
			if (child != null) {
				addText (child, "so:name", "unit_name");
				addString (child, "so:sameAs", "unit_id");				
			}
			
			child = (JSONObject) json_doc.get ("variable");
			if (child != null) {
				addText (child, "so:name", "variable_name");
				addString (child, "so:sameAs", "variable_id");				
			}
			
			success_flag = true;
		}
		
		return success_flag;
	}
	
	
	@Override
	public String getUserFriendlyTypename() {
		return "Measured Variable";
	}

	
	public String getNameKey () {
		return null;
	}
	

	static public void addQueryTerms (List <String> fields, Map <String, Float> boosts) {
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
		
		boosts.put (TD_TRAIT_NAME, GD_NAME_BOOST);
		boosts.put (TD_MEASUREMENT_NAME, GD_NAME_BOOST);
		boosts.put (TD_UNIT_NAME, GD_NAME_BOOST);
	}
}
