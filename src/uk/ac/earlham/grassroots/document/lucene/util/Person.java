package uk.ac.earlham.grassroots.document.lucene.util;


import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import uk.ac.earlham.grassroots.document.lucene.FieldTrialDocument;
import uk.ac.earlham.grassroots.document.lucene.GrassrootsDocument;

public class Person  {

	final static public String PE_NAME = "_name";
	final static public String PE_EMAIL = "_email";
	final static public String PE_ROLE = "_role";
	final static public String PE_AFFILIATION = "_affiliation";
	final static public String PE_ORCID = "_orcid";
	final static public String PE_ALL_PEOPLE = "_all_people";



	static public void addQueryTerms (final String prefix, List <String> fields, Map <String, Float> boosts, Map <String, String> string_fields) {
		fields.add (prefix + Person.PE_NAME);
		fields.add (prefix + Person.PE_EMAIL);
		fields.add (prefix + Person.PE_ROLE);
		fields.add (prefix + Person.PE_AFFILIATION);
		fields.add (prefix + Person.PE_ORCID);

		if (boosts != null) {
			boosts.put (Person.PE_NAME, GrassrootsDocument.GD_NAME_BOOST);
			boosts.put (Person.PE_EMAIL, GrassrootsDocument.GD_NAME_BOOST);
		}

		if (string_fields != null) {
			string_fields.put (Person.PE_EMAIL, Person.PE_EMAIL);
		}

	}

	static public void addPeople (GrassrootsDocument grassroots_doc, JSONObject json_doc, String input_json_key, String output_person_prefix) {
		Object o = json_doc.get (input_json_key);

		if (o != null) {
			if (o instanceof JSONArray) {
				JSONArray people = (JSONArray) o;
				StringBuilder sb = new StringBuilder ();
				
				for (Object person : people) {
					if (person instanceof JSONObject) {
						addPerson (grassroots_doc, (JSONObject) person, output_person_prefix, sb);
					}
				}
				
				if (sb.length () > 0) {
					grassroots_doc.addText (output_person_prefix + Person.PE_ALL_PEOPLE, sb.toString ());
				}
			}
		}

	}


	static public void addPerson (GrassrootsDocument grassroots_doc, JSONObject json_doc, String input_json_key, String output_person_prefix, StringBuilder sb) {
		Object o = json_doc.get (input_json_key);

		if (o != null) {
			if (o instanceof JSONObject) {
				addPerson (grassroots_doc, (JSONObject) o, output_person_prefix, sb);
			}
		}

	}


	static public void addPerson (GrassrootsDocument grassroots_doc, JSONObject person_json, String output_person_prefix, StringBuilder sb) {
		boolean added_summary_flag = false;
		
		/* name */
		Object o = person_json.get ("so:name");
		if (o != null) {
			String name = o.toString ();
			grassroots_doc.addText (output_person_prefix + Person.PE_NAME, name);

			if (sb != null) {
				sb.append (name);
				sb.append (' ');				
				added_summary_flag = true;
			}
		}

		/* email */
		o = person_json.get ("so:email");
		if (o != null) {
			String email = o.toString ();
			grassroots_doc.addText (output_person_prefix + Person.PE_EMAIL, email);

			if (sb != null) {
				sb.append (email);
				sb.append (' ');				
				added_summary_flag = true;
			}
		}

		/* role */
		o = person_json.get ("so:roleName");
		if (o != null) {
			String role = o.toString ();
			grassroots_doc.addText (output_person_prefix + Person.PE_ROLE, role);
		}

		/* affiliation */
		o = person_json.get ("so:affiliation");
		if (o != null) {
			String affiliation = o.toString ();
			grassroots_doc.addText (output_person_prefix + Person.PE_AFFILIATION, affiliation);
		}

		/* orcid */
		o = person_json.get ("orcid");
		if (o != null) {
			String orcid = o.toString ();
			grassroots_doc.addString (output_person_prefix + Person.PE_ORCID, orcid);
		}
		
		if (added_summary_flag) {
			sb.append (", ");
		}
		
	}
	

}	

