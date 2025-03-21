package uk.ac.earlham.grassroots.document.lucene;

import java.util.List;
import java.util.Map;

import org.apache.lucene.facet.FacetsConfig;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import uk.ac.earlham.grassroots.document.json.MartiJSON;
import uk.ac.earlham.grassroots.document.json.StudyJSON;
import uk.ac.earlham.grassroots.document.lucene.util.DocumentWrapper;
import uk.ac.earlham.grassroots.document.lucene.util.Person;

public class MartiDocument extends MongoDocument {
	final static private String MD_PREFIX = "marti-";
	final static public String MD_SITE = MD_PREFIX + "site";
	final static public String MD_TAXA = MD_PREFIX + "taxa";


	public MartiDocument (JSONObject json_doc, DocumentWrapper wrapper) throws IllegalArgumentException {
		super (json_doc, wrapper);
	}


	protected boolean addFields (JSONObject json_doc) {
		boolean success_flag = false;
		
		if (super.addFields (json_doc)) {
			
			/*
			 * Add the project-specific fields
			 */
			if (addSite (json_doc)) {
				if (addTaxa (json_doc)) {
					success_flag = true;					
				} else {
					System.err.println ("MartiDocument.addTaxa failed");
				}
			} else {
				System.err.println ("MartiDocument.addSite failed");
			}

		}
	
		return success_flag;
	}
	
	public String getUserFriendlyTypename () {
		return "MARTi Sample";
	}
	
	public String getNameKey () {
		return "so:name";
	}


	public String getDescriptionKey () {
		return "description";
	}

	private boolean addSite (JSONObject json_doc) {
		return addText (json_doc, MartiJSON.MA_SITE, MartiDocument.MD_SITE);
	}

	
	private boolean addTaxa (JSONObject doc) {
		int num_taxa = 0;
		int num_imported = 0;
		
		Object o = doc.get (MartiJSON.MA_TAXA);

		if (o != null) {
			if (o instanceof JSONArray) {
				JSONArray taxa = (JSONArray) o;
				
				num_taxa = taxa.size ();
				
				for (int i = 0; i < num_taxa; ++ i) {
					o = taxa.get (i);
					
					if (o != null) {
						String taxonomy_id = o.toString ();
						
						if (!taxonomy_id.isBlank ()) {
							if (addString (MD_TAXA, taxonomy_id)) {
								++ num_imported;
							}
								
						}
					}

				}
			}

		}

		return (num_taxa == num_imported);
	}

	
	private boolean addUrl (JSONObject json_doc) {
		return addString (json_doc, MartiJSON.MA_URL, GrassrootsDocument.GD_PUBLIC_LINK);
	}

	
	static public void setUpFacetsConfig (FacetsConfig facets_config) {
	}


	static public void addQueryTerms (List <String> fields, Map <String, Float> boosts, Map <String, String> string_fields) {
		fields.add (MartiDocument.MD_SITE);
		//string_fields.put (MartiDocument.MD_TAXA, MartiDocument.MD_TAXA);
		fields.add (MartiDocument.MD_TAXA);
	}

}
