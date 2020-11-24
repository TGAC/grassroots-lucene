package uk.ac.earlham.grassroots.document.json;

import java.util.Map;

import org.apache.lucene.document.Document;

import uk.ac.earlham.grassroots.document.lucene.GrassrootsDocument;
import uk.ac.earlham.grassroots.document.lucene.ProjectDocument;

public class ProjectJSON extends GrassrootsJSON {
	final public static String PR_AUTHORS = "authors";
	final public static String PR_PROJECT_CODES = "project_codes";
	final public static String PR_URL = "so:url";
	final public static String PR_ID = "uuid";

	public ProjectJSON (Document doc, Map <String, String []> highlights, int highlighter_index) {
		super (doc, highlights, highlighter_index);	
	}
	

	public boolean addToJSON (Document doc) {
		boolean b = super.addToJSON (doc);
		
		if (b) {
			addJSONMultiValuedField (doc, ProjectDocument.PD_AUTHOR, ProjectJSON.PR_AUTHORS);
			addJSONMultiValuedField (doc, ProjectDocument.PD_GRANT_CODE, ProjectJSON.PR_PROJECT_CODES);
			addJSONField (doc, GrassrootsDocument.GD_PUBLIC_LINK, ProjectJSON.PR_URL);
		}
		
		return b;
	}
	
	
	public String getIdKey () {
		return ProjectJSON.PR_ID;
	}

}
