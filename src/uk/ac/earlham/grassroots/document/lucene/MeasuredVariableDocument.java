package uk.ac.earlham.grassroots.document.lucene;


import java.util.List;
import java.util.Map;

import org.apache.lucene.facet.FacetField;
import org.apache.lucene.facet.FacetsConfig;
import org.json.simple.JSONObject;

import uk.ac.earlham.grassroots.document.json.MeasuredVariableJSON;
import uk.ac.earlham.grassroots.document.lucene.util.DocumentWrapper;


public class MeasuredVariableDocument extends MongoDocument {
	final static private String MVD_TYPE_NAME = "Measured Variable";
	
	final static private String MVD_PREFIX = "measured_variable-";
	final static public String MVD_TRAIT_NAME = MVD_PREFIX + "trait_name";
	final static public String MVD_TRAIT_DESCRIPTION = MVD_PREFIX + "trait_description";
	final static public String MVD_TRAIT_ABBREVIATION = MVD_PREFIX + "trait_abbreviation";
	final static public String MVD_TRAIT_ID = MVD_PREFIX + "trait_id";
	final static public String MVD_MEASUREMENT_NAME = MVD_PREFIX + "measurement_name";
	final static public String MVD_MEASUREMENT_DESCRIPTION = MVD_PREFIX + "measurement_description";
	final static public String MVD_MEASUREMENT_ID = MVD_PREFIX + "measurement_id";
	final static public String MVD_UNIT_NAME = MVD_PREFIX + "unit_name";
	final static public String MVD_UNIT_ID = MVD_PREFIX + "unit_id";
	final static public String MVD_VARIABLE_NAME = MVD_PREFIX + "variable_name";
	final static public String MVD_VARIABLE_ID = MVD_PREFIX + "variable_id";
	final static public String MVD_ONTOLOGY_NAME = MVD_PREFIX + "ontology_name";
	final static public String MVD_ONTOLOGY_ID = MVD_PREFIX + "ontology_id";
	final static public String MVD_ONTOLOGY_CROP = MVD_PREFIX + "crop";
	
	
	public MeasuredVariableDocument (JSONObject json_doc, DocumentWrapper wrapper) throws IllegalArgumentException {
		super (json_doc, wrapper);
	}
	
	
	protected boolean addFields (JSONObject json_doc) {
		boolean success_flag = false;
		
		/*
		{
			"unit": {
				"so:name": "ppm",
				"so:sameAs": "ROTH_UNIT:000239",
				"so:description": "ppm",
				"abbreviation": "ppm"
			},
			"@type": "Grassroots:MeasuredVariable",
			"variable": {
				"so:name": "ZnDe_CalcIcpYldRe_ppm",
				"so:sameAs": "ROTH_VARIABLE:000538",
				"so:description": "ZnDe_CalcIcpYldRe_ppm"
			},
			"trait": {
				"so:name": "Zinc deviation",
				"so:sameAs": "ROTH_TRAIT:000507",
				"so:description": "Zinc deviation from a regression line plotted through concentration against grain yield.",
				"abbreviation": "ZnDe"
			},
			"scale": {
				"so:name": "Numerical",
				"class_type": "xsd:double"
			},
			"_id": {
				"$oid": "625ebcac721eed64c607472c"
			},
			"measurement": {
				"so:name": "Calculation from ICP data and grain yield (at 100% DM) data.",
				"so:sameAs": "ROTH_MEAS:000494",
				"so:description": "Zinc deviation calculated from a regression line plotted on grain concentration (from ICP analysis) against grain yield at 100% Dry Matter.",
				"abbreviation": "CalcIcpYldRe"
			},
			"type_description": "Measured Variable"
		}		 
		*/
		
		if (super.addFields (json_doc)) {
			indexMeasuredVariable (this, json_doc);
			
			success_flag = true;
		}
		
		return success_flag;
	}
	
	
	public void index (JSONObject json_doc) {
		MeasuredVariableDocument.indexMeasuredVariable (this, json_doc);
	}
	
	public static void indexMeasuredVariable (GrassrootsDocument grassroots_doc, JSONObject json_doc) {
		/*
		 * Add the treatment-specific fields
		 */
		JSONObject child = (JSONObject) json_doc.get (MeasuredVariableJSON.MVJ_TRAIT);

		if (child != null) {
			grassroots_doc.addText (child, MeasuredVariableJSON.MVJ_TERM_NAME, MeasuredVariableDocument.MVD_TRAIT_NAME);
			grassroots_doc.addText (child, MeasuredVariableJSON.MVJ_TERM_DESCRIPTION, MeasuredVariableDocument.MVD_TRAIT_DESCRIPTION);
			grassroots_doc.addString (child, MeasuredVariableJSON.MVJ_TERM_ABBREVIATION, MeasuredVariableDocument.MVD_TRAIT_ABBREVIATION);
			grassroots_doc.addString (child, MeasuredVariableJSON.MVJ_TERM_URL, MeasuredVariableDocument.MVD_TRAIT_ID);				
		}

		child = (JSONObject) json_doc.get (MeasuredVariableJSON.MVJ_MEASUREMENT);
		if (child != null) {
			grassroots_doc.addText (child, MeasuredVariableJSON.MVJ_TERM_NAME, MeasuredVariableDocument.MVD_MEASUREMENT_NAME);
			grassroots_doc.addText (child, MeasuredVariableJSON.MVJ_TERM_DESCRIPTION, MeasuredVariableDocument.MVD_MEASUREMENT_DESCRIPTION);
			grassroots_doc.addString (child, MeasuredVariableJSON.MVJ_TERM_URL, MeasuredVariableDocument.MVD_MEASUREMENT_ID);				
		}

		child = (JSONObject) json_doc.get (MeasuredVariableJSON.MVJ_UNIT);
		if (child != null) {
			grassroots_doc.addText (child, MeasuredVariableJSON.MVJ_TERM_NAME, MeasuredVariableDocument.MVD_UNIT_NAME);
			grassroots_doc.addString (child, MeasuredVariableJSON.MVJ_TERM_URL, MeasuredVariableDocument.MVD_UNIT_ID);				
		}
		
		child = (JSONObject) json_doc.get (MeasuredVariableJSON.MVJ_VARIABLE);
		if (child != null) {
			grassroots_doc.addText (child, MeasuredVariableJSON.MVJ_TERM_NAME, MeasuredVariableDocument.MVD_VARIABLE_NAME);
			grassroots_doc.addString (child, MeasuredVariableJSON.MVJ_TERM_URL, MeasuredVariableDocument.MVD_VARIABLE_ID);				
		}
		
		child = (JSONObject) json_doc.get (MeasuredVariableJSON.MVJ_ONTOLOGY);
		if (child != null) {
			grassroots_doc.addText (child, MeasuredVariableJSON.MVJ_TERM_NAME, MeasuredVariableDocument.MVD_ONTOLOGY_NAME);
			grassroots_doc.addString (child, MeasuredVariableJSON.MVJ_TERM_URL, MeasuredVariableDocument.MVD_ONTOLOGY_ID);				
			
			/* Add crop as facet rather than as string */
			//grassroots_doc.addString (child, MeasuredVariableJSON.MVJ_CROP, MeasuredVariableDocument.MVD_ONTOLOGY_CROP);	
			
			String value = (String) json_doc.get (MeasuredVariableJSON.MVJ_CROP);
			
			if (value != null) {
				FacetField crop_facet = new FacetField (MeasuredVariableDocument.MVD_ONTOLOGY_CROP, value);
				grassroots_doc.gd_wrapper.addFacet (MeasuredVariableDocument.MVD_ONTOLOGY_CROP, value);
			} 


		} else {
			System.err.println ("No Ontology in " + child);
		}
			

		
	}
	
	@Override
	public String getUserFriendlyTypename() {
		return MVD_TYPE_NAME;
	}

	
	@Override
	protected boolean addFacet (JSONObject json_doc) {
		FacetField crop_facet = null;
		JSONObject child = (JSONObject) json_doc.get (MeasuredVariableJSON.MVJ_ONTOLOGY);

		if (child != null) {
			String value = (String) json_doc.get (MeasuredVariableJSON.MVJ_CROP);
			
			if (value != null) {
				crop_facet = new FacetField (GD_DATATYPE, getUserFriendlyTypename (), value);
			} 
		}

		if (crop_facet == null) {
			crop_facet = new FacetField (GD_DATATYPE, getUserFriendlyTypename ());
		}
		
		gd_wrapper.addFacet (crop_facet);

		
		return true;
	}
	
	
	public String getNameKey () {
		return null;
	}

	
	static public void setUpFacetsConfig (FacetsConfig facets_config) {
		facets_config.setHierarchical (MeasuredVariableDocument.MVD_TYPE_NAME, true);
	}
	
	
	static public void addQueryTerms (List <String> fields, Map <String, Float> boosts, Map <String, String> string_fields) {
		fields.add (MVD_TRAIT_NAME);
		fields.add (MVD_TRAIT_DESCRIPTION);
		fields.add (MVD_TRAIT_ABBREVIATION);
		fields.add (MVD_TRAIT_ID);
		fields.add (MVD_MEASUREMENT_NAME);
		fields.add (MVD_MEASUREMENT_DESCRIPTION);
		fields.add (MVD_MEASUREMENT_ID);
		fields.add (MVD_UNIT_NAME);
		fields.add (MVD_UNIT_ID);		
		fields.add (MVD_VARIABLE_NAME);
		fields.add (MVD_VARIABLE_ID);
		
		if (boosts != null) {
			boosts.put (MVD_TRAIT_NAME, GD_NAME_BOOST);
			boosts.put (MVD_MEASUREMENT_NAME, GD_NAME_BOOST);
			boosts.put (MVD_UNIT_NAME, GD_NAME_BOOST);
		}
		
		if (string_fields != null) {
			string_fields.put (MVD_TRAIT_ABBREVIATION, MVD_TRAIT_ABBREVIATION);
			string_fields.put (MVD_TRAIT_ID, MVD_TRAIT_ID);
			string_fields.put (MVD_MEASUREMENT_ID, MVD_MEASUREMENT_ID);
			string_fields.put (MVD_UNIT_ID, MVD_UNIT_ID);
			string_fields.put (MVD_VARIABLE_ID, MVD_VARIABLE_ID);
		}
	}
	
	
	static public boolean isFieldMultiValued (String field) {			
		return MongoDocument.isFieldMultiValued (field);
	}
}
