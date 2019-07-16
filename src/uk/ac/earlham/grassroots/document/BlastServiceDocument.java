package uk.ac.earlham.grassroots.document;

import org.json.simple.JSONObject;

import uk.ac.earlham.grassroots.document.util.DocumentWrapper;

public class BlastServiceDocument extends DatasetsServiceDocument {

	
	public BlastServiceDocument (JSONObject json_doc, DocumentWrapper wrapper) throws IllegalArgumentException {
		super (json_doc, wrapper);
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
