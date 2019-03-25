package uk.ac.earlham.grassroots.document;

import org.json.simple.JSONObject;

public class BlastServiceDocument extends GrassrootsDocument {

	
	public BlastServiceDocument (JSONObject json_doc) throws IllegalArgumentException {
		super (json_doc);
	}
	
	protected boolean addFields (JSONObject json_doc) {
		boolean success_flag = false;
		
		if (super.addFields (json_doc)) {			
			
			/*
			 * Add the BLAST database details by parsing the service config
			 */
			Object value = json_doc.get ("databases");
			
			if (value != null) {
				if (value instanceof JSONArray) {
					JSONArray items = (JSONArray) value;
					int num_imported = 0;
					
					for (Object o : items) {
						if (o instanceof JSONObject) {
							JSONObject item = (JSONObject) o;
							String input_key = getNameKey ();
							
							if (addText (item, input_key, GD_NAME, GD_NAME_BOOST)) {
								if (addText (item, input_key, GD_DESCRIPTION, GD_DESCRIPTION_BOOST)) {
									++ num_imported;
								} else {
									System.err.println ("Failed to add value for " + GD_DESCRIPTION + " with " + input_key + " from " + item.toString ());
								}
								
							} else {
								System.err.println ("Failed to add text for " + GD_NAME + " with " + input_key + " from " + item.toString ());
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
	
	public String getUserFriendlyTypename() {
		return "Service";
	}

}
