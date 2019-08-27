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
			if (addText (json_doc, AD_STREET)) {
				if (addText (json_doc, AD_LOCALITY)) {
					if (addText (json_doc, AD_REGION)) {
						if (addText (json_doc, AD_COUNTRY)) {
							if (addText (json_doc, AD_POSTCODE)) {
								success_flag = true;
							}
						}
					}
				}
			}
		}
	
		return success_flag;
	}
	
	
	public JSONArray getSchemaFields (JSONArray fields) {
		super.getSchemaFields (fields);
		
		addField (fields, AD_STREET, "solr.TextField", true, true);
		addField (fields, AD_LOCALITY, "solr.TextField", true, true);
		addField (fields, AD_REGION, "solr.TextField", true, true);
		addField (fields, AD_COUNTRY, "solr.TextField", true, true);
		addField (fields, AD_POSTCODE, "solr.StrField", true, true);
	
		return fields;
	}
	
	
	public String getNameKey () {
		return "name";
	}
	
	@Override
	public String getUserFriendlyTypename() {
		return "Address";
	}
}
