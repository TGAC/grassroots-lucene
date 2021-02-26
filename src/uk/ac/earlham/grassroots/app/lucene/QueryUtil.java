package uk.ac.earlham.grassroots.app.lucene;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.BoostQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.uhighlight.UnifiedHighlighter;
import org.apache.lucene.search.BooleanClause;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import uk.ac.earlham.grassroots.document.json.GrassrootsJSON;
import uk.ac.earlham.grassroots.document.lucene.AddressDocument;
import uk.ac.earlham.grassroots.document.lucene.FieldTrialDocument;
import uk.ac.earlham.grassroots.document.lucene.GrassrootsDocument;
import uk.ac.earlham.grassroots.document.lucene.GrassrootsDocumentFactory;
import uk.ac.earlham.grassroots.document.lucene.ProgrammeDocument;
import uk.ac.earlham.grassroots.document.lucene.ProjectDocument;
import uk.ac.earlham.grassroots.document.lucene.StudyDocument;
import uk.ac.earlham.grassroots.document.lucene.MeasuredVariableDocument;


public class QueryUtil {
	private static Analyzer qu_analyzer = null;
	private static Pattern qu_highlighted_pattern = null;
	
	public static Analyzer getAnalyzer () {
		if (qu_analyzer == null) {
			qu_analyzer = new StandardAnalyzer ();
		}
		
		return qu_analyzer;
	}

	public static Pattern getHighlightedPattern () {
		if (qu_highlighted_pattern == null) {
			qu_highlighted_pattern = Pattern.compile ("<b>(\\S+)</b>");
		}
		
		return qu_highlighted_pattern;
	}

	
	private static void getFields (List <String> fields, Map <String, Float> boosts, Map <String, String> string_fields) {
		GrassrootsDocument.addQueryTerms (fields, boosts, string_fields);		
		AddressDocument.addQueryTerms (fields, boosts, string_fields);
		FieldTrialDocument.addQueryTerms (fields, boosts, string_fields);
		ProjectDocument.addQueryTerms (fields, boosts, string_fields);
		StudyDocument.addQueryTerms (fields, boosts, string_fields);
		MeasuredVariableDocument.addQueryTerms (fields, boosts, string_fields);	
		ProgrammeDocument.addQueryTerms (fields, boosts, string_fields);		
	}
	
	
	private static void addTermQueryToBuilder (String field, String query, Float boost, BooleanQuery.Builder query_builder) {
		Query q;
		TermQuery term_query = new TermQuery (new Term (field, query));
		
		if (boost != null) {
			q = new BoostQuery (term_query, boost);
		} else {
			q = term_query;
		}

		query_builder.add (q, BooleanClause.Occur.SHOULD);
	}
	
	private static void addPhraseQueryToBuilder (String field, String query, Float boost, BooleanQuery.Builder query_builder) {
		Query q;
		PhraseQuery phrase_query = new PhraseQuery (field, query);

		if (boost != null) {
			q = new BoostQuery (phrase_query, boost);
		} else {
			q = phrase_query;
		}

		query_builder.add (q, BooleanClause.Occur.SHOULD);
	}
	

	public static Query buildGrassrootsQueryUsingParser (List <String> queries) {
		Query query = null;		
		
		List <String> fields = new ArrayList <String> ();
		Map <String, Float> boosts = new HashMap <String, Float> ();
		Map <String, String> string_fields = new HashMap <String, String> ();

		getFields (fields, boosts, string_fields);
		
		StringBuilder sb = new StringBuilder ();

		for (String q : queries) {
			boolean phrase_query_flag = q.contains (" ");
			
			for (String field : fields) {
				boolean string_field_flag = string_fields.containsKey (field);
				String escaped_field;
				Float boost = boosts.get (field);

				if (boost != null) {
					sb.append ("(");
				}
				
				if (field.contains (":")) {
					escaped_field = field.replaceAll (":", "\\\\:");					
				} else {
					escaped_field = field;
				}
					 
					
								
				if (phrase_query_flag && !string_field_flag) {
					sb.append (escaped_field);
					sb.append (":");					
					sb.append ("\"");
					sb.append (q);
					sb.append ("\"");				
				} else {
					String [] subqueries = q.split (" ");

					for (String subquery : subqueries) {
						sb.append (escaped_field);
						sb.append (":");					
						sb.append (subquery);
						sb.append (" ");					
					}
					
					
//					sb.append (q);
				}

				if (boost != null) {
					sb.append (")^");
					sb.append (boost);
				}

				sb.append (" ");
			}			

		}

		QueryParser parser = new QueryParser ("", getAnalyzer ());
		String raw_query = sb.toString ();

		System.out.println ("raw query: " + raw_query);		

		try {
			query = parser.parse (raw_query);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		if (query != null) {
			System.out.println ("parsed query: " + query.toString ());		
		}
		
		return query;
	}
	
	
	public static void PrintBooleanQuery (BooleanQuery query) {
		List <BooleanClause> clauses = query.clauses ();
		int i = 0;
		
		for (BooleanClause clause : clauses) {
			Query clause_query = clause.getQuery ();
			String field = null;
			Term [] terms_array = new Term [1];
			Term [] terms = null;
				
			if (clause_query instanceof BoostQuery) {
				clause_query = ((BoostQuery) clause_query).getQuery ();
			}
			
			if (clause_query instanceof PhraseQuery) {
				PhraseQuery pq = (PhraseQuery) clause_query;
				
				field = pq.getField ();
				terms =	pq.getTerms ();
			} else if (clause_query instanceof TermQuery) {
				TermQuery tq = (TermQuery) clause_query;
				
				Term term = tq.getTerm ();
				field = term.field ();

				terms_array [0] = term;
				terms = terms_array;
			} else {
				System.out.println ("query is a " + clause_query.getClass ().getSimpleName ());
			}

			System.out.print ("i: " + i + ", " + clause_query.getClass ().getSimpleName () + ": ");

			if (terms != null) {
				System.out.print (" field: " + field + " terms: ");
				for (Term term : terms) {
					System.out.print ("\"" + term.text () + "\", ");				
				}
			}
			
			System.out.println ("");
			
			++ i;
		}
	}
	
	
	public static Query buildGrassrootsQuery (List <String> queries) {
		Query query = null;		
		
		List <String> fields = new ArrayList <String> ();
		Map <String, Float> boosts = new HashMap <String, Float> ();
		Map <String, String> string_fields = new HashMap <String, String> ();

		getFields (fields, boosts, string_fields);
		
//		String [] fields_array = fields.toArray (new String [0]);
//		QueryParser parser = new MultiFieldQueryParser (fields_array, getAnalyzer (), boosts);

		BooleanQuery.Builder query_builder = new BooleanQuery.Builder ();
		
		for (String query_str : queries) {
			for (String field : fields) {
				
				Float boost = boosts.get (field);

				if (query_str.contains (" ")) {
					if (string_fields.containsKey (field)) {
						addTermQueryToBuilder (field, query_str, boost, query_builder);	
					} else {
						StringBuilder sb = new StringBuilder ();
						
						sb.append (query_str);
						
						addPhraseQueryToBuilder (field, sb.toString (), boost, query_builder);						
					}
				} else {
					addTermQueryToBuilder (field, query_str, boost, query_builder);
				}
			}			

		}
		
		query = query_builder.build ();
		
		if (query != null) {
			System.out.println ("parsed query: " + query.toString ());		
		}
		
		return query;
	}
	
	public static JSONArray search (Query query, IndexReader reader, IndexSearcher searcher) throws IOException {
		return search (query, reader, searcher, reader.numDocs ());
	}
	
	public static JSONArray search (Query query, IndexReader reader, IndexSearcher searcher, int max_num_hits) throws IOException {
		JSONArray docs = new JSONArray ();
		TopDocs results = searcher.search (query, max_num_hits);
		ScoreDoc [] hits = results.scoreDocs;

		int num_total_hits = Searcher.CastLongToInt (results.totalHits.value);
		int limit = Math.min (num_total_hits, hits.length);
		

		Map <String, String []> highlights = GetHighlightingData (query, searcher, reader, getAnalyzer (), results);

		
		for (int i = 0; i < limit; ++ i) {
			ScoreDoc score_doc = hits [i];
			Document doc = searcher.doc (score_doc.doc);
			
			GrassrootsJSON json_doc = GrassrootsDocumentFactory.getJSON (doc, highlights, i);

			if (json_doc != null) {
				JSONObject json = json_doc.getAsJSON ();
				docs.add (json);
			}
		}
					
		return docs;
	}
	
	
	
	static public String getHighlightedFieldValue (String field_name, Map <String, String []> highlights, ScoreDoc [] docs, int index) {
		String highlighted_value = null;
		String [] values = highlights.get (field_name);
		
		if (values != null) {
			if (index < values.length) {
				String value = values [index];
				
				if (value != null) {
					Pattern p = getHighlightedPattern ();					
		    		Matcher matcher = p.matcher (value);
		    		
		    		if (matcher.find ()) {
		    			highlighted_value = value;
		    		}
				}
			}
		}
				
		return highlighted_value;
	}

	
	static public Map <String, String []> GetHighlightingData (Query query, IndexSearcher searcher, IndexReader reader, Analyzer analyzer, TopDocs hits) {
		UnifiedHighlighter highlighter = new UnifiedHighlighter (searcher, analyzer);        
		List <String> fields = new ArrayList <String> ();
		
		getFields (fields, null, null);
		
		String [] fields_array = (String []) fields.toArray (new String [0]);
	
		Map <String, String []> highlights = null;
	
		try {
			highlights = highlighter.highlightFields (fields_array, query, hits);
		} catch (IOException e) {
			System.err.println ("Failed to get highlighted fields for query \"" + query + "\", exception: "+ e);
		}
	
		return highlights;
	}
	

	
	static public void DoUnifiedHighlighting (Query query, IndexSearcher searcher, IndexReader reader, Analyzer analyzer, TopDocs hits) {
		Map <String, String []> highlights = GetHighlightingData (query, searcher, reader, analyzer, hits);	
		
		if (highlights != null) {
			ScoreDoc [] docs = hits.scoreDocs;
			
			String [] keys = highlights.keySet ().toArray (new String [0]);
			
			Arrays.sort (keys);
			
			final int num_keys = keys.length;

		    Pattern pattern = Pattern.compile ("<b>(\\S+)</b>");
			
			for (int i = 0; i < num_keys; ++ i) {
			    System.out.println (keys [i] + ":");
			    String [] values = highlights.get (keys [i]);

			    
			    int j = 0;
			    
			    for (String value: values) {
		    		int doc_id = docs [j].doc;
		    		Document doc = null;
		    		
					try {
						doc = reader.document (doc_id);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					if (doc != null) {
			    		String name = doc.get (GrassrootsDocument.GD_UNIQUE_NAME);
			    						    	
				    	if (value != null) {
				    		Matcher matcher = pattern.matcher (value);
				    		
				    		
				    		if (matcher.find ()) {
				    			System.out.println ("\t MATCH doc [" + j + "] " + doc_id +  " - " + name + ": " + value);
				    		} else {
				    			System.out.println ("\t MISS  doc [" + j + "] " + doc_id +  " - " +name + ": " + value);			    			
				    		}
				    	} else {
				    		System.out.println ("\t EMPTY doc [" + j + "] " + doc_id +  " - " + name + ": null");
				    	}		
					}
					
			    	++ j;
			    }
			}
		}
		
	}
	
}
