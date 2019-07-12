package uk.ac.earlham.grassroots.app.solr;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.function.BiConsumer;

import org.apache.solr.common.params.SolrParams;
import org.apache.solr.common.util.NamedList;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.request.SolrRequestHandler;
import org.apache.solr.response.SolrQueryResponse;

public class Indexer implements SolrRequestHandler {
	 private static final Logger in_log = LoggerFactory.getLogger (MethodHandles.lookup ().lookupClass ());
	
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
		SolrParams params = req.getParams ();
		String value = params.get ("data");
		
		if (value != null) {
			in_log.info ("data = " + value);
		}
		
	}

	@Override
	public void init (NamedList args) {
		// TODO Auto-generated method stub
		
	}

}
