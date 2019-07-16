package uk.ac.earlham.grassroots.document.util;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.facet.FacetField;


import uk.ac.earlham.grassroots.document.GrassrootsDocument;

public class LuceneDocumentWrapper implements DocumentWrapper {

	protected Document ldw_document;
	protected StringBuilder ldw_default_field_buffer;
	
	public LuceneDocumentWrapper () {
		ldw_document = new Document ();
		ldw_default_field_buffer = new StringBuilder ();
	}

	
	public Document getDocument () {
		return ldw_document;
	}

	
	@Override
	public boolean process() {
		if (ldw_default_field_buffer.length () > 0) {
			String s = ldw_default_field_buffer.toString ();
			TextField default_field = new TextField (GrassrootsDocument.GD_DEFAULT_SEARCH_KEY, s, Field.Store.YES);
			ldw_document.add (default_field);			
		}
		
		return true;
	}
	
	@Override
	public void addText (String key, String value) {
		// TODO Auto-generated method stub
		Field f = new TextField (key, value, Field.Store.YES);
		ldw_document.add (f);
		
		addToDefaultBuffer (value);
	}

	@Override
	public boolean addString (String key, String value) {
		Field f = new StringField (key, value, Field.Store.YES);
		ldw_document.add (f);
		
		addToDefaultBuffer (value);
		return true;
	}	

	
	@Override
	public void addNonIndexedString (String key, String value) {
		// TODO Auto-generated method stub
		Field f = new StoredField (key, value);
		ldw_document.add (f);
	}

	@Override
	public void addDateString (String key, String value) {
		// TODO Auto-generated method stub

	}

	
	@Override
	public boolean addFacet (String key, String value) {
		ldw_document.add (new FacetField (key, value));
		return true;
	}

	
	protected void addToDefaultBuffer (String value) {
		if (ldw_default_field_buffer.length () > 0) {
			ldw_default_field_buffer.append (" ");				
		}
		
		ldw_default_field_buffer.append (value);
	}


}	

