package uk.ac.earlham.grassroots.document;


import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;

import uk.ac.earlham.grassroots.document.util.DocumentWrapper;

public class AddressDocument extends MongoDocument {
	final static private String AD_PREFIX = "address-";
	final static private String AD_STREET = AD_PREFIX + "street";
	final static private String AD_LOCALITY = AD_PREFIX + "city";
	final static private String AD_REGION = AD_PREFIX + "county";
	final static private String AD_COUNTRY = AD_PREFIX + "country";
	final static private String AD_POSTCODE = AD_PREFIX + "postcode";

	
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
					
					addText (address_json, "streetAddress", AD_STREET);
					addText (address_json, "addressLocality", AD_LOCALITY);
					addText (address_json, "addressRegion", AD_REGION);
					addText (address_json, "addressCountry", AD_COUNTRY);
					addText (address_json, "postalCode", AD_POSTCODE);
					
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
	public String getUserFriendlyTypename () {
		return "Location";
	}


	static public void addQueryTerms (List <String> fields, Map <String, Float> boosts) {
		fields.add (AD_STREET);
		fields.add (AD_LOCALITY);
		fields.add (AD_REGION);
		fields.add (AD_COUNTRY);
		fields.add (AD_POSTCODE);
	}

}
