package uk.ac.earlham.grassroots.document.lucene;


import java.util.Map;

import org.apache.lucene.document.Document;
import org.json.simple.JSONObject;

import uk.ac.earlham.grassroots.document.json.AddressJSON;
import uk.ac.earlham.grassroots.document.json.BlastServiceJSON;
import uk.ac.earlham.grassroots.document.json.FieldTrialJSON;
import uk.ac.earlham.grassroots.document.json.GrassrootsJSON;
import uk.ac.earlham.grassroots.document.json.ProgrammeJSON;
import uk.ac.earlham.grassroots.document.json.ProjectJSON;
import uk.ac.earlham.grassroots.document.json.SamtoolsServiceJSON;
import uk.ac.earlham.grassroots.document.json.StudyJSON;
import uk.ac.earlham.grassroots.document.json.TreatmentJSON;
import uk.ac.earlham.grassroots.document.json.MeasuredVariableJSON;
import uk.ac.earlham.grassroots.document.lucene.util.DocumentWrapper;


/**
 * A factory class for inspecting the JSON data sent by Grassroots and 
 * creates the appropriate GrassrootsDocument.
 * 
 * @author billy
 *
 */
public class GrassrootsDocumentFactory {
	static public String GDF_TYPE = "@type";
	static public String GDF_SUBTYPE_SERVICE = "service";
	
	/**
	 * Create the GrassrootsDocument for a given JSON object.
	 * 
	 * @param json_doc The JSON object created from the Grassroots JSON.
	 * @return The appropriate GrassrootsDocument or <code>null<code> upon
	 * error
	 */
	static public GrassrootsDocument createDocument (JSONObject json_doc, DocumentWrapper wrapper) {
		GrassrootsDocument doc = null;
		Object obj_type = json_doc.get (GDF_TYPE);
		
		if (obj_type != null) {
			String datatype = obj_type.toString ();
			
			switch (datatype) {
				case "Grassroots:FieldTrial":
					doc = new FieldTrialDocument (json_doc, wrapper);
					break;
					
				case "Grassroots:Study": 
					doc = new StudyDocument (json_doc, wrapper);					
					break;

				case "Grassroots:MeasuredVariable": 
					doc = new MeasuredVariableDocument (json_doc, wrapper);					
					break;

				case "Grassroots:Treatment": 
					doc = new TreatmentDocument (json_doc, wrapper);					
					break;
					
				case "Grassroots:Location": 
					doc = new AddressDocument (json_doc, wrapper);					
					break;

				case "Grassroots:Programme": 
					doc = new ProgrammeDocument (json_doc, wrapper);					
					break;

				case "Grassroots:Service": {
					Object o = json_doc.get (GDF_SUBTYPE_SERVICE);
					
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
					
				case "Grassroots:Project":
					doc = new ProjectDocument (json_doc, wrapper);
					break;
				
				case "Grassroots:MARTiSample":
					doc = new MartiDocument (json_doc, wrapper);
					break;

					
				default:
					System.err.println ("Unknown " + GDF_TYPE + ": \"" + datatype + "\"");
					break;
			}	
		}
		
		return doc;
	}

	
	static public GrassrootsJSON getJSON (Document doc, Map <String, String []> highlights, int highlight_index) {
		GrassrootsJSON grassroots_json = null;
		String datatype = doc.get (GDF_TYPE);
		
		if (datatype != null) {
			
			switch (datatype) {
				case "Grassroots:FieldTrial":
					grassroots_json = new FieldTrialJSON (doc, highlights, highlight_index);
					break;
					
				case "Grassroots:Study": 
					grassroots_json = new StudyJSON (doc, highlights, highlight_index);					
					break;

				case "Grassroots:MeasuredVariable": 
					grassroots_json = new MeasuredVariableJSON (doc, highlights, highlight_index);						
					break;
				
				case "Grassroots:Treatment": 
					grassroots_json = new TreatmentJSON (doc, highlights, highlight_index);						
					break;
					
				case "Grassroots:Location": 
					grassroots_json = new AddressJSON (doc, highlights, highlight_index);					
					break;

				case "Grassroots:Programme": 
					grassroots_json = new ProgrammeJSON (doc, highlights, highlight_index);					
					break;

				case "Grassroots:Service": {
					String name = doc.get (GDF_SUBTYPE_SERVICE);
					
					if (name != null) {
						
						switch (name) {
							case "BlastN":
							case "BlastP":
							case "BlastX":
								grassroots_json = new BlastServiceJSON (doc, highlights, highlight_index);		
								break;

							case "Scaffold":
								grassroots_json = new SamtoolsServiceJSON (doc, highlights, highlight_index);	
								break;
								
							default:
								System.out.println ("Unknown Grassroots:Service: \"" + name + "\"");
								break;
						}
					}					
				}
				break;
					
				case "Grassroots:Project":
					grassroots_json = new ProjectJSON (doc, highlights, highlight_index);	
					break;
				
				default:
					System.err.println ("Unknown " + GDF_TYPE + ": \"" + datatype + "\"");
					break;
			}	
		}
		
		return grassroots_json;
	}

	
}
