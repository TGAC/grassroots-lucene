package uk.ac.earlham.grassroots.document.lucene.util;

import org.apache.lucene.facet.FacetField;


public interface DocumentWrapper {

	boolean process ();
	
	void addText (String key, String value);
	
	boolean addString (String key, String value);

	boolean addFacet (String key, String value);

	boolean addFacet (FacetField facet);
	
	void addNonIndexedString (String key, String value);

	void addDateString (String key, String value);
	
	void clear ();
}
