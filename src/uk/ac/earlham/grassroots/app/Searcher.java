/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/* 
 * This is based upon the code at
 * 
 * https://lucene.apache.org/core/7_6_0/demo/src-html/org/apache/lucene/demo/SearchFiles.html
 */
package uk.ac.earlham.grassroots.app;


import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.facet.FacetResult;
import org.apache.lucene.facet.Facets;
import org.apache.lucene.facet.FacetsCollector;
import org.apache.lucene.facet.FacetsConfig;
import org.apache.lucene.facet.taxonomy.FastTaxonomyFacetCounts;
import org.apache.lucene.facet.taxonomy.TaxonomyReader;
import org.apache.lucene.facet.taxonomy.directory.DirectoryTaxonomyReader;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import uk.ac.earlham.grassroots.document.GrassrootsDocument;


/** Simple command-line based search demo. */
public class Searcher {
	private IndexReader se_index_reader;
	private TaxonomyReader se_taxonomy_reader;

	private FacetsConfig se_config;
	
	
	public Searcher (String index_dir_name, String tax_dir_name) throws IOException {
		Directory index_dir = FSDirectory.open (Paths.get (index_dir_name));
		Directory tax_dir = FSDirectory.open (Paths.get (tax_dir_name));
		
		se_index_reader =  DirectoryReader.open (index_dir);
		se_taxonomy_reader = new DirectoryTaxonomyReader (tax_dir);
		se_config = new FacetsConfig ();
	}

	/** Simple command-line based search demo. */
	public static void main(String[] args) throws Exception {
		String usage =
				"Usage:\tjava uk.ac.earlham.grassroots.app.Searcher [-index dir] [-field f] [-repeat n] [-queries file] [-query string] [-raw] [-paging hitsPerPage]\n\nSee http://lucene.apache.org/core/4_1_0/demo/ for details.";
		if (args.length > 0 && ("-h".equals(args[0]) || "-help".equals(args[0]))) {
			System.out.println(usage);
			System.exit(0);
		}

		String index = "index";
		String tax_dirname = null;
		String query_str = null;
		int hits_per_page = 10;

		for (int i = 0; i < args.length; ++ i) {
			if ("-index".equals (args [i])) {
				index = args [++ i];
			} else if ("-tax".equals (args [i])) {
				tax_dirname = args [++ i];
			} else if ("-query".equals (args [i])) {
				query_str = args [++ i];
			} else if ("-paging".equals(args[i])) {
				hits_per_page = Integer.parseInt (args [++ i]);

				if (hits_per_page <= 0) {
					System.err.println("There must be at least 1 hit per page.");
					System.exit(1);
				}
			}
		}

		if (index != null) {
			Searcher searcher = new Searcher (index, tax_dirname);			

			if (query_str != null) {
				searcher.search (query_str);
			}
		}
		
	}

	
	private void buildQuery (StringBuilder sb, String key, String value, float boost) {
		if (sb.length () > 0) {
			sb.append (' ');
		}

		sb.append ("(");
		sb.append (key);
		sb.append (": \"");
		sb.append (value);
		sb.append ("\")^");
		sb.append (boost);		
	}
	
	public boolean search (String query_str) {
		final float NAME_BOOST = 5.0f;
		final float DESCRIPTION_BOOST = 3.0f;
		
		boolean success_flag = false;
		List <FacetResult> results = null;
		StandardAnalyzer analyzer = new StandardAnalyzer ();
		QueryParser parser = new QueryParser (GrassrootsDocument.GD_DEFAULT_SEARCH_KEY, analyzer);
		Query q = null;		
		
		StringBuilder sb = new StringBuilder ();
		buildQuery (sb, GrassrootsDocument.GD_NAME, query_str, NAME_BOOST);
		buildQuery (sb, GrassrootsDocument.GD_DESCRIPTION, query_str, DESCRIPTION_BOOST);
		sb.append (" \"");
		sb.append (query_str);
		sb.append ("\"");
		
		System.out.println ("query: " + query_str);		
		
		try {				
			q = parser.parse (sb.toString ());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (q != null) {
			try {
				doSearch (q, 100);
				results = getFacetsOnly (q);
				success_flag = true;
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
			
			if (results != null) {
				Iterator <FacetResult> itr = results.iterator ();
				int i = 0;
				
				while (itr.hasNext ()) {
					FacetResult res = itr.next ();
					
					System.out.println (Integer.toString (i) + ": " + res.toString ());
					++ i;
				}
			}
		}
		
		return success_flag;
	}

	
	
	  
	/** User runs a query and counts facets only without collecting the matching documents.*/
	private List <FacetResult> getFacetsOnly (Query q) throws IOException {
		IndexSearcher searcher = new IndexSearcher (se_index_reader);
		FacetsCollector fc = new FacetsCollector();

		// MatchAllDocsQuery is for "browsing" (counts facets
		// for all non-deleted docs in the index); normally
		// you'd use a "normal" query:
		
		if (q == null) {
			q = new MatchAllDocsQuery ();
		}

		FacetsCollector.search (searcher, q, 10, fc);


		// Retrieve results
		List <FacetResult> results = new ArrayList <FacetResult> ();

		// Count both "Publish Date" and "Author" dimensions
		Facets facets = new FastTaxonomyFacetCounts (se_taxonomy_reader, se_config, fc);
		results.add (facets.getTopChildren (10, GrassrootsDocument.GD_DATATYPE));

		return results;
	}

	
	/**
	 * This demonstrates a typical paging search scenario, where the search engine presents 
	 * pages of size n to the user. The user can then go to the next page if interested in
	 * the next hits.
	 * 
	 * When the query is executed for the first time, then only enough results are collected
	 * to fill 5 result pages. If the user wants to page beyond this limit, then the query
	 * is executed another time and all hits are collected.
	 * 
	 */
	public void doSearch (Query query, int max_num_hits) throws IOException {
		IndexSearcher searcher = new IndexSearcher (se_index_reader);
		TopDocs results = searcher.search (query, max_num_hits);
		ScoreDoc [] hits = results.scoreDocs;

		int num_total_hits = Math.toIntExact (results.totalHits);

		for (int i = 0; i < num_total_hits; ++ i) {
			Document doc = searcher.doc (hits [i].doc);
			System.out.println ("doc [" + i + "]: " + doc);
		}
	}
}