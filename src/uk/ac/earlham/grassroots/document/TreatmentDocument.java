package uk.ac.earlham.grassroots.document;

import org.json.simple.JSONObject;

import uk.ac.earlham.grassroots.app.lucene.QueryUtil;
import uk.ac.earlham.grassroots.document.util.DocumentWrapper;

public class TreatmentDocument extends MongoDocument {
	static private String TD_TRAIT_NAME = "trait_name";
	static private String TD_TRAIT_DESCRIPTION = "trait_description";
	static private String TD_TRAIT_ABBREVIATION = "trait_abbreviation";
	static private String TD_TRAIT_ID = "trait_id";
	static private String TD_MEASUREMENT_NAME = "measurement_name";
	static private String TD_MEASUREMENT_DESCRIPTION = "measurement_description";
	static private String TD_MEASUREMENT_ID = "measurement_id";
	static private String TD_UNIT_NAME = "unit_name";
	static private String TD_UNIT_ID = "unit_id";
	static private String TD_VARIABLE_NAME = "variable_name";
	static private String TD_VARIABLE_ID = "variable_id";
	
	
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
	

	@Override
	public void addQueryTerms(String value, StringBuilder query_buffer) {
		QueryUtil.buildQuery (query_buffer, TD_TRAIT_NAME, value);		
		QueryUtil.buildQuery (query_buffer, TD_TRAIT_DESCRIPTION, value);		
		QueryUtil.buildQuery (query_buffer, TD_TRAIT_ABBREVIATION, value);		
		QueryUtil.buildQuery (query_buffer, TD_TRAIT_ID, value);		

		QueryUtil.buildQuery (query_buffer, TD_MEASUREMENT_NAME, value);		
		QueryUtil.buildQuery (query_buffer, TD_MEASUREMENT_DESCRIPTION, value);		
		QueryUtil.buildQuery (query_buffer, TD_MEASUREMENT_ID, value);		

		QueryUtil.buildQuery (query_buffer, TD_UNIT_NAME, value);		
		QueryUtil.buildQuery (query_buffer, TD_UNIT_ID, value);		

		QueryUtil.buildQuery (query_buffer, TD_VARIABLE_NAME, value);		
		QueryUtil.buildQuery (query_buffer, TD_VARIABLE_ID, value);		
	}
}
