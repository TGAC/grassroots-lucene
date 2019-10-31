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
package uk.ac.earlham.grassroots.app.lucene;


import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.facet.DrillDownQuery;
import org.apache.lucene.facet.DrillSideways;
import org.apache.lucene.facet.DrillSideways.DrillSidewaysResult;
import org.apache.lucene.facet.FacetResult;
import org.apache.lucene.facet.Facets;
import org.apache.lucene.facet.FacetsCollector;
import org.apache.lucene.facet.FacetsConfig;
import org.apache.lucene.facet.taxonomy.FastTaxonomyFacetCounts;
import org.apache.lucene.facet.taxonomy.TaxonomyReader;
import org.apache.lucene.facet.taxonomy.directory.DirectoryTaxonomyReader;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import uk.ac.earlham.grassroots.document.GrassrootsDocument;


class DrillDownData {
	int ddd_total_num_hits;
	int ddd_from_index;
	int ddd_to_index;
	List <Document> ddd_hits;
	FacetResult ddd_active_facet;
	List <FacetResult> ddd_facets;	
}

class DrillSidewaysData {
	List <Document> dsd_hits;
	List <FacetResult> dsd_facets;
}



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
	public static void main(String[] args)  {
		String usage =
				"Usage:\tjava uk.ac.earlham.grassroots.app.Searcher [-index dir] [-field f] [-repeat n] [-queries file] [-query string] [-raw] [-paging hitsPerPage]\n\nSee http://lucene.apache.org/core/4_1_0/demo/ for details.";
		if (args.length > 0 && ("-h".equals(args[0]) || "-help".equals(args[0]))) {
			System.out.println(usage);
			System.exit(0);
		}

		String index = null;
		String tax_dirname = null;
		List <String> queries = new ArrayList <String> ();
		int hits_per_page = 10;
		int page = 0;
		String facet_name = null;
		String facet_value = null;
		String search_type = "default";
		PrintStream output_stm = System.out;
		
		
		for (int i = 0; i < args.length; ++ i) {
			if ("-index".equals (args [i])) {
				index = args [++ i];
			} else if ("-tax".equals (args [i])) {
				tax_dirname = args [++ i];
			} else if ("-query".equals (args [i])) {
				
				while (++ i < args.length) {
					queries.add (args [i]);
				}

			} else if ("-facet_name".equals (args [i])) {
				facet_name = args [++ i];
			} else if ("-facet_value".equals (args [i])) {
				facet_value = args [++ i];
			} else if ("-out".equals (args [i])) {
				String filename = args [++ i];
				
				try {
					output_stm = new PrintStream (new FileOutputStream (filename));
				} catch (FileNotFoundException e) {
					System.err.println ("Failed to open " + filename + " e: " + e);
				}
				
			} else if ("-search_type".equals (args [i])) {
				search_type = args [++ i];
			} else if ("-page_size".equals(args[i])) {
				hits_per_page = Integer.parseInt (args [++ i]);

				if (hits_per_page <= 0) {
					System.err.println("There must be at least 1 hit per page.");
					System.exit(1);
				}
			} else if ("-page".equals (args [i])) {
				page = Integer.parseInt (args [++ i]);

				if (page <= 0) {
					System.err.println("Invalid page.");
					System.exit(1);
				}
			}
		}

		if (index != null) {
			Searcher searcher = null;
			
			try {
				searcher = new Searcher (index, tax_dirname);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}			

			if (searcher != null) {
				Query q = null;

				if (!queries.isEmpty ()) {					
					q = searcher.buildGrassrootsQuery (queries);
				} else {
					q = new MatchAllDocsQuery ();					
				}
				if (q != null) {
					JSONObject json_res = new JSONObject ();

					switch (search_type) {
						case "default": {
							List <Document> docs = null;

							try {
								docs = searcher.standardSearch (q, hits_per_page);
							} catch (IOException e) {
								System.err.println ("standardSearch failed: " + q.toString () + " e: " + e);
							}
							
							if (docs != null) {
								searcher.addHitsToJSON (docs, json_res);
							}
						}
						break;
						
						case "all-facets": {
							List <FacetResult> facets = searcher.getAllFacets (q, hits_per_page);
							
							if (facets != null) {
								searcher.addFacetResults (facets, json_res);
							}							
						}
						break;
						
						case "facets-only": {
							List <FacetResult> facets = searcher.facetsOnlySearch (q, facet_name, hits_per_page);
							
							if (facets != null) {
								searcher.addFacetResults (facets, json_res);
							}
						}
						break;

						case "drill-down": {
							DrillDownData results = null;
							
							try {
								results = searcher.drillDown (q, facet_name, facet_value, facet_name, hits_per_page, page);
							} catch (IOException e) {
								System.err.println ("standardSearch failed: " + q.toString () + " e: " + e);
							}
							
						
							if (results != null) {
								searcher.addHitsToJSON (results.ddd_hits, json_res);
								
								if (results.ddd_active_facet != null) {
									searcher.addFacetResult (results.ddd_active_facet, json_res);
								}
								
								if (results.ddd_facets != null) {
									searcher.addFacetResults (results.ddd_facets, json_res);
								}
								
								searcher.addSearchStats (results.ddd_from_index, results.ddd_to_index, results.ddd_total_num_hits, json_res);
							}
						}
						break;

						case "drill-sideways": {
							DrillSidewaysData results = null;

							try {
								results = searcher.drillSideways (q, facet_name, facet_value, hits_per_page);
							} catch (IOException e) {
								System.err.println ("standardSearch failed: " + q.toString () + " e: " + e);
							}
							
							if (results != null) {
								searcher.addHitsToJSON (results.dsd_hits, json_res);
								searcher.addFacetResults (results.dsd_facets, json_res);
							}
							
						}
						
						break;
					}
					
					
					output_stm.println (json_res.toJSONString ());
				}
									
			}

		}
		
		
		if (output_stm != System.out) {
			output_stm.close ();
		}
	}

	
	private void addHitsToJSON (List<Document> docs, JSONObject results) {
		if (docs != null) {
			JSONArray docs_array = new JSONArray ();

			for (Document doc : docs) {	
				JSONObject doc_json = getLuceneDocumentAsJSON (doc);
				docs_array.add (doc_json);			
			}

			results.put ("documents", docs_array);
		}
	}

	private void buildQuery (StringBuilder sb, String key, String value, float boost) {
		if (sb.length () > 0) {
			sb.append (' ');
		}

		String escaped_key = key.replace (":", "\\:");
		final boolean wild_flag = value.contains ("*");
	
		sb.append ("(");
		sb.append (escaped_key);
		sb.append (':');
		
		if (!wild_flag) {
			sb.append ('"');			
		}
		
		sb.append (value);

		if (!wild_flag) {
			sb.append ('"');			
		}

		sb.append (")^");
		sb.append (boost);		
	}
	
	
	public Query buildGrassrootsQuery (List <String> queries) {
		final float NAME_BOOST = 5.0f;
		final float DESCRIPTION_BOOST = 3.0f;
		Query q = null;		
		StandardAnalyzer analyzer = new StandardAnalyzer ();
		QueryParser parser = new QueryParser (GrassrootsDocument.GD_DEFAULT_SEARCH_KEY, analyzer);
		
		StringBuilder sb = new StringBuilder ();
			
		AddStringsToQuery (sb, queries, false);
		
		System.out.println ("query: " + sb.toString ());		
		
		try {				
			q = parser.parse (sb.toString ());
		} catch (ParseException e) {
			System.err.println ("Failed to parse query \"" + q + "\", exception: "+ e);
		}
		
		return q;
	}

	
	private void AddStringsToQuery (StringBuilder sb, List <String> terms, boolean quote_flag) {
		final float NAME_BOOST = 5.0f;
		final float DESCRIPTION_BOOST = 3.0f;

		for (String s : terms) {
			if (sb.length () != 0) {
				sb.append (' ');
			}

			if (s.contains (":")) {
				sb.append (" AND ");
				sb.append (s);
			} else {	
				sb.append ("(");				
				buildQuery (sb, GrassrootsDocument.GD_NAME, s, NAME_BOOST);
				buildQuery (sb, GrassrootsDocument.GD_DESCRIPTION, s, DESCRIPTION_BOOST);
				sb.append (" \"");
				sb.append (s);
				sb.append ("\")");				
			}
			
		}
	
	}
	
	
	/** User runs a query and counts facets only without collecting the matching documents.*/
	public List <FacetResult> getAllFacets (Query q, int max_num_facets) {
		IndexSearcher searcher = new IndexSearcher (se_index_reader);
		FacetsCollector fc = new FacetsCollector();

		// MatchAllDocsQuery is for "browsing" (counts facets
		// for all non-deleted docs in the index); normally
		// you'd use a "normal" query:
		
		if (q == null) {
			q = new MatchAllDocsQuery ();
		}

		// Retrieve results
		List <FacetResult> results = new ArrayList <FacetResult> ();

		try {
			FacetsCollector.search (searcher, q, max_num_facets, fc);
			// Count both "Publish Date" and "Author" dimensions
			Facets facets = new FastTaxonomyFacetCounts (se_taxonomy_reader, se_config, fc);
			
			results = facets.getAllDims (max_num_facets);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

		return results;
	}

		  
	/** User runs a query and counts facets only without collecting the matching documents.*/
	public List <FacetResult> facetsOnlySearch (Query q, String facet_name, int max_num_facets) {
		IndexSearcher searcher = new IndexSearcher (se_index_reader);
		FacetsCollector fc = new FacetsCollector();

		// MatchAllDocsQuery is for "browsing" (counts facets
		// for all non-deleted docs in the index); normally
		// you'd use a "normal" query:
		
		if (q == null) {
			q = new MatchAllDocsQuery ();
		}

		// Retrieve results
		List <FacetResult> results = new ArrayList <FacetResult> ();

		try {
			FacetsCollector.search (searcher, q, max_num_facets, fc);
			// Count both "Publish Date" and "Author" dimensions
			Facets facets = new FastTaxonomyFacetCounts (se_taxonomy_reader, se_config, fc);
			results.add (facets.getTopChildren (max_num_facets, facet_name));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

		return results;
	}


	  /** 
	   * Drill down on a given facet and return all hits and facets
	   */
	  public DrillSidewaysData drillSideways (Query base_query, String facet_name, String facet_value, int max_num_facets) throws IOException {
	    IndexSearcher searcher = new IndexSearcher (se_index_reader);

	    // Passing no base_query means we drill down on all
	    // documents ("browse only"):
		DrillDownQuery q = null;
	    if (base_query != null) {
	    	q = new DrillDownQuery (se_config, base_query);
	    } else {
	    	q = new DrillDownQuery (se_config);    	
	    }
	    
	    // Now user drills down on the given facet
	    q.add (facet_name, facet_value);
		
	    DrillSideways ds = new DrillSideways(searcher, se_config, se_taxonomy_reader);
	    DrillSidewaysResult result = ds.search (q, max_num_facets);

	    // Retrieve results
	    List <FacetResult> facets = result.facets.getAllDims (max_num_facets);

	    
	    // Retrieve results
		ScoreDoc [] hits = result.hits.scoreDocs;
		int num_total_hits = Searcher.CastLongToInt (result.hits.totalHits.value);
		int limit = Math.min (num_total_hits, hits.length);
		
		List <Document> docs = new ArrayList <Document> ();
		for (int i = 0; i < limit; ++ i) {
			Document doc = searcher.doc (hits [i].doc);
			docs.add (doc);
		}
	    
		DrillSidewaysData search_results = new DrillSidewaysData ();
		search_results.dsd_hits = docs;
		search_results.dsd_facets = facets;
		
		return search_results;
	  }
	
	  
	  
	  /** User drills down on a facet, and we
	   *  return another facets for  */
	  public DrillDownData drillDown (Query base_query, String facet_name, String facet_value, String facet_to_return, int hits_per_page, int page_number) throws IOException {
	    IndexSearcher searcher = new IndexSearcher (se_index_reader);
		FacetsCollector fc = new FacetsCollector ();
		final int MAX_NUM_RESULTS = 1024;
		List <FacetResult> all_facets = null;
		
	    // Passing no baseQuery means we drill down on all
	    // documents ("browse only"):
		DrillDownQuery q = null;
	    if (base_query != null) {
	    	q = new DrillDownQuery (se_config, base_query);
	    } else {
	    	q = new DrillDownQuery (se_config);    	
	    }
	    
	    /*
	     * Are we drilling down on a specific facet?
	     */
	    if ((facet_name != null) && (facet_value != null)) {
	    	q.add (facet_name, facet_value);
	    }

	    
	    TopDocs resultDocs = FacetsCollector.search (searcher, q, MAX_NUM_RESULTS, fc);

	    List <FacetsCollector.MatchingDocs> matching_docs = fc.getMatchingDocs ();
	    
	    // Retrieve facets
	    Facets facets = new FastTaxonomyFacetCounts (se_taxonomy_reader, se_config, fc);

	    if (facets != null) {
	    	all_facets = facets.getAllDims (MAX_NUM_RESULTS);
	    }
	    
	    FacetResult result = null;

	    if (facet_to_return != null) {
	    	result = facets.getTopChildren (hits_per_page, facet_to_return);
	    }
	    

	    for (FacetsCollector.MatchingDocs matching_doc : matching_docs) {
	    
	    }
	    

	    
	    
	    // Retrieve results
		ScoreDoc [] hits = resultDocs.scoreDocs;
		int total_hits = Searcher.CastLongToInt (resultDocs.totalHits.value);
		
		
		List <Document> docs = new ArrayList <Document> ();
		int start = hits_per_page * page_number;
		int end = 0;

		if (start < total_hits) {
			end = start + hits_per_page - 1;

			if (end >= total_hits) {
				end = total_hits - 1;
			}
			
			for (int i = start; i <= end; ++ i) {
				Document doc = searcher.doc (hits [i].doc);
				docs.add (doc);
			}
		} else {
			start = 0;
		}

	    
		DrillDownData search_results = new DrillDownData ();
		search_results.ddd_total_num_hits = total_hits;
		search_results.ddd_from_index = start;
		search_results.ddd_to_index = end;
		search_results.ddd_hits = docs;
		search_results.ddd_active_facet = result;
		search_results.ddd_facets = all_facets;
	    
	    return search_results;
	  }
	


	private void addFacetResults (List <FacetResult> facets, JSONObject res) {		
		if (facets != null) {
			JSONArray facets_array = new JSONArray ();

			for (FacetResult facet : facets) {	
				JSONObject facet_json = getFacetResultAsJSON (facet);
				facets_array.add (facet_json);			
			}

			res.put ("facets", facets_array);
		}
	}

	
	private void addFacetResult (FacetResult facet, JSONObject res) {
		JSONArray facets_array = new JSONArray ();
		JSONObject facet_json = getFacetResultAsJSON (facet);
		facets_array.add (facet_json);			

		res.put ("facets", facets_array);
	}
	  

	private void addSearchStats (int from, int to, int total_hits, JSONObject res) {
		res.put ("from", from);
		res.put ("to", to);
		res.put ("total_hits", total_hits);
	}
	
	private JSONObject getFacetResultAsJSON (FacetResult facet) {
		JSONObject facet_json = new JSONObject ();
		
		facet_json.put ("childCount", facet.childCount);
		facet_json.put ("dim", facet.dim);

		if ((facet.path != null) && (facet.path.length > 0)) {
			JSONArray path_json = new JSONArray (); 
			
			for (int i = 0; i < facet.path.length; ++ i) {
				path_json.add (facet.path [i]);
			}
			
			facet_json.put ("path", path_json);	
		}


		if ((facet.labelValues != null) && (facet.labelValues.length > 0)) {
			JSONArray label_values = new JSONArray (); 
			
			for (int i = 0; i < facet.labelValues.length; ++ i) {
				JSONObject label_value = new JSONObject ();
				
				label_value.put ("label", facet.labelValues [i].label);
				label_value.put ("value", facet.labelValues [i].value);

				label_values.add (label_value);
			}
			
			facet_json.put ("labelValues", label_values);	
		}

		
		facet_json.put ("value", facet.value);
				
		return facet_json;
	}
	
	
	public static int CastLongToInt (long l) {
	    if ((l >= Integer.MIN_VALUE) && (l <= Integer.MAX_VALUE)) {
	    	return ((int) l);
	    } else {
	        throw new IllegalArgumentException (l + " is outside the integer range.");
	    }
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
	public List <Document> standardSearch (Query query, int max_num_hits) throws IOException {
		List <Document> docs = new ArrayList <Document> ();
		IndexSearcher searcher = new IndexSearcher (se_index_reader);
		TopDocs results = searcher.search (query, max_num_hits);
		ScoreDoc [] hits = results.scoreDocs;

		int num_total_hits = Searcher.CastLongToInt (results.totalHits.value);
		int limit = Math.min (num_total_hits, hits.length);
		
		for (int i = 0; i < limit; ++ i) {
			Document doc = searcher.doc (hits [i].doc);
			docs.add (doc);
		}
		
		return docs;
	}
	
	
	public JSONObject getLuceneDocumentAsJSON (Document doc) {
		JSONObject res = new JSONObject ();
		List <IndexableField> fields = doc.getFields ();
		HashMap <String, List <String> > map = new HashMap <String, List <String>> ();
		
		final int num_fields = fields.size ();
				
		for (int i = 0; i < num_fields; ++ i) {
			IndexableField field = fields.get (i);
			String key = field.name ();
			String value = field.stringValue ();
			
			List <String> values = map.get (key);
			
			if (values == null) {
				values = new ArrayList <String> ();
				map.put (key, values);
			}

			values.add (value);
		}
		
	    Iterator <Entry <String, List <String> > > itr = map.entrySet ().iterator ();
	    while (itr.hasNext ()) {
	        Map.Entry <String, List <String> > pair = itr.next ();
	        String key = pair.getKey ();
	        List <String> values = pair.getValue ();
	        
	        if (values.size () > 1) {
	        	JSONArray arr = new JSONArray ();
	        	
	        	arr.addAll (values);

	        	res.put (key, arr);
	        } else {
	        	res.put (key, values.get (0));
	        }
	       	     	        
	    }
		
		return res;
	}

	
	public String getLuceneDocumentAsProperties (Document doc) {
		StringBuilder sb = new StringBuilder (); 
		List <IndexableField> fields = doc.getFields ();
		final int num_fields = fields.size ();
		
		for (int i = 0; i < num_fields; ++ i) {
			IndexableField field = fields.get (i);
			sb.append ("\t");
			sb.append (field.name ());
			sb.append (" = ");
			sb.append (field.stringValue ());	
			sb.append ("\n");
		}
		
		return sb.toString ();
	}

}