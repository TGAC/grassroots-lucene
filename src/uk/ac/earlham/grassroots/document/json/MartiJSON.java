package uk.ac.earlham.grassroots.document.json;

import java.util.Map;

import org.apache.lucene.document.Document;

import uk.ac.earlham.grassroots.document.lucene.GrassrootsDocument;
import uk.ac.earlham.grassroots.document.lucene.MartiDocument;
import uk.ac.earlham.grassroots.document.lucene.ProjectDocument;

public class MartiJSON extends GrassrootsJSON {
	final public static String MA_URL = "so:url";
	final public static String MA_ID = "_id";
	final public static String MA_SITE = "site_name";
	
	public MartiJSON (Document doc, Map <String, String []> highlights, int highlighter_index) {
		super (doc, highlights, highlighter_index);	
	}
	

	public boolean addToJSON (Document doc) {
		if (super.addToJSON (doc)) {
			if (addJSONField (doc, GrassrootsDocument.GD_PUBLIC_LINK, MartiJSON.MA_URL)) {
				if (addJSONField (doc, MartiDocument.MD_SITE, MartiJSON.MA_SITE)) {
					return true;
				}				
			}
				
		}
		
		return false;
	}
	
	
	public String getIdKey () {
		return MartiJSON.MA_ID;
	}
	
}
