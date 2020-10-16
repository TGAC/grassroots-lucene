package uk.ac.earlham.grassroots.document;


import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import uk.ac.earlham.grassroots.document.util.DocumentWrapper;

public class AddressDocument extends MongoDocument {
	static private String AD_STREET = "streetAddress";
	static private String AD_LOCALITY = "addressLocality";
	static private String AD_REGION = "addressRegion";
	static private String AD_COUNTRY = "addressCountry";
	static private String AD_POSTCODE = "postalCode";

	
	public AddressDocument (JSONObject json_doc, DocumentWrapper wrapper) throws IllegalArgumentException {
		super (json_doc, wrapper);
	}
	
	
	protected boolean addFields (JSONObject json_doc) {
		boolean success_flag = false;
		
		if (super.addFields (json_doc)) {
			
			/*
			 * Add the address-specific fields
			 */
			JSONObject temp_json = (JSONObject) json_doc.get ("address");
			
			if (temp_json != null) {
				JSONObject address_json = (JSONObject) temp_json.get ("Address");
				
				if (address_json != null) {
					
					addText (address_json, AD_STREET);
					addText (address_json, AD_LOCALITY);
					addText (address_json, AD_REGION);
					addText (address_json, AD_COUNTRY);
					addText (address_json, AD_POSTCODE);
					
				}
			}		

			success_flag = true;
		}
	
		return success_flag;
	}

	
	
	public String getNameKey () {
		return "name";
	}
	
	@Override
	public String getUserFriendlyTypename() {
		return "Location";
	}
}
