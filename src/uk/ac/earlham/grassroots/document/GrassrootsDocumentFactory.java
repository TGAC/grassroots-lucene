package uk.ac.earlham.grassroots.document;


import org.json.simple.JSONObject;

import uk.ac.earlham.grassroots.document.GrassrootsDocument;


public class GrassrootsDocumentFactory {

	static public GrassrootsDocument createDocument (JSONObject json_doc) {
		GrassrootsDocument doc = null;
		Object obj_type = json_doc.get ("@type");
		
		if (obj_type != null) {
			String datatype = obj_type.toString ();
			
			switch (datatype) {
				case "FieldTrial":
					doc = new FieldTrialDocument (json_doc);
					break;
					
				case "Study": 
					doc = new StudyDocument (json_doc);					
					break;
					
				default:
					break;
			}	
		}
		
		return doc;
	}
}
