package uk.ac.earlham.grassroots.document.util;

import java.io.IOException;

import org.apache.lucene.document.Document;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrRequest;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.util.NamedList;

public class SolrDocumentWrapper implements DocumentWrapper {
	protected SolrClient sdw_client;
	protected SolrInputDocument sdw_doc;
	
	public SolrDocumentWrapper (String url) {
		sdw_client = new HttpSolrClient.Builder (url).build ();
		sdw_doc = new SolrInputDocument ();
	}
	

	public void clear () {
		sdw_doc.clear ();
	}
	
	@Override
	public boolean process() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void addText(String key, String value) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean addString(String key, String value) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean addFacet(String key, String value) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void addNonIndexedString(String key, String value) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addDateString(String key, String value) {

	}

}
