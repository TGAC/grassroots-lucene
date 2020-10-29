package uk.ac.earlham.grassroots.app.lucene;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.Query;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;


public class Deleter {
	
	public Deleter () {
	}
	
	public static void main (String[] args) {
		String usage =
				"Usage:\tjava uk.ac.earlham.grassroots.app.lucene.Deleter [-index dir] [-query string]";
		if (args.length > 0 && ("-h".equals(args[0]) || "-help".equals(args[0]))) {
			System.out.println (usage);
			System.exit(0);
		}

		String index = null;
		String tax_dirname = null;
		List <String> queries = new ArrayList <String> ();
		PrintStream output_stm = System.out;
		
		
		for (int i = 0; i < args.length; ++ i) {
			if ("-index".equals (args [i])) {
				index = args [++ i];
			} else if ("-query".equals (args [i])) {
				
				while (++ i < args.length) {
					queries.add (args [i]);
				}
			}
		}
		
		if (index != null) {			
			Query q = null;

			if (!queries.isEmpty ()) {					
				q = QueryUtil.buildGrassrootsQuery (queries);

				if (q != null) {
					Deleter deleter = new Deleter ();
				
					if (!deleter.run (q, index)) {
						
					}
				}
			} 
	
		}
	}

	
	public boolean run (Query query, String index_dir_name) {
		boolean success_flag = false;
		IndexWriter index_writer = null;
		
		
		try {
			index_writer = getIndexWriter (index_dir_name);
		} catch (IOException ioe) {
			System.err.println ("getIndexWriter (): " + ioe.getMessage());	
		}
		
		if (index_writer != null) {
			try {
				index_writer.deleteDocuments (query);
			    success_flag = true;
			} catch (CorruptIndexException cie) {
				System.err.println ("index_writer.deleteDocuments (): " + cie.getMessage());			    													
			} catch (IOException ioe) {
				System.err.println ("index_writer.deleteDocuments (): " + ioe.getMessage());			    													
			}

			if (success_flag) {
				try {
				    index_writer.close ();
				} catch (IOException ioe) {
				    success_flag = false;
					System.err.println ("index_writer.close (): " + ioe.getMessage());			    													
				}
			}
		}
		
		return success_flag;
	}
	
	
	private IndexWriter getIndexWriter (String index_dir_name) throws IOException {
		Directory index_dir = FSDirectory.open (Paths.get (index_dir_name));	
		Analyzer analyzer = new StandardAnalyzer ();
		IndexWriterConfig iwc = new IndexWriterConfig (analyzer);
		IndexWriter index_writer = new IndexWriter (index_dir, iwc);
		
		return index_writer;
	}
}
