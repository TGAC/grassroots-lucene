package uk.ac.earlham.grassroots.document;


import org.json.simple.JSONObject;

import uk.ac.earlham.grassroots.document.util.DocumentWrapper;

public class AddressDocument extends MongoDocument {
	
	public AddressDocument (JSONObject json_doc, DocumentWrapper wrapper) throws IllegalArgumentException {
		super (json_doc, wrapper);
	}
	
	
	protected boolean addFields (JSONObject json_doc) {
		boolean success_flag = false;
		
		if (super.addFields (json_doc)) {
			
			/*
			 * Add the address-specific fields
			 */
			if (addText (json_doc, "streetAddress")) {
				if (addText (json_doc, "addressLocality")) {
					if (addText (json_doc, "addressRegion")) {
						if (addText (json_doc, "addressCountry")) {
							if (addText (json_doc, "postalCode")) {
								success_flag = true;
							}
						}
					}
				}
			}
		}
	
		return success_flag;
	}
	
	
	public String getNameKey () {
		return "name";
	}
	
	@Override
	public String getUserFriendlyTypename() {
		return "Address";
	}
}
