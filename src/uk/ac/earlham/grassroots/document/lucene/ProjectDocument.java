package uk.ac.earlham.grassroots.document.lucene;

import java.util.List;
import java.util.Map;

import org.apache.lucene.search.BooleanQuery;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import uk.ac.earlham.grassroots.document.lucene.util.DocumentWrapper;

public class ProjectDocument extends GrassrootsDocument {
	final static private String PR_PREFIX = "project-";
	final static private String PR_AUTHOR = PR_PREFIX + "author";
	final static private String PR_GRANT_CODE = PR_PREFIX + "grant_code";


	public ProjectDocument (JSONObject json_doc, DocumentWrapper wrapper) throws IllegalArgumentException {
		super (json_doc, wrapper);
	}


	protected boolean addFields (JSONObject json_doc) {
		boolean success_flag = false;
		
		if (super.addFields (json_doc)) {
			
			/*
			 * Add the project-specific fields
			 */
			if (addAuthors (json_doc)) {
				if (addProjectCodes (json_doc)) {
					if (addUrl (json_doc)) {
						success_flag = true;
					}
				}
			}
		}
	
		return success_flag;
	}
	
	public String getUserFriendlyTypename () {
		return "Dataset";
	}
	
	public String getNameKey () {
		return "projectName";
	}


	public String getDescriptionKey () {
		return "description";
	}

	
	
	private boolean addAuthors (JSONObject json_doc) {
		return addArrayOfStrings (json_doc, "authors", PR_AUTHOR, false);
	}


	private boolean addProjectCodes (JSONObject json_doc) {
		return addArrayOfStrings (json_doc, "project_codes", PR_GRANT_CODE, false);
	}

	
	private boolean addArrayOfStrings (JSONObject json_doc, String array_key, String doc_key, boolean as_string_flag) {
		boolean success_flag = true;
		
		Object obj = json_doc.get (array_key);
		
		if (obj != null) {
			if (obj instanceof JSONArray) {
				JSONArray target_array = (JSONArray) obj;
				final int last_index = target_array.size () - 1;
				int i = 0;
				
				for (i = 0; i <= last_index; ++ i) {
					Object o = target_array.get (i);
					String s = o.toString ();
				
					if (as_string_flag) {
						if (!addString (doc_key, s)) {
							success_flag = false;
						}						
					} else {
						if (!addText (doc_key, s)) {
							success_flag = false;
						}						
					}
				}				
			}
		}
	
		return success_flag;		
	}

	
	private boolean addUrl (JSONObject json_doc) {
		return addString (json_doc, "url", GD_PUBLIC_LINK);
	}


	@Override
	public boolean setId (JSONObject json_doc) {
		boolean success_flag = false;
		String s = (String) json_doc.get ("uuid");
		
		if (s != null) {
			gd_unique_id = s;
			success_flag = true;
		}
		
		return success_flag;
	}


	static public void addQueryTerms (List <String> fields, Map <String, Float> boosts) {
		fields.add (PR_AUTHOR);
		fields.add (PR_GRANT_CODE);
	}

}
