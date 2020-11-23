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
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.io.StringReader;
import java.nio.file.Paths;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.KeywordAnalyzer;
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
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.Formatter;
import org.apache.lucene.search.highlight.Fragmenter;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.search.highlight.SimpleSpanFragmenter;
import org.apache.lucene.search.highlight.TokenSources;
import org.apache.lucene.store.Directory;

import org.apache.lucene.search.uhighlight.UnifiedHighlighter;


import org.apache.lucene.store.FSDirectory;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import uk.ac.earlham.grassroots.document.AddressDocument;
import uk.ac.earlham.grassroots.document.FieldTrialDocument;
import uk.ac.earlham.grassroots.document.GrassrootsDocument;
import uk.ac.earlham.grassroots.document.ProjectDocument;
import uk.ac.earlham.grassroots.document.StudyDocument;
import uk.ac.earlham.grassroots.document.TreatmentDocument;


class DrillDownData {
	int ddd_total_num_hits;
	int ddd_from_index;
	int ddd_to_index;
	List <Document> ddd_hits;
	List <FacetResult> ddd_facets;	
	Map <String, String []> ddd_highlits;
}

class DrillSidewaysData {
	List <Document> dsd_hits;
	List <FacetResult> dsd_facets;
	Map <String, String []> dsd_highlights;
}



/** Simple command-line based search demo. */
public class Searcher {
	private IndexReader se_index_reader;
	private TaxonomyReader se_taxonomy_reader;

	private FacetsConfig se_config;

	
	private Searcher (IndexReader index_reader, TaxonomyReader taxonomy_reader) {
		se_index_reader = index_reader;
		se_taxonomy_reader = taxonomy_reader;
		se_config = new FacetsConfig ();
	}
	
	public static Searcher getSearcher (String index_dir_name, String tax_dir_name, String config_filename) {
		Searcher searcher = null;
		IndexReader index_reader = getIndexReader (index_dir_name);
		
		if (index_reader != null) {
			TaxonomyReader taxonomy_reader = getTaxReader (tax_dir_name);		
			
			if (taxonomy_reader != null) {
				searcher = new Searcher (index_reader, taxonomy_reader);							
			}
			
		}
				
		return searcher;
	}

	
	private static IndexReader getIndexReader (String index_dir_name) {
		IndexReader reader = null;
		Directory index_dir = null;

		try {
			index_dir = FSDirectory.open (Paths.get (index_dir_name));
		} catch (IOException e) {
			System.err.println ("Failed to open index_dir " + index_dir_name + ": " + e.getMessage ());	
		}

		
		if (index_dir != null) {
			try {
				reader =  DirectoryReader.open (index_dir);
			} catch (IOException e) {
				System.err.println ("Failed to get index reader " + index_dir_name + ": " + e.getMessage ());	
			}
		}
		
		return reader;
	}


	private static DirectoryTaxonomyReader getTaxReader (String tax_dir_name) {
		DirectoryTaxonomyReader reader = null;
		Directory tax_dir = null;

		try {
			tax_dir = FSDirectory.open (Paths.get (tax_dir_name));
		} catch (IOException e) {
			System.err.println ("Failed to open tax_dir " + tax_dir_name + ": " + e.getMessage ());	
		}

		if (tax_dir != null) {
			try {
				reader = new DirectoryTaxonomyReader (tax_dir);
			} catch (IOException e) {
				System.err.println ("Failed to get tax reader " + tax_dir_name + ": " + e.getMessage ());	
			}

		}
		
		return reader;
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
		List <AbstractMap.SimpleEntry <String, String>> facets = new ArrayList <AbstractMap.SimpleEntry <String, String>> ();
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

			} else if ("-facet".equals (args [i])) {				
				String facet = args [++ i];				
				String [] parts = facet.split (":");
				
				if (parts.length == 2) {
					facets.add (new AbstractMap.SimpleEntry <String, String> (parts [0], parts [1]));
				} else {
					System.err.println ("Failed to parse facet " + facet);
				}
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
			Searcher searcher = getSearcher (index, tax_dirname, null);

			if (searcher != null) {
				Query q = null;
				
				if (!queries.isEmpty ()) {					
					q = QueryUtil.buildGrassrootsQuery (queries);
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
							List <FacetResult> facet_results = searcher.getAllFacets (q, hits_per_page);
							
							if (facet_results != null) {
								searcher.addFacetResults (facet_results, json_res);
							}							
						}
						break;
						
						case "facets-only": {
							List <FacetResult> facet_results = searcher.facetsOnlySearch (q, facets, hits_per_page);
							
							if (facet_results != null) {
								searcher.addFacetResults (facet_results, json_res);
							}
						}
						break;

						case "drill-down": {
							DrillDownData results = null;
							
							try {
								results = searcher.drillDown (q, facets, hits_per_page, page);
							} catch (IOException e) {
								System.err.println ("standardSearch failed: " + q.toString () + " e: " + e);
							}
							
						
							if (results != null) {
								searcher.addHitsToJSON (results.ddd_hits, json_res);
								
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
								results = searcher.drillSideways (q, facets, hits_per_page);
							} catch (IOException e) {
								System.err.println ("drillSideways failed: " + q.toString () + " e: " + e);
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

	
	private void addHitsToJSON (List <Document> docs, JSONObject results) {
		if (docs != null) {
			JSONArray docs_array = new JSONArray ();

			for (Document doc : docs) {	
				JSONObject doc_json = getLuceneDocumentAsJSON (doc);
				docs_array.add (doc_json);			
			}

			results.put ("documents", docs_array);
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
			System.err.println ("getAllFacets failed for query : " + q.toString () + " e: " + e);
			return null;
		}

		return results;
	}

		  
	/** User runs a query and counts facets only without collecting the matching documents.*/
	public List <FacetResult> facetsOnlySearch (Query q, List <AbstractMap.SimpleEntry <String, String>> facets, int max_num_facets) {
		List <FacetResult> results = null;
		IndexSearcher searcher = new IndexSearcher (se_index_reader);
		FacetsCollector fc = new FacetsCollector();

		// MatchAllDocsQuery is for "browsing" (counts facets
		// for all non-deleted docs in the index); normally
		// you'd use a "normal" query:
		
		if (q == null) {
			q = new MatchAllDocsQuery ();
		}

		Facets facet_counts = null;

		try {
			FacetsCollector.search (searcher, q, max_num_facets, fc);
			facet_counts = new FastTaxonomyFacetCounts (se_taxonomy_reader, se_config, fc);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
				
		if ((facets != null) && (!facets.isEmpty ())) {
			// Retrieve results
			results = new ArrayList <FacetResult> ();

		    if ((facets != null) && (!facets.isEmpty ())) {
		    	for (AbstractMap.SimpleEntry <String, String> facet : facets) {
		    		FacetResult fr = null;
		    		
					try {
						fr = facet_counts.getTopChildren (max_num_facets, facet.getKey ());
					} catch (IOException e) {
						System.err.println ("Failed to get facte count for " + facet.getKey () + " e: " + e);
					}
		    		
		    		if (fr != null) {
		    			results.add (fr);	    		
		    		}
		    	}
		    }
			
		}		

		return results;
	}


	  /** 
	   * Drill down on a given facet and return all hits and facets
	   */
	  public DrillSidewaysData drillSideways (Query base_query, List <AbstractMap.SimpleEntry <String, String>> facets, int max_num_facets) throws IOException {
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
	    if (facets != null) {
	    	for (AbstractMap.SimpleEntry <String, String> facet : facets) {
		    	q.add (facet.getKey (), facet.getValue ());	    		
	    	}
	    }
	    
	    DrillSideways ds = new DrillSideways(searcher, se_config, se_taxonomy_reader);
	    DrillSidewaysResult result = ds.search (q, max_num_facets);

	    // Retrieve results
	    List <FacetResult> facet_results = result.facets.getAllDims (max_num_facets);

	    
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
		search_results.dsd_facets = facet_results;
		
		return search_results;
	  }
	
	  
	  
	  /** User drills down on a facet, and we
	   *  return another facets for  */
	  public DrillDownData drillDown (Query base_query,  List <AbstractMap.SimpleEntry <String, String>> facets, int hits_per_page, int page_number) throws IOException {
	    IndexSearcher searcher = new IndexSearcher (se_index_reader);
		FacetsCollector fc = new FacetsCollector ();
		final int MAX_NUM_RESULTS = 1024;
		List <FacetResult> all_facets = null;
		Analyzer analyzer = QueryUtil.getAnalyzer ();
		
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
	    if (facets != null) {
	    	for (AbstractMap.SimpleEntry <String, String> facet : facets) {
		    	q.add (facet.getKey (), facet.getValue ());	    		
	    	}
	    }

	    
	    TopDocs resultDocs = FacetsCollector.search (searcher, q, MAX_NUM_RESULTS, fc);

	    List <FacetsCollector.MatchingDocs> matching_docs = fc.getMatchingDocs ();
	    
	    // Retrieve facets
	    Facets facet_counts = new FastTaxonomyFacetCounts (se_taxonomy_reader, se_config, fc);

    	all_facets = facet_counts.getAllDims (MAX_NUM_RESULTS);
    	
    	if (all_facets != null) {
    		int num_facets = all_facets.size ();
			System.out.println ("all_facets size" + num_facets);	
    	} else {
			System.out.println ("all_facets is null");		    		
    	}
	    
	    if ((facets != null) && (!facets.isEmpty ())) {
	    	for (AbstractMap.SimpleEntry <String, String> facet : facets) {
	    		FacetResult fr = facet_counts.getTopChildren (hits_per_page, facet.getKey ());
	    		
	    		if (fr != null) {
	    			all_facets.add (fr);	    		
	    		}
	    	}
	    }
	    
	    	    	    
	    // Retrieve results
		ScoreDoc [] hits = resultDocs.scoreDocs;
		int total_hits = Searcher.CastLongToInt (resultDocs.totalHits.value);

		Map <String, String []> highlights = QueryUtil.GetHighlightingData (base_query, searcher, se_index_reader, analyzer, resultDocs);

		
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
				
				/*
				Iterator <IndexableField> fields = doc.iterator ();
				
				while (fields.hasNext ()) {
					IndexableField field = fields.next ();
					
					String [] values = highlights.get (field.name ());
					
					if (values != null) {
						
					}
				}
				*/
				
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
		return QueryUtil.search (query, se_index_reader, max_num_hits);
	}
	
	
	public JSONObject getLuceneDocumentAsJSON (Document doc) {
		JSONObject res = new JSONObject ();
		String service = doc.get ("service");
		
		if (service != null) {
			switch (service) {
				case "BlastN":
				case "BlastX":
				case "BlastP":
					/*
					 * Convert the payload field to a full JSON object
					 */
					IndexableField field = doc.getField ("payload");
					
					if (field != null) {
						String payload = field.stringValue ();
						JSONParser parser = new JSONParser ();
						Object o = null;
						
						try {
							o = parser.parse (payload);
						} catch (org.json.simple.parser.ParseException e) {
							e.printStackTrace();
						}
						
						if (o != null) {
							JSONObject payload_json = (JSONObject) o;
							
							res.put ("payload", payload_json);
						}
						
						doc.removeField ("payload");
					}
				break;
				
				default:
				break;
			}	
		}

		/*
		 * Group the duplicated fields together
		 */
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

	static private String [] getFacets (String facet_str) {
		String [] facets = facet_str.split (",");
		
		if (facets != null) {
			for (int i = facets.length - 1; i >= 0; -- i) {
				facets [i] = facets [i].trim ();
			}
		}
		
		return facets;
	}	
}
