package uk.ac.earlham.grassroots.app.lucene;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;

import uk.ac.earlham.grassroots.document.AddressDocument;
import uk.ac.earlham.grassroots.document.FieldTrialDocument;
import uk.ac.earlham.grassroots.document.GrassrootsDocument;
import uk.ac.earlham.grassroots.document.ProjectDocument;
import uk.ac.earlham.grassroots.document.StudyDocument;
import uk.ac.earlham.grassroots.document.TreatmentDocument;

public class QueryUtil {
	private static Analyzer qu_analyzer;
	
	public static Analyzer getAnalyzer () {
		if (qu_analyzer == null) {
			qu_analyzer = new KeywordAnalyzer ();
		}
		
		return qu_analyzer;
	}
	
	public static Query buildGrassrootsQuery (List <String> queries) {
		Query q = null;		
		
		List  <String>  fields = new ArrayList <String> ();
		Map <String, Float> boosts = new HashMap <String, Float> ();
		
	
		GrassrootsDocument.addQueryTerms (fields, boosts);		
		AddressDocument.addQueryTerms (fields, boosts);
		FieldTrialDocument.addQueryTerms (fields, boosts);
		ProjectDocument.addQueryTerms (fields, boosts);
		StudyDocument.addQueryTerms (fields, boosts);
		TreatmentDocument.addQueryTerms (fields, boosts);
		
		String [] fields_array = fields.toArray (new String [0]);
		QueryParser parser = new MultiFieldQueryParser (fields_array, getAnalyzer (), boosts);

			
		StringBuilder sb = new StringBuilder ();
		
		for (String query : queries) {
			sb.append (query);
			sb.append (" ");
		}
		
		System.out.println ("query: " + sb.toString ());		
		
		try {				
			q = parser.parse (sb.toString ());
		} catch (ParseException e) {
			System.err.println ("Failed to parse query \"" + q + "\", exception: "+ e);
		}
		
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
		
		for (int i = 0; i < limit; ++ i) {
			Document doc = searcher.doc (hits [i].doc);
			docs.add (doc);
		}
		
		return docs;
	}
	
	public static void AddStringsToQuery (StringBuilder sb, List <String> terms, boolean quote_flag) {
		final float NAME_BOOST = 5.0f;
		final float DESCRIPTION_BOOST = 3.0f;
		final float DEFAULT_BOOST = 1.0f;

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
				buildQuery (sb, GrassrootsDocument.GD_STRING_SEARCH_KEY, s, DEFAULT_BOOST);
				
				sb.append (" \"");
				sb.append (s);
				sb.append ("\")");				
			}
			
		}
	
	}
	

	public static void buildQuery (StringBuilder sb, String key, String value) {
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
		
	}

	public static void buildQuery (StringBuilder sb, String key, String value, float boost) {
		buildQuery (sb, key, value);
		
		if (boost != 1.0f) {
			sb.append (")^");
			sb.append (boost);		
		}
	}
	
}
