package uk.ac.earlham.grassroots.document.json;

import java.util.Map;

import org.apache.lucene.document.Document;

import uk.ac.earlham.grassroots.document.lucene.GrassrootsDocument;
import uk.ac.earlham.grassroots.document.lucene.MongoDocument;
import uk.ac.earlham.grassroots.document.lucene.StudyDocument;

public class StudyJSON extends MongoJSON {
	final static public String SJ_PARENT_TRIAL = "parent_field_trial";
	final static public String SJ_SOIL = "soil";
	final static public String SJ_PHENOTYPE_GATHERING = "phenotype_gathering_notes";
	final static public String SJ_STUDY_DESIGN = "study_design";
	final static public String SJ_CURRENT_CROP = "current_crop";
	final static public String SJ_PREVIOUS_CROP = "previous_crop";
	final static public String SJ_SOWING_DATE = "sowing_date";
	final static public String SJ_HARVEST_DATE = "harvest_date";
	final static public String SJ_SLOPE = "envo:00002000";
	final static public String SJ_ASPECT = "ncit:C42677";
	final static public String SJ_PHENOTYPES = "phenotypes";
	final static public String SJ_ACCESSIONS = "accessions";
	final static public String SJ_ADDRESS = "address";
	final static public String SJ_ADDRESS_ID = "address_id";
	final static public String SJ_TREATMENTS = "treatment_factors";
	final static public String SJ_CURATOR = "curator";
	final static public String SJ_CONTACT = "contact";
	final static public String SJ_PLAN_CHANGES = "plan_changes";
	final static public String SJ_DATA_NOT_INCLUDED = "data_not_included";
	final static public String SJ_PHYSICAL_SAMPLES_COLLECTED = "physical_samples_collected";
	final static public String SJ_IMAGE_NOTES = "image_collection_notes";
	final static public String SJ_SHAPE_NOTES = "shape_data_notes";
	
	public StudyJSON (Document doc, Map <String, String []> highlights, int highlighter_index) {
		super (doc, highlights, highlighter_index);		
	}

	public boolean addToJSON (Document doc) {
		boolean b = super.addToJSON (doc);
		
		if (b) {
			
			if (addJSONField (doc, StudyDocument.SD_STUDY_DESIGN, StudyJSON.SJ_STUDY_DESIGN)) {
				
				addJSONField (doc, StudyDocument.SD_PHENOTYPE_GATHERING, StudyJSON.SJ_PHENOTYPE_GATHERING);
				addJSONField (doc, StudyDocument.SD_CURATOR, StudyJSON.SJ_CURATOR);
				addJSONField (doc, StudyDocument.SD_CONTACT, StudyJSON.SJ_CONTACT);
				addJSONField (doc, StudyDocument.SD_PLAN_CHANGES, StudyJSON.SJ_PLAN_CHANGES);
				addJSONField (doc, StudyDocument.SD_DATA_NOT_INCLUDED, StudyJSON.SJ_DATA_NOT_INCLUDED);
				addJSONField (doc, StudyDocument.SD_PHYSICAL_SAMPLES_COLLECTED, StudyJSON.SJ_PHYSICAL_SAMPLES_COLLECTED);
				addJSONField (doc, StudyDocument.SD_IMAGE_NOTES, StudyJSON.SJ_IMAGE_NOTES);
				addJSONField (doc, StudyDocument.SD_SHAPE_NOTES, StudyJSON.SJ_SHAPE_NOTES);
				
				b = true;
			} else {
				b = false;
			}
		}
		
		return b;
	}
}
