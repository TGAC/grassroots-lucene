package uk.ac.earlham.grassroots.app.lucene;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.uhighlight.UnifiedHighlighter;

import uk.ac.earlham.grassroots.document.AddressDocument;
import uk.ac.earlham.grassroots.document.FieldTrialDocument;
import uk.ac.earlham.grassroots.document.GrassrootsDocument;
import uk.ac.earlham.grassroots.document.ProgrammeDocument;
import uk.ac.earlham.grassroots.document.ProjectDocument;
import uk.ac.earlham.grassroots.document.StudyDocument;
import uk.ac.earlham.grassroots.document.TreatmentDocument;

public class QueryUtil {
	private static Analyzer qu_analyzer = null;
	private static Pattern qu_highlighted_pattern = null;
	
	public static Analyzer getAnalyzer () {
		if (qu_analyzer == null) {
			qu_analyzer = new StandardAnalyzer (); // KeywordAnalyzer ();
		}
		
		return qu_analyzer;
	}

	public static Pattern getHighlightedPattern () {
		if (qu_highlighted_pattern == null) {
			qu_highlighted_pattern = Pattern.compile ("<b>(\\S+)</b>");
		}
		
		return qu_highlighted_pattern;
	}

	
	private static void getFields (List <String> fields, Map <String, Float> boosts) {
		GrassrootsDocument.addQueryTerms (fields, boosts);		
		AddressDocument.addQueryTerms (fields, boosts);
		FieldTrialDocument.addQueryTerms (fields, boosts);
		ProjectDocument.addQueryTerms (fields, boosts);
		StudyDocument.addQueryTerms (fields, boosts);
		TreatmentDocument.addQueryTerms (fields, boosts);	
		ProgrammeDocument.addQueryTerms (fields, boosts);		
	}
	
	
	public static Query buildGrassrootsQuery (List <String> queries) {
		Query q = null;		
		
		List <String> fields = new ArrayList <String> ();
		Map <String, Float> boosts = new HashMap <String, Float> ();
			
		getFields (fields, boosts);
		
		String [] fields_array = fields.toArray (new String [0]);
		QueryParser parser = new MultiFieldQueryParser (fields_array, getAnalyzer (), boosts);

		StringBuilder sb = new StringBuilder ();
		
		for (String query : queries) {
			sb.append (query);
			sb.append (" ");
		}
		
		System.out.println ("raw query: " + sb.toString ());		
		
		try {				
			q = parser.parse (sb.toString ());
		} catch (ParseException e) {
			System.err.println ("Failed to parse query \"" + q + "\", exception: "+ e);
		}

		System.out.println ("parsed query: " + q.toString ());		

		
		return q;
	}
	
	public static List <Document> search (Query query, IndexReader reader) throws IOException {
		return search (query, reader, reader.numDocs ());
	}
	
	public static List <Document> search (Query query, IndexReader reader, int max_num_hits) throws IOException {
		List <Document> docs = new ArrayList <Document> ();
		IndexSearcher searcher = new IndexSearcher (reader);
		TopDocs results = searcher.search (query, max_num_hits);
		ScoreDoc [] hits = results.scoreDocs;

		int num_total_hits = Searcher.CastLongToInt (results.totalHits.value);
		int limit = Math.min (num_total_hits, hits.length);
		
		
		Map <String, String []> highlights = GetHighlightingData (query, searcher, reader, qu_analyzer, results);

		
		for (int i = 0; i < limit; ++ i) {
			ScoreDoc score_doc = hits [i];
			Document doc = searcher.doc (score_doc.doc);
			
			if (highlights != null) {
				
			}
			
			docs.add (doc);
		}
		
		
		DoUnifiedHighlighting (query, searcher, reader, qu_analyzer, results);
				
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
		
		getFields (fields, null);
		
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

			for (int i = 0; i < num_keys; ++ i) {
			    System.out.println (keys [i] + ":");
			    String [] values = highlights.get (keys [i]);

			    Pattern pattern = Pattern.compile ("<b>(\\S+)</b>");
			    
			    int j = 0;
			    
			    for (String value: values) {
			    	
			    	if (value != null) {
			    		Matcher matcher = pattern.matcher (value);
			    		
			    		if (matcher.find ()) {
			    			System.out.println ("\t MATCH doc [" + j + "] " + docs [j] + ": " + value);
			    		} else {
			    			System.out.println ("\t MISS  doc [" + j + "] " + docs [j] + ": " + value);			    			
			    		}
			    	} else {
			    		System.out.println ("\t EMPTY doc [" + j + "] " + docs [j] + ": null");
			    	}		
			    	
			    	++ j;
			    }
			}
		}
		
	}
	
}
