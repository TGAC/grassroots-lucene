package uk.ac.earlham.grassroots.document;


import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import uk.ac.earlham.grassroots.document.util.DocumentWrapper;


abstract public class DatasetsServiceDocument extends ServiceDocument {
	
	public DatasetsServiceDocument (JSONObject json_doc, DocumentWrapper wrapper) throws IllegalArgumentException {
		super (json_doc, wrapper);
	}
	
	protected boolean addFields (JSONObject json_doc) {
		boolean success_flag = false;
		
		if (super.addFields (json_doc)) {						
			Object value = json_doc.get (getDatasetsKey ());
			
			if (value != null) {
				if (value instanceof JSONArray) {
					JSONArray items = (JSONArray) value;
					int num_imported = 0;
					
					for (Object o : items) {
						if (o instanceof JSONObject) {
							JSONObject item = (JSONObject) o;
							final String name_key = getDatasetNameKey ();
							final String description_key = getDescriptionKey ();
							
							if (addText (item, name_key, GD_NAME)) {
								if (addText (item, description_key, GD_DESCRIPTION)) {
									++ num_imported;
								} else {
									System.err.println ("Failed to add value for " + GD_DESCRIPTION + " with " + description_key + " from " + item.toString ());
								}
								
							} else {
								System.err.println ("Failed to add text for " + GD_NAME + " with " + name_key + " from " + item.toString ());
							}
						}
					}			
					
					if (num_imported == items.size ()) {
						success_flag = true;
					}
				}
			}
		}
	
		return success_flag;
	}

	
	protected String getDatasetNameKey () {
		return "projectName";
	}

	
	public String geDescriptionKey () {
		return "desccription";
	}


	abstract protected String getDatasetsKey ();

	
	public String getUniqueIdKey () {
		return "uuid";		
	}

	
}
