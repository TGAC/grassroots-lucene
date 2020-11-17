package uk.ac.earlham.grassroots.document;

import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;

import uk.ac.earlham.grassroots.document.util.DocumentWrapper;

public class ProgramDocument extends MongoDocument {
	final static private String PD_PREFIX = "program-";
	final static private String PD_ABBREVIATION = PD_PREFIX + "abbreviation";
	final static private String PD_CROP = PD_PREFIX + "crop";
	final static private String PD_PI = PD_PREFIX + "pi";
	final static private String PD_URL = PD_PREFIX + "url";

	
	public ProgramDocument (JSONObject json_doc, DocumentWrapper wrapper) throws IllegalArgumentException {
		super (json_doc, wrapper);
	}

	
	@Override
	public String getUserFriendlyTypename() {
		return "Program";
	}

	protected boolean addFields (JSONObject json_doc) {
		boolean success_flag = false;
		
		if (super.addFields (json_doc)) {
			
			addString (json_doc, "so:alternateName", PD_ABBREVIATION);
			addString (json_doc, "crop", PD_CROP);
			addText (json_doc, "principal_investigator", PD_PI);
			addString (json_doc, "so:url", PD_URL);
					
			success_flag = true;
		}
	
		return success_flag;
	}
	
	
	static public void addQueryTerms (List <String> fields, Map <String, Float> boosts) {
		fields.add (PD_ABBREVIATION);
		fields.add (PD_CROP);
		fields.add (PD_PI);
		fields.add (PD_URL);
	
		if (boosts != null) {
			boosts.put (PD_ABBREVIATION, GD_DESCRIPTION_BOOST);
		}
	}

}
