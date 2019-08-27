package uk.ac.earlham.grassroots.document;

import org.json.simple.JSONObject;

import uk.ac.earlham.grassroots.document.util.DocumentWrapper;

public class TreatmentDocument extends MongoDocument {
	static private String TD_TRAIT = "trait";
	static private String TD_MEASUREMENT = "measurement";
	static private String TD_UNIT = "unit";
	static private String TD_VARIABLE = "variable";
	
	
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
			addData (json_doc, TD_TRAIT);
			addData (json_doc, TD_MEASUREMENT);
			addData (json_doc, TD_UNIT);
			addData (json_doc, TD_VARIABLE);
			
			success_flag = true;
		}
		
		return success_flag;
	}
	
	
	@Override
	public String getUserFriendlyTypename() {
		return "Treatment";
	}

	
	public String getNameKey () {
		return null;
	}
	
	private void addData (JSONObject json_doc, String child_name) {
		JSONObject child = (JSONObject) json_doc.get (child_name);
		
		if (child != null) {
			addText (child, "so:name");
			addText (child, "so:description");
			addString (child, "so:sameAs");
			addText (child, "abbreviation");
		}			
	}
}
