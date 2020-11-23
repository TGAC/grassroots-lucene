package uk.ac.earlham.grassroots.document.lucene;

import org.json.simple.JSONObject;

import uk.ac.earlham.grassroots.document.json.BlastServiceJSON;
import uk.ac.earlham.grassroots.document.lucene.util.DocumentWrapper;

public class BlastServiceDocument extends ServiceDocument {

	
	public BlastServiceDocument (JSONObject json_doc, DocumentWrapper wrapper) throws IllegalArgumentException {
		super (json_doc, wrapper);
	}
	

	protected boolean addFields (JSONObject json_doc) {
		boolean success_flag = false;
		
		if (super.addFields (json_doc)) {
			
			if (addText (json_doc, ServiceDocument.SD_SERVICE)) {
				
				/*
				 * Add the Blast-specific fields
				 */
				if (addNonIndexedString (json_doc, GD_INTERNAL_LINK)) {
					Object obj = json_doc.get (BlastServiceJSON.BSJ_PAYLOAD);
					
					if ((obj != null) && (obj instanceof JSONObject)) {
						JSONObject payload = (JSONObject) obj;
						String payload_str = payload.toJSONString ();
						
						if (addNonIndexedString (BlastServiceJSON.BSJ_PAYLOAD, payload_str)) {
							success_flag = true;
						}
										
					}
				}
			}
		}
	
		return success_flag;
	}

	static public boolean isFieldMultiValued (String field) {
		return GrassrootsDocument.isFieldMultiValued (field);
	}

}
