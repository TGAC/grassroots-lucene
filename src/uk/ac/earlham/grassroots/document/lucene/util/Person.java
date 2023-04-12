package uk.ac.earlham.grassroots.document.lucene.util;


import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;


import uk.ac.earlham.grassroots.document.lucene.GrassrootsDocument;

public class Person  {

	final static public String PE_NAME = "_name";
	final static public String PE_EMAIL = "_email";
	final static public String PE_ROLE = "_role";
	final static public String PE_AFFILIATION = "_affiliation";
	final static public String PE_ORCID = "_orcid";



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


	static public void addPerson (GrassrootsDocument grassroots_doc, JSONObject json_doc, String input_json_key, String output_person_prefix) {
		Object o = json_doc.get (input_json_key);

		if (o != null) {
			if (o instanceof JSONObject) {
				JSONObject curator = (JSONObject) o;

				/* name */
				o = curator.get ("so:name");
				if (o != null) {
					String name = o.toString ();
					grassroots_doc.addText (output_person_prefix + Person.PE_NAME, name);
				}

				/* email */
				o = curator.get ("so:email");
				if (o != null) {
					String name = o.toString ();
					grassroots_doc.addText (output_person_prefix + Person.PE_EMAIL, name);
				}

				/* role */
				o = curator.get ("so:roleName");
				if (o != null) {
					String name = o.toString ();
					grassroots_doc.addText (output_person_prefix + Person.PE_ROLE, name);
				}

				/* affiliation */
				o = curator.get ("so:affiliation");
				if (o != null) {
					String name = o.toString ();
					grassroots_doc.addText (output_person_prefix + Person.PE_AFFILIATION, name);
				}

				/* orcid */
				o = curator.get ("orcid");
				if (o != null) {
					String name = o.toString ();
					grassroots_doc.addString (output_person_prefix + Person.PE_ORCID, name);
				}
			}
		}

	}



}	

