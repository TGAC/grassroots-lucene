package uk.ac.earlham.grassroots.document;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import uk.ac.earlham.grassroots.document.util.DocumentWrapper;

public class StudyDocument extends MongoDocument {
	final static private String SD_PREFIX = "study-";
	final static private String SD_PARENT_TRIAL = SD_PREFIX + "trial";
	final static private String SD_SOIL = SD_PREFIX + "soil";
	final static private String SD_PHENOTYPE_GATHERING = SD_PREFIX + "phenotype_gathering_notes";
	final static private String SD_STUDY_DESIGN = SD_PREFIX + "study_design";
	final static private String SD_CURRENT_CROP = SD_PREFIX + "current_crop";
	final static private String SD_PREVIOUS_CROP = SD_PREFIX + "previous_crop";
	final static private String SD_SOWING_DATE = SD_PREFIX + "sowing_date";
	final static private String SD_HARVEST_DATE = SD_PREFIX + "harvest_date";
	final static private String SD_SLOPE = SD_PREFIX + "slope";
	final static private String SD_ASPECT = SD_PREFIX + "aspect";
	final static private String SD_PHENOTYPE_NAME = SD_PREFIX + "phenotype_name";
	final static private String SD_PHENOTYPE_DESCRIPTION = SD_PREFIX + "phenotype_description";
	final static private String SD_ACCESSION = SD_PREFIX + "accession";
	
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
					addText (json_doc, "soil", SD_SOIL);
					addText (json_doc, "phenotype_gathering_notes", SD_PHENOTYPE_GATHERING);
					addText (json_doc, "study_design", SD_STUDY_DESIGN);

					addDateString (json_doc, "sowing_date", SD_SOWING_DATE);
					addDateString (json_doc, "harvest_date", SD_HARVEST_DATE);
					
					/*
					 * crop, previous crop, aspect, slope 
					 */
					addCrop (json_doc, "current_crop", SD_CURRENT_CROP);
					addCrop (json_doc, "previous_crop", SD_PREVIOUS_CROP);

					// slope
					addText (json_doc, "envo:00002000", SD_SLOPE);

					// aspect
					addAspect (json_doc);
					
					
					// phenotypes
					addPhenotypes (json_doc);

					// acccessions
					addAccessions (json_doc);
					
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
	
	
	void addCrop (JSONObject doc, String crop_key, String lucene_key) {
		addText (doc, crop_key, lucene_key);
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
				addString (SD_ASPECT, aspect_str);
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
							addText (SD_PHENOTYPE_NAME, name);						
							names_map.put (name, name);
						}
					}

					value = phenotype.get ("so:description");
					if (value != null) {
						String description = value.toString ();
						
						if (!descriptions_map.containsKey (description)) {
							addText (SD_PHENOTYPE_DESCRIPTION, description);						
							descriptions_map.put (description, description);
						}

					}
				}
			}
			
		}
		
	}


	void addAccessions (JSONObject doc) {		
		Object o = doc.get ("accessions");
		
		if (o != null) {
			if (o instanceof JSONArray) {
				JSONArray accessions = (JSONArray) o;				
				final int num_accessions = accessions.size ();
				
				for (int i = 0; i < num_accessions; ++ i) {
					o = accessions.get (i);

					String accession = o.toString ();
					
					addString (SD_ACCESSION, accession);
					
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
					addText (SD_PARENT_TRIAL, name);						
				}
			}
		}
		
	}

	
	
	@Override
	public String getUserFriendlyTypename() {
		return "Study";
	}
	

	static public void addQueryTerms (List <String> fields, Map <String, Float> boosts) {
		fields.add (SD_PARENT_TRIAL);
		fields.add (SD_PHENOTYPE_GATHERING);
		fields.add (SD_SOIL);
		fields.add (SD_STUDY_DESIGN);
		fields.add (SD_CURRENT_CROP);
		fields.add (SD_PREVIOUS_CROP);
		fields.add (SD_SLOPE);
		fields.add (SD_ASPECT);		
		fields.add (SD_PHENOTYPE_NAME);
		fields.add (SD_PHENOTYPE_DESCRIPTION);
		fields.add (SD_ACCESSION);
		
		if (boosts != null) {
			boosts.put (SD_ACCESSION, GD_NAME_BOOST);
			boosts.put (SD_PHENOTYPE_NAME, GD_NAME_BOOST);
		}
	}

}
