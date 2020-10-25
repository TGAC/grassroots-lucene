package uk.ac.earlham.grassroots.document;

import java.util.HashMap;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import uk.ac.earlham.grassroots.document.util.DocumentWrapper;

public class StudyDocument extends MongoDocument {
	
	public StudyDocument (JSONObject json_doc, DocumentWrapper wrapper) throws IllegalArgumentException {
		super (json_doc, wrapper);
	}
	
	
	protected boolean addFields (JSONObject json_doc) {
		boolean success_flag = false;
		
		if (super.addFields (json_doc)) {
			JSONObject parent_field_trial = (JSONObject) json_doc.get ("parent_field_trial");
			
			if (parent_field_trial != null) {
				boolean added_address_flag = false;
				
				if (json_doc.get ("address_id") != null) {
					added_address_flag = addMongoId (json_doc, "address_id");
				} else if (json_doc.get ("address") != null) {
					added_address_flag = true;					
				}
				
				if (added_address_flag) {
					final String description_key = getDescriptionKey ();
					
					/*
					 * Add the study-specific fields
					 */
					addText (json_doc, "soil");
					addText (json_doc, "phenotype_gathering_notes", description_key);
					addText (json_doc, "design", description_key);
					addText (json_doc, "study_design", description_key);

					addDateString (json_doc, "sowing_date");
					addDateString (json_doc, "harvest_date");
					
					/*
					 * crop, previous crop, aspect, slope 
					 */
					addCrop (json_doc, "current_crop");
					addCrop (json_doc, "previous_crop");

					// slope
					addText (json_doc, "envo:00002000");

					// aspect
					addAspect (json_doc);
					
					
					// phenotypes
					addPhenotypes (json_doc);

					
					// parent field trial
					addParentFieldTrial (json_doc);

					
					success_flag = true;
				} else {
					System.err.println ("Failed to add mongo id for address_id from " + json_doc);
				}
				
			}
			
		}
	
		return success_flag;
	}
	
	
	void addCrop (JSONObject doc, String crop_key) {
		JSONObject crop = (JSONObject) doc.get (crop_key);
		
		if (crop != null) {
			addText (crop, GrassrootsDocument.GD_NAME, crop_key);
		}
	}
	
	void addAspect (JSONObject doc) {		
		String aspect = (String) doc.get ("ncit:C42677");
		
		if (aspect != null) {
			String aspect_str = null;
		
			if (aspect.compareTo ("http://purl.obolibrary.org/obo/NCIT_C45849") == 0) {
				aspect_str = "North";
			} else if (aspect.compareTo ("http://purl.obolibrary.org/obo/NCIT_C45853") == 0) {
				aspect_str = "North-East"; 
			} else if (aspect.compareTo ("http://purl.obolibrary.org/obo/NCIT_C45851") == 0) {
				aspect_str = "East"; 
			} else if (aspect.compareTo ("http://purl.obolibrary.org/obo/NCIT_C45855") == 0) {
				aspect_str = "South-East"; 
			} else if (aspect.compareTo ("http://purl.obolibrary.org/obo/NCIT_C45850") == 0) {
				aspect_str = "South"; 
			} else if (aspect.compareTo ("http://purl.obolibrary.org/obo/NCIT_C45856") == 0) {
				aspect_str = "South-West"; 
			} else if (aspect.compareTo ("http://purl.obolibrary.org/obo/NCIT_C45852") == 0) {
				aspect_str = "West"; 
			} else if (aspect.compareTo ("http://purl.obolibrary.org/obo/NCIT_C45854") == 0) {
				aspect_str = "North-West"; 
			}

			if (aspect_str != null) {
				gd_wrapper.addString ("aspect", aspect_str);
			}
		}
		
	}
	

	void addPhenotypes (JSONObject doc) {		
		Object o = doc.get ("phenotypes");
		HashMap <String, String> names_map = new HashMap <String, String> ();
		HashMap <String, String> descriptions_map = new HashMap <String, String> ();
		
		if (o != null) {
			if (o instanceof JSONArray) {
				JSONArray phenotypes = (JSONArray) o;				
				final int num_phenotypes = phenotypes.size ();
				
				for (int i = 0; i < num_phenotypes; ++ i) {
					JSONObject phenotype = (JSONObject) phenotypes.get (i);

					Object value = phenotype.get ("so:name");
					if (value != null) {
						String name = value.toString ();
						
						if (!names_map.containsKey (name)) {
							addText (GrassrootsDocument.GD_NAME, name);						
							names_map.put (name, name);
						}
					}

					value = phenotype.get ("so:description");
					if (value != null) {
						String description = value.toString ();
						
						if (!descriptions_map.containsKey (description)) {
							addText (GrassrootsDocument.GD_DESCRIPTION, description);						
							descriptions_map.put (description, description);
						}

					}
				}
			}
			
		}
		
	}


	
	void addParentFieldTrial (JSONObject doc) {		
		Object o = doc.get ("parent_field_trial");

		
		if (o != null) {
			if (o instanceof JSONObject) {
				JSONObject field_trial = (JSONObject) o;
								
				o = field_trial.get ("so:name");
				
				if (o != null) {
					String name = o.toString ();
					addText (GrassrootsDocument.GD_NAME, name);						
				}
			}
		}
		
	}

	
	
	@Override
	public String getUserFriendlyTypename() {
		return "Study";
	}
}
