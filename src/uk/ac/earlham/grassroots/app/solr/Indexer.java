package uk.ac.earlham.grassroots.app.solr;

import org.apache.solr.common.util.NamedList;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.request.SolrRequestHandler;
import org.apache.solr.response.SolrQueryResponse;

public class Indexer implements SolrRequestHandler {

	@Override
	public Category getCategory () {
		return Category.UPDATE;
	}

	@Override
	public String getDescription () {
		return "A handler for submitting Grassroots data to be indexed inside Solr";
	}

	@Override
	public String getName () {
		return "Grassroots indexer";
	}

	@Override
	public void handleRequest (SolrQueryRequest req, SolrQueryResponse res) {
		// TODO Auto-generated method stub		
	}

	@Override
	public void init (NamedList args) {
		// TODO Auto-generated method stub
		
	}

}
