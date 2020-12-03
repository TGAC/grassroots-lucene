package uk.ac.earlham.grassroots.document.lucene;

import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;


import uk.ac.earlham.grassroots.document.json.TreatmentJSON;
import uk.ac.earlham.grassroots.document.lucene.util.DocumentWrapper;

public class TreatmentDocument extends MongoDocument {
	final static private String TD_PREFIX = "treatment-";
	final static public String TD_ONTOLOGY_ID = TD_PREFIX + "ontology_id";
	final static public String TD_SYNONYM = TD_PREFIX + "synonym";
	
	public TreatmentDocument (JSONObject json_doc, DocumentWrapper wrapper) throws IllegalArgumentException {
		super (json_doc, wrapper);
	}

	
	@Override
	public String getUserFriendlyTypename() {
		return "Treatment";
	}

	
	protected boolean addFields (JSONObject json_doc) {
		boolean success_flag = false;
		
		if (super.addFields (json_doc)) {
			
			if (addString (json_doc, TreatmentJSON.TJ_ONTOLOGY_ID, TreatmentDocument.TD_ONTOLOGY_ID)) {
				
			}
					
			success_flag = true;
		}
	
		return success_flag;
	}
	

	protected void addSynonyms (JSONObject doc) {		
		Object o = doc.get (TreatmentJSON.TJ_SYNONYMS);
		
		if (o != null) {
			if (o instanceof JSONArray) {
				JSONArray synonyms = (JSONArray) o;				
				final int num_accessions = synonyms.size ();
				
				for (int i = 0; i < num_accessions; ++ i) {
					o = synonyms.get (i);

					String synonym = o.toString ();
					
					addString (TreatmentDocument.TD_SYNONYM, synonym);					
				}
			}			
		}
	}


	
	static public void addQueryTerms (List <String> fields, Map <String, Float> boosts, Map <String, String> string_fields) {
		fields.add (TreatmentDocument.TD_ONTOLOGY_ID);
		fields.add (TreatmentDocument.TD_SYNONYM);
	

		if (boosts != null) {
			boosts.put (TreatmentDocument.TD_ONTOLOGY_ID, GrassrootsDocument.GD_NAME_BOOST);
			boosts.put (TreatmentDocument.TD_SYNONYM, GrassrootsDocument.GD_DESCRIPTION_BOOST);
		}
		
		if (string_fields != null) {
			string_fields.put (TreatmentDocument.TD_ONTOLOGY_ID, TreatmentDocument.TD_ONTOLOGY_ID);
		}

	}

}
