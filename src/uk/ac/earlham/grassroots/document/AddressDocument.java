package uk.ac.earlham.grassroots.document;


import org.json.simple.JSONObject;

public class AddressDocument extends GrassrootsDocument {
	
	public AddressDocument (JSONObject json_doc) throws IllegalArgumentException {
		super (json_doc);
	}
	
	
	protected boolean addFields (JSONObject json_doc) {
		boolean success_flag = false;
		
		if (super.addFields (json_doc)) {
			
			/*
			 * Add the address-specific fields
			 */
			if (addText (json_doc, "streetAddress", 1.0f)) {
				if (addText (json_doc, "addressLocality", 1.0f)) {
					if (addText (json_doc, "addressRegion", 1.0f)) {
						if (addText (json_doc, "addressCountry", 1.0f)) {
							if (addText (json_doc, "postalCode", 1.0f)) {
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
