package uk.ac.earlham.grassroots.document.lucene;

import java.util.List;
import java.util.Map;

import org.apache.lucene.facet.FacetsConfig;
import org.json.simple.JSONObject;

import uk.ac.earlham.grassroots.document.lucene.util.DocumentWrapper;
import uk.ac.earlham.grassroots.document.lucene.util.Person;

import uk.ac.earlham.grassroots.document.json.FieldTrialJSON;


public class FieldTrialDocument extends MongoDocument {
	final static private String FTD_PREFIX = "trial-";
	final static public String FTD_TEAM = FTD_PREFIX + "team";
	final static public String FTD_PEOPLE = FTD_PREFIX + "people";
	
	public FieldTrialDocument (JSONObject json_doc, DocumentWrapper wrapper) throws IllegalArgumentException {
		super (json_doc, wrapper);
	}

	
	protected boolean addFields (JSONObject json_doc) {
		boolean success_flag = false;
		
		if (super.addFields (json_doc)) {
			
			/*
			 * Add the field trial-specific fields
			 */
			addText (json_doc, "team", FieldTrialDocument.FTD_TEAM);
			
			
			Person.addPeople (this, json_doc, FieldTrialJSON.FTJ_PEOPLE, FieldTrialDocument.FTD_PEOPLE);


			
			success_flag = true;
		}
	
		return success_flag;
	}
	
	
	@Override
	public String getUserFriendlyTypename() {
		return "Field Trial";
	}

	static public void setUpFacetsConfig (FacetsConfig facets_config) {
	}


	static public void addQueryTerms (List <String> fields, Map <String, Float> boosts, Map <String, String> string_fields) {
		fields.add (FieldTrialDocument.FTD_TEAM);

		Person.addQueryTerms (FieldTrialDocument.FTD_PEOPLE, fields, boosts, string_fields);
	}
	
}
