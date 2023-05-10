package uk.ac.earlham.grassroots.app.solr;

import java.lang.invoke.MethodHandles;

import org.apache.solr.common.params.SolrParams;
import org.apache.solr.common.util.NamedList;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.request.SolrRequestHandler;
import org.apache.solr.response.SolrQueryResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.solr.metrics.SolrMetricsContext;


public class Searcher implements SolrRequestHandler {
	 private static final Logger se_log = LoggerFactory.getLogger (MethodHandles.lookup ().lookupClass ());

	@Override
	public Category getCategory() {
		// TODO Auto-generated method stub
		return Category.SEARCHER;
	}

	@Override
	public String getDescription() {
		return "A handler for searching Grassroots data";
	}

	@Override
	public String getName() {
		return "Grassroots searcher";
	}

	@Override
	public void handleRequest (SolrQueryRequest req, SolrQueryResponse res) {
		SolrParams params = req.getParams ();
		String value = params.get ("query");
		
		if (value != null) {
			se_log.info ("query = " + value);
		}
	}

	@Override
	public void init (NamedList config) {
		// TODO Auto-generated method stub
	}


	@Override
	public SolrMetricsContext getSolrMetricsContext () {
			return null;
	}
	
	@Override
	public void initializeMetrics​ (SolrMetricsContext parentContext, String scope) {
	}
}
