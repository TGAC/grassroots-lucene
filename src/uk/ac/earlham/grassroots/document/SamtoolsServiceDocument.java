package uk.ac.earlham.grassroots.document;

import org.json.simple.JSONObject;


public class SamtoolsServiceDocument extends DatasetsServiceDocument {

	public SamtoolsServiceDocument (JSONObject json_doc) throws IllegalArgumentException {
		super (json_doc);
	}
	
	public String getDatasetsKey () {
		return "index_files";
	}


	protected String getDatasetNameKey () {
		return "Blast database";
	}

}
