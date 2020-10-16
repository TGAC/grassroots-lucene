package uk.ac.earlham.grassroots.document;

import org.json.simple.JSONObject;

import uk.ac.earlham.grassroots.document.util.DocumentWrapper;

public class BlastServiceDocument extends ServiceDocument {

	
	public BlastServiceDocument (JSONObject json_doc, DocumentWrapper wrapper) throws IllegalArgumentException {
		super (json_doc, wrapper);
	}
	

	protected boolean addFields (JSONObject json_doc) {
		boolean success_flag = false;
		
		if (super.addFields (json_doc)) {
			
			if (addText (json_doc, "service")) {
				
				/*
				 * Add the Blast-specific fields
				 */
				if (addNonIndexedString (json_doc, GD_INTERNAL_LINK)) {
					Object obj = json_doc.get ("payload");
					
					if ((obj != null) && (obj instanceof JSONObject)) {
						JSONObject payload = (JSONObject) obj;
						String payload_str = payload.toJSONString ();
						
						if (addNonIndexedString ("payload", payload_str)) {
							success_flag = true;
						}
										
					}
				}
			}
		}
	
		return success_flag;
	}


}
