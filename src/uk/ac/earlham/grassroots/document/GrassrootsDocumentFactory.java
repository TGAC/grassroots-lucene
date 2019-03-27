package uk.ac.earlham.grassroots.document;


import org.json.simple.JSONObject;

import uk.ac.earlham.grassroots.document.GrassrootsDocument;


/**
 * A factory class for inspecting the JSON data sent by Grassroots and 
 * creates the appropriate GrassrootsDocument.
 * 
 * @author billy
 *
 */
public class GrassrootsDocumentFactory {

	/**
	 * Create the GrassrootsDocument for a given JSON object.
	 * 
	 * @param json_doc The JSON object created from the Grassroots JSON.
	 * @return The appropriate GrassrootsDocument or <code>null<code> upon
	 * error
	 */
	static public GrassrootsDocument createDocument (JSONObject json_doc) {
		GrassrootsDocument doc = null;
		Object obj_type = json_doc.get ("@type");
		
		if (obj_type != null) {
			String datatype = obj_type.toString ();
			
			switch (datatype) {
				case "Grassroots:FieldTrial":
					doc = new FieldTrialDocument (json_doc);
					break;
					
				case "Grassroots:Study": 
					doc = new StudyDocument (json_doc);					
					break;
					
				case "Grassroots:Address": 
					doc = new AddressDocument (json_doc);					
					break;
					
				case "Grassroots:Service": {
					Object o = json_doc.get ("so:name");
					
					if (o != null) {
						String name = o.toString ();
						
						switch (name) {
							case "BlastN":
							case "BlastP":
							case "BlastX":
								doc = new BlastServiceDocument (json_doc);	
								break;

							case "Scaffold":
								doc = new SamtoolsServiceDocument (json_doc);	
								break;
								
							default:
								System.out.println ("Unknown Grassroots:Service: \"" + name + "\"");
								break;
						}
					}					
				}
				break;
					
				default:
					System.out.println ("Unknown @type: \"" + datatype + "\"");
					break;
			}	
		}
		
		return doc;
	}
	
}
