package uk.ac.earlham.grassroots.document.lucene;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import uk.ac.earlham.grassroots.document.json.StudyJSON;
import uk.ac.earlham.grassroots.document.lucene.util.DocumentWrapper;

public class StudyDocument extends MongoDocument {
	final static public String SD_PREFIX = "study-";
	final static public String SD_PARENT_TRIAL = SD_PREFIX + "trial";
	final static public String SD_SOIL = SD_PREFIX + "soil";
	final static public String SD_PHENOTYPE_GATHERING = SD_PREFIX + "phenotype_gathering_notes";
	final static public String SD_STUDY_DESIGN = SD_PREFIX + "study_design";
	final static public String SD_CURRENT_CROP = SD_PREFIX + "current_crop";
	final static public String SD_PREVIOUS_CROP = SD_PREFIX + "previous_crop";
	final static public String SD_SOWING_DATE = SD_PREFIX + "sowing_date";
	final static public String SD_HARVEST_DATE = SD_PREFIX + "harvest_date";
	final static public String SD_SLOPE = SD_PREFIX + "slope";
	final static public String SD_ASPECT = SD_PREFIX + "aspect";
	final static public String SD_PHENOTYPE_NAME = SD_PREFIX + "phenotype_name";
	final static public String SD_PHENOTYPE_DESCRIPTION = SD_PREFIX + "phenotype_description";
	final static public String SD_ACCESSION = SD_PREFIX + "accession";
	final static public String SD_ADDRESS = SD_PREFIX + "address_id";
	final static public String SD_TREATMENT_NAME = SD_PREFIX + "treatment_name";
	final static public String SD_TREATMENT_SYNONYM = SD_PREFIX + "treatment_synonym";
	final static public String SD_TREATMENT_DESCRIPTION = SD_PREFIX + "treatment_description";
	
	
	public StudyDocument (JSONObject json_doc, DocumentWrapper wrapper) throws IllegalArgumentException {
		super (json_doc, wrapper);
	}
	
	
	protected boolean addFields (JSONObject json_doc) {
		boolean success_flag = false;
		
		if (super.addFields (json_doc)) {
			JSONObject parent_field_trial = (JSONObject) json_doc.get ("parent_field_trial");
			
			if (parent_field_trial != null) {	
				// parent field trial
				addParentFieldTrial (json_doc);
			}

			if (json_doc.get (StudyJSON.SJ_ADDRESS_ID) != null) {
				addMongoId (json_doc, StudyJSON.SJ_ADDRESS_ID, StudyDocument.SD_ADDRESS);
			}
							
			/*
			 * Add the study-specific fields
			 */
			addText (json_doc, StudyJSON.SJ_SOIL, SD_SOIL);
			addText (json_doc, StudyJSON.SJ_PHENOTYPE_GATHERING, SD_PHENOTYPE_GATHERING);
			addText (json_doc, StudyJSON.SJ_STUDY_DESIGN, SD_STUDY_DESIGN);
	
			addDateString (json_doc, StudyJSON.SJ_SOWING_DATE, SD_SOWING_DATE);
			addDateString (json_doc, StudyJSON.SJ_HARVEST_DATE, SD_HARVEST_DATE);
			
			/*
			 * crop, previous crop, aspect, slope 
			 */
			addCrop (json_doc, StudyJSON.SJ_CURRENT_CROP, SD_CURRENT_CROP);
			addCrop (json_doc, StudyJSON.SJ_PREVIOUS_CROP, SD_PREVIOUS_CROP);
	
			// slope
			addText (json_doc, StudyJSON.SJ_SLOPE, SD_SLOPE);
	
			// aspect
			addAspect (json_doc);
			
			
			// phenotypes
			addPhenotypes (json_doc);
	
			// acccessions
			addAccessions (json_doc);
	
			// treatments
			addTreatments (json_doc);
			
			success_flag = true;
						
		}
	
		return success_flag;
	}
	
	
	void addCrop (JSONObject doc, String crop_key, String lucene_key) {
		Object o = doc.get (crop_key);
		
		if (o != null) {
			if (o instanceof JSONObject) {
				o = ((JSONObject) o).get ("so:name");
				
				if (o != null)  {
					String s = o.toString ();
					addText (doc, lucene_key, s);
				}
			}
		}
	}
	
	void addAspect (JSONObject doc) {		
		String aspect = (String) doc.get (StudyJSON.SJ_ASPECT);
		
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
		Object o = doc.get (StudyJSON.SJ_PHENOTYPES);
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
		Object o = doc.get (StudyJSON.SJ_ACCESSIONS);
		
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
		Object o = doc.get (StudyJSON.SJ_PARENT_TRIAL);

		
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


	void addTreatments (JSONObject doc) {		
		Object o = doc.get (StudyJSON.SJ_TREATMENTS);
		
		if (o != null) {
			if (o instanceof JSONArray) {
				JSONArray treatment_factors = (JSONArray) o;				
				final int num_factors = treatment_factors.size ();
				
				for (int i = 0; i < num_factors; ++ i) {
					JSONObject treatment_factor = (JSONObject) treatment_factors.get (i);

					o = treatment_factor.get ("treatment");
					
					if ((o != null) && (o instanceof JSONObject)) {
						JSONObject treatment = (JSONObject) o;
						
						o = treatment.get ("so:name");						
						if (o != null) {
							String name = o.toString ();
							addText (SD_TREATMENT_NAME, name);						
						}

						o = treatment.get ("so:description");						
						if (o != null) {
							String desc = o.toString ();
							addText (SD_TREATMENT_DESCRIPTION, desc);						
						}

						o = treatment.get ("synonyms");
						if (o != null) {
							if (o instanceof JSONArray) {
								JSONArray synonyms = (JSONArray) o;
								final int num_synonyms = synonyms.size ();
								
								for (int j = 0; j < num_synonyms; ++ j) {
									o = synonyms.get (j);
									
									String synonym = o.toString ();
									addText (SD_TREATMENT_SYNONYM, synonym);															
								}
								
							} else if (o instanceof JSONObject) {
								JSONObject synonym = (JSONObject) o;
								String syn_value = o.toString ();
								addText (SD_TREATMENT_SYNONYM, syn_value);																							
							}
								
						}
					}
					
				}
			}
			
		}
		
	}
	
	
	
	@Override
	public String getUserFriendlyTypename() {
		return "Study";
	}
	

	static public void addQueryTerms (List <String> fields, Map <String, Float> boosts, Map <String, String> string_fields) {
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
		fields.add (SD_TREATMENT_NAME);
		fields.add (SD_TREATMENT_DESCRIPTION);
		fields.add (SD_TREATMENT_SYNONYM);
		
		if (boosts != null) {
			boosts.put (SD_ACCESSION, GD_NAME_BOOST);
			boosts.put (SD_PHENOTYPE_NAME, GD_NAME_BOOST);
			boosts.put (SD_TREATMENT_NAME, GD_NAME_BOOST);
			boosts.put (SD_TREATMENT_SYNONYM, GD_NAME_BOOST);
		}
		
		if (string_fields != null) {
			string_fields.put (SD_ACCESSION, SD_ACCESSION);
			string_fields.put (SD_ASPECT, SD_ASPECT);
		}
		
	}

	
	static public boolean isFieldMultiValued (String field) {		
		if ((field.equals (SD_PHENOTYPE_NAME)) || (field.equals (SD_PHENOTYPE_DESCRIPTION)) || (field.equals (SD_ACCESSION))) {
			return true;
		} else if ((field.equals (SD_TREATMENT_NAME)) || (field.equals (SD_TREATMENT_DESCRIPTION)) || (field.equals (SD_TREATMENT_SYNONYM))) {
			return true;
		} else {		
			return MongoDocument.isFieldMultiValued (field);
		}
	}
	
}
