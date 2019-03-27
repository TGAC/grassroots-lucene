package uk.ac.earlham.grassroots.document;


import org.json.simple.JSONArray;
import org.json.simple.JSONObject;


abstract public class DatasetsServiceDocument extends ServiceDocument {
	
	public DatasetsServiceDocument (JSONObject json_doc) throws IllegalArgumentException {
		super (json_doc);
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
							final String description_key = getDatasetDescriptionKey ();
							
							if (addText (item, name_key, GD_NAME, GD_NAME_BOOST)) {
								if (addText (item, description_key, GD_DESCRIPTION, GD_DESCRIPTION_BOOST)) {
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
		return GD_NAME;
	}

	
	protected String getDatasetDescriptionKey () {
		return GD_DESCRIPTION;
	}


	abstract protected String getDatasetsKey ();
	
}
