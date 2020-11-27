package uk.ac.earlham.grassroots.document.lucene;

import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;

import uk.ac.earlham.grassroots.document.json.ProgrammeJSON;
import uk.ac.earlham.grassroots.document.lucene.util.DocumentWrapper;

public class ProgrammeDocument extends MongoDocument {
	final static private String PD_PREFIX = "programme-";
	final static public String PD_ABBREVIATION = PD_PREFIX + "abbreviation";
	final static public String PD_CROP = PD_PREFIX + "crop";
	final static public String PD_PI = PD_PREFIX + "pi";
	final static public String PD_URL = PD_PREFIX + "url";

	
	public ProgrammeDocument (JSONObject json_doc, DocumentWrapper wrapper) throws IllegalArgumentException {
		super (json_doc, wrapper);
	}

	
	@Override
	public String getUserFriendlyTypename() {
		return "Programme";
	}

	protected boolean addFields (JSONObject json_doc) {
		boolean success_flag = false;
		
		if (super.addFields (json_doc)) {
			
			addString (json_doc, ProgrammeJSON.PJ_ABBREVIATION, ProgrammeDocument.PD_ABBREVIATION);
			addString (json_doc, ProgrammeJSON.PJ_CROP, ProgrammeDocument.PD_CROP);
			addText (json_doc, ProgrammeJSON.PJ_PI, ProgrammeDocument.PD_PI);
			addString (json_doc, ProgrammeJSON.PJ_URL, ProgrammeDocument.PD_URL);
					
			success_flag = true;
		}
	
		return success_flag;
	}
	
	
	static public void addQueryTerms (List <String> fields, Map <String, Float> boosts, Map <String, String> string_fields) {
		fields.add (ProgrammeDocument.PD_ABBREVIATION);
		fields.add (ProgrammeDocument.PD_CROP);
		fields.add (ProgrammeDocument.PD_PI);
		fields.add (ProgrammeDocument.PD_URL);
	
		if (boosts != null) {
			boosts.put (ProgrammeDocument.PD_ABBREVIATION, ProgrammeDocument.GD_DESCRIPTION_BOOST);
		}
		
		if (string_fields != null) {
			string_fields.put (ProgrammeDocument.PD_ABBREVIATION, ProgrammeDocument.PD_ABBREVIATION);
			string_fields.put (ProgrammeDocument.PD_CROP, ProgrammeDocument.PD_CROP);
			string_fields.put (ProgrammeDocument.PD_URL, ProgrammeDocument.PD_URL);
		}

	}

}
