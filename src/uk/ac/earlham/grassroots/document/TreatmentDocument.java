package uk.ac.earlham.grassroots.document;

import org.json.simple.JSONObject;

public class TreatmentDocument extends GrassrootsDocument {

	
	public TreatmentDocument (JSONObject json_doc) throws IllegalArgumentException {
		super (json_doc);
	}
	
	
	protected boolean addFields (JSONObject json_doc) {
		boolean success_flag = false;
		
		if (super.addFields (json_doc)) {
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
			
			
			/*
			 * Add the treatment-specific fields
			 */
			addData (json_doc, "trait");
			addData (json_doc, "measurement");
			addData (json_doc, "unit");
			
			if (addMongoId (json_doc, "parent_field_trial_id")) { 
				if (addDateString (json_doc, "sowing_date")) {
					if (addDateString (json_doc, "harvest_date")) {
						success_flag = true;
					}
				}
			}
		}
	
		return success_flag;
	}
	
	
	@Override
	public String getUserFriendlyTypename() {
		return "Treatment";
	}

	
	private void addData (JSONObject json_doc, String child_name) {
		JSONObject child = json_doc.get (child_name);
		
		if (child != null) {
			String value = (String) json_doc.get ("so:name");
			
			addText (json_doc, "so:name", GD_NAME_BOOST);
			addText (json_doc, "so:description", GD_NAME_BOOST);
			addString (json_doc, "so:sameAs", GD_NAME_BOOST);
			addText (json_doc, "abbreviation", 1.0f);
		}			
	}
}
