package uk.ac.earlham.grassroots.document;

import org.json.simple.JSONObject;

public class BlastServiceDocument extends DatasetsServiceDocument {

	
	public BlastServiceDocument (JSONObject json_doc) throws IllegalArgumentException {
		super (json_doc);
	}
	

	public String getDatasetsKey () {
		return "databases";
	}

	
	protected String getDatasetNameKey () {
		return "name";
	}

	
	protected String getDatasetDescriptionKey () {
		return "description";
	}

}
