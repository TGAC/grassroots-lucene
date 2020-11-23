package uk.ac.earlham.grassroots.document.lucene;


import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;

import uk.ac.earlham.grassroots.document.json.AddressJSON;
import uk.ac.earlham.grassroots.document.lucene.util.DocumentWrapper;

public class AddressDocument extends MongoDocument {
	final static private String AD_PREFIX = "address-";
	final static public String AD_STREET = AD_PREFIX + "street";
	final static public String AD_LOCALITY = AD_PREFIX + "city";
	final static public String AD_REGION = AD_PREFIX + "county";
	final static public String AD_COUNTRY = AD_PREFIX + "country";
	final static public String AD_POSTCODE = AD_PREFIX + "postcode";

	
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
					
					addText (address_json, AddressJSON.AJ_STREET, AD_STREET);
					addText (address_json, AddressJSON.AJ_CITY, AD_LOCALITY);
					addText (address_json, AddressJSON.AJ_COUNTY, AD_REGION);
					addText (address_json, AddressJSON.AJ_COUNTRY, AD_COUNTRY);
					addText (address_json, AddressJSON.AJ_POSTCODE, AD_POSTCODE);
					
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
