package uk.ac.earlham.grassroots.document.lucene;

import org.json.simple.JSONObject;

import uk.ac.earlham.grassroots.document.lucene.util.DocumentWrapper;


public class SamtoolsServiceDocument extends ServiceDocument {

	public SamtoolsServiceDocument (JSONObject json_doc, DocumentWrapper wrapper) throws IllegalArgumentException {
		super (json_doc, wrapper);
	}
	
	public String getDatasetsKey () {
		return "index_files";
	}


	protected String getDatasetNameKey () {
		return "Blast database";
	}

	static public boolean isFieldMultiValued (String field) {
		return GrassrootsDocument.isFieldMultiValued (field);
	}
}
