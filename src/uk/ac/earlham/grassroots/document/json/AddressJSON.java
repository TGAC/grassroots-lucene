package uk.ac.earlham.grassroots.document.json;

import java.util.Map;

import org.apache.lucene.document.Document;

import uk.ac.earlham.grassroots.document.lucene.AddressDocument;
import uk.ac.earlham.grassroots.document.lucene.MongoDocument;

public class AddressJSON extends GrassrootsJSON {
	final public static String AJ_STREET = "streetAddress";
	final public static String AJ_CITY = "addressLocality";
	final public static String AJ_COUNTY = "addressRegion";
	final public static String AJ_COUNTRY = "addressCountry";
	final public static String AJ_POSTCODE = "postalCode";
	
	
	public AddressJSON (Document doc, Map <String, String []> highlights) {
		super (doc, highlights);		
	}
	

	public boolean addToJSON (Document doc, Map <String, String []> highlights) {
		boolean b = super.addToJSON (doc, highlights);
		
		if (b) {
			addJSONField (doc, AddressDocument.AD_STREET, AddressJSON.AJ_STREET);
			addJSONField (doc, AddressDocument.AD_STREET, AddressJSON.AJ_CITY);
			addJSONField (doc, AddressDocument.AD_STREET, AddressJSON.AJ_COUNTY);
			addJSONField (doc, AddressDocument.AD_STREET, AddressJSON.AJ_COUNTRY);
		}
		
		return b;
	}

}
