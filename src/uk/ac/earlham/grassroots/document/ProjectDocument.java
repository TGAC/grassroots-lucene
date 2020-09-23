package uk.ac.earlham.grassroots.document;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import uk.ac.earlham.grassroots.document.util.DocumentWrapper;

public class ProjectDocument extends GrassrootsDocument {

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


	@Override
	public String getUniqueIdKey () {
		return "uuid";
	}

	
	public String getNameKey () {
		return "projectName";
	}


	public String getDescriptionKey () {
		return "description";
	}

	
	
	private boolean addAuthors (JSONObject json_doc) {
		return addArrayOfStrings (json_doc, "authors", "author", false);
	}


	private boolean addProjectCodes (JSONObject json_doc) {
		return addArrayOfStrings (json_doc, "project_codes", "grant_code", false);
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
	
}
