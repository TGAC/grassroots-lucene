package uk.ac.earlham.grassroots.document;


import org.json.simple.JSONObject;

import uk.ac.earlham.grassroots.document.GrassrootsDocument;
import uk.ac.earlham.grassroots.document.util.DocumentWrapper;


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
	static public GrassrootsDocument createDocument (JSONObject json_doc, DocumentWrapper wrapper) {
		GrassrootsDocument doc = null;
		Object obj_type = json_doc.get ("@type");
		
		if (obj_type != null) {
			String datatype = obj_type.toString ();
			
			switch (datatype) {
				case "Grassroots:FieldTrial":
					doc = new FieldTrialDocument (json_doc, wrapper);
					break;
					
				case "Grassroots:Study": 
					doc = new StudyDocument (json_doc, wrapper);					
					break;

				case "Grassroots:Phenotype": 
					doc = new TreatmentDocument (json_doc, wrapper);					
					break;
					
				case "Grassroots:Location": 
					doc = new AddressDocument (json_doc, wrapper);					
					break;
					
				case "Grassroots:Service": {
					Object o = json_doc.get ("so:name");
					
					if (o != null) {
						String name = o.toString ();
						
						switch (name) {
							case "BlastN":
							case "BlastP":
							case "BlastX":
								doc = new BlastServiceDocument (json_doc, wrapper);	
								break;

							case "Scaffold":
								doc = new SamtoolsServiceDocument (json_doc, wrapper);	
								break;
								
							default:
								System.out.println ("Unknown Grassroots:Service: \"" + name + "\"");
								break;
						}
					}					
				}
				break;
					
				case "grassroots:project":
					doc = new ProjectDocument (json_doc, wrapper);
					break;
				
				default:
					System.err.println ("Unknown @type: \"" + datatype + "\"");
					break;
			}	
		}
		
		return doc;
	}
	
}
