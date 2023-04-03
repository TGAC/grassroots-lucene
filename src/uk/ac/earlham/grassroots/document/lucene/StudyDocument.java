package uk.ac.earlham.grassroots.document.lucene;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
	final static public String SD_CURATOR = SD_PREFIX + "curator";
	final static public String SD_CONTACT = SD_PREFIX + "contact";
	final static public String SD_PLAN_CHANGES = SD_PREFIX + "plan_changes";
	final static public String SD_DATA_NOT_INCLUDED = SD_PREFIX + "data_not_included";
	final static public String SD_PHYSICAL_SAMPLES_COLLECTED = SD_PREFIX + "physical_samples_collected";
	final static public String SD_IMAGE_NOTES = SD_PREFIX + "image_collection_notes";
	final static public String SD_SHAPE_NOTES = SD_PREFIX + "shape_data_notes";

	final static public String SD_CURATOR_NAME = SD_PREFIX + "curator_name";
	final static public String SD_CURATOR_EMAIL = SD_PREFIX + "curator_email";
	final static public String SD_CURATOR_ROLE = SD_PREFIX + "curator_role";
	final static public String SD_CURATOR_AFFILIATION = SD_PREFIX + "curator_affiliation";
	final static public String SD_CURATOR_ORCID = SD_PREFIX + "curator_orcid";



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

			addText (json_doc, StudyJSON.SJ_PLAN_CHANGES, SD_PLAN_CHANGES);
			addText (json_doc, StudyJSON.SJ_DATA_NOT_INCLUDED, SD_DATA_NOT_INCLUDED);
			addText (json_doc, StudyJSON.SJ_PHYSICAL_SAMPLES_COLLECTED, SD_PHYSICAL_SAMPLES_COLLECTED);
			addText (json_doc, StudyJSON.SJ_IMAGE_NOTES, SD_IMAGE_NOTES);
			addText (json_doc, StudyJSON.SJ_SHAPE_NOTES, SD_SHAPE_NOTES);

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

			// people
			addPerson (json_doc, StudyJSON.SJ_CURATOR, StudyDocument.SD_CURATOR);
			addPerson (json_doc, StudyJSON.SJ_CONTACT, StudyDocument.SD_CONTACT);


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


	/*
  "phenotypes": {
    "BM_Calc_tha": {
      "definition": {
        "@type": "Grassroots:MeasuredVariable",
        "type_description": "Measured Variable",
        "variable": {
          "so:sameAs": "CO_321:0001036",
          "so:name": "BM_Calc_tha"
        },
        "trait": {
          "so:sameAs": "CO_321:0000005",
          "so:name": "Aboveground biomass at maturity",
          "so:description": "All above-ground biomass at maturity.",
          "abbreviation": "BM"
        },
        "measurement": {
          "so:sameAs": "CO_321:0001027",
          "so:name": "BM Computation",
          "so:description": "Cut all aboveground biomass in a predetermined area (A). Avoid border effects by sampling away from edges of plot. Biomass as other yield components can be calculated or measured individually (Bell and Fischer, 1994; Reynolds et al., 2001; Pask et al., 2012), decide which method suit better for your objectives."
        },
        "unit": {
          "so:sameAs": "CO_321:0000432",
          "so:name": "t/ha"
        }
      }
    },
	 */

	void addPhenotypes (JSONObject doc) {
		Object o = doc.get (StudyJSON.SJ_PHENOTYPES);
		HashMap <String, String> names_map = new HashMap <String, String> ();
		HashMap <String, String> descriptions_map = new HashMap <String, String> ();

		if (o != null) {

			if (o instanceof JSONObject) {
				JSONObject phenotypes = (JSONObject) o;
				Set keys = phenotypes.keySet ();
				Iterator itr = keys.iterator ();

				while (itr.hasNext ()) {
					String key = itr.next ().toString ();

					o = phenotypes.get (key);

					if (o instanceof JSONObject) {
						o = ((JSONObject) o).get ("definition");

						if (o != null) {
							if (o instanceof JSONObject) {
								JSONObject definition = (JSONObject) o;

								MeasuredVariableDocument.indexMeasuredVariable (this, definition);
							}

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


	void addPerson (JSONObject doc, String input_json_key, String output_name_key, String output_email_key, String output_role_key, String output_affiliation_key, String output_orcid_key) {
		Object o = doc.get (input_json_key);

		if (o != null) {
			if (o instanceof JSONObject) {
				JSONObject curator = (JSONObject) o;

				/* name */
				o = curator.get ("so:name");
				if (o != null) {
					String name = o.toString ();
					addText (output_name_key, name);
				}

				/* email */
				o = curator.get ("so:email");
				if (o != null) {
					String name = o.toString ();
					addText (output_email_key, name);
				}

				/* role */
				o = curator.get ("so:roleName");
				if (o != null) {
					String name = o.toString ();
					addText (output_role_key, name);
				}

				/* affiliation */
				o = curator.get ("so:affiliation");
				if (o != null) {
					String name = o.toString ();
					addText (output_affiliation_key, name);
				}

				/* orcid */
				o = curator.get ("orcid");
				if (o != null) {
					String name = o.toString ();
					addString (output_orcid_key, name);
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
		fields.add (SD_CURATOR);
		fields.add (SD_CONTACT);
        fields.add (SD_PLAN_CHANGES);
        fields.add (SD_DATA_NOT_INCLUDED);
        fields.add (SD_PHYSICAL_SAMPLES_COLLECTED);
        fields.add (SD_IMAGE_NOTES);
        fields.add (SD_SHAPE_NOTES);

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
